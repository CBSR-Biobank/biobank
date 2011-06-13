package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

import org.hibernate.Session;

/**
 * Persist the wrapped object of the given {@code ModelWrapper<?>} on the
 * server. Also sets the given {@code ModelWrapper<?>}'s wrapped model object to
 * be the object result of the {@code SDKQueryResult}, when informed and
 * notifies listeners.
 * 
 * @author jferland
 * 
 */
public class PersistModelWrapperQueryTask<E> implements QueryTask {
    private final ModelWrapper<E> modelWrapper;

    public PersistModelWrapperQueryTask(ModelWrapper<E> modelWrapper) {
        this.modelWrapper = modelWrapper;
    }

    @Override
    public SDKQuery getSDKQuery() {
        return new PersistAction<E>(modelWrapper);
    }

    @Override
    public void afterExecute(SDKQueryResult result) {
        WrapperEventType eventType;
        if (modelWrapper.isNew()) {
            eventType = WrapperEventType.INSERT;
        } else {
            eventType = WrapperEventType.UPDATE;
        }

        // Do not want to clear caches as that will destroy the
        // wrapper-to-wrapper associations. Only property that might change is
        // the id (and version, which is not tracked). So, set the wrapped
        // object directly rather than through ModelWrapper.setWrappedObject().
        // WAIT. THIS IS SO BROKEN! :-(
        // WHAT IF WE DO A, oh, oops... what if we do a "NullAction" that just
        // resets to the new object that came back from the server?
        // @SuppressWarnings("unchecked")
        // E newWrappedObject = (E) result.getObjectResult();
        // E oldWrappedObject = modelWrapper.wrappedObject;
        // modelWrapper.wrappedObject = newWrappedObject;
        //
        // Property<? extends Integer, ? super E> idProperty = modelWrapper
        // .getIdProperty();
        // Integer newId = idProperty.get(newWrappedObject);
        // Integer oldId = idProperty.get(oldWrappedObject);
        // if (!newId.equals(oldId)) {
        // modelWrapper.propertyChangeSupport.firePropertyChange(
        // idProperty.getName(), oldId, newId);
        // }

        // setWrappedObject(modelWrapper, result.getObjectResult());

        WrapperEvent event = new WrapperEvent(eventType, modelWrapper);
        modelWrapper.notifyListeners(event);
    }

    private static <E> void setWrappedObject(ModelWrapper<E> modelWrapper,
        Object newModel) {
        Class<E> klazz = modelWrapper.getWrappedClass();
        E tmp = klazz.cast(newModel);
        modelWrapper.setWrappedObject(tmp);
    }

    /**
     * Persist the wrapped object of the given {@code ModelWrapper}. Determine
     * what to do (insert or update) on the server instead of the client.
     * 
     * @author jferland
     * 
     * @param <E>
     */
    private static class PersistAction<E> extends BiobankWrapperAction<E> {
        private static final long serialVersionUID = 1L;

        public PersistAction(ModelWrapper<E> wrapper) {
            super(wrapper);
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            E model = getModel();
            session.saveOrUpdate(model);
            return model;
        }
    }
}
