package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
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

        setWrappedObject(modelWrapper, result.getObjectResult());

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
    private static class PersistAction<E> extends BiobankSearchAction<E> {
        private static final long serialVersionUID = 1L;

        public PersistAction(ModelWrapper<E> wrapper) {
            super(wrapper);
        }

        @Override
        public Object doAction(Session session)
            throws BiobankSessionActionException {
            E model = getModel();
            session.saveOrUpdate(model);
            return model;
        }
    }
}
