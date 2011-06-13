package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

import org.hibernate.Session;

/**
 * Delete the wrapped object of the given {@code ModelWrapper<?>} on the server.
 * Also sets the given {@code ModelWrapper<?>}'s wrapped model object to be the
 * object result of the {@code SDKQueryResult}, when informed and notifies
 * listeners.
 * 
 * @author jferland
 * 
 */
public class DeleteModelWrapperQueryTask<E> implements QueryTask {
    private final ModelWrapper<E> modelWrapper;

    public DeleteModelWrapperQueryTask(ModelWrapper<E> modelWrapper) {
        this.modelWrapper = modelWrapper;
    }

    @Override
    public SDKQuery getSDKQuery() {
        return new DeleteAction<E>(modelWrapper);
    }

    @Override
    public void afterExecute(SDKQueryResult result) {
        // setWrappedObject(modelWrapper, result.getObjectResult());

        // TODO: not sure this is necessary.
        modelWrapper.setId(null);

        WrapperEventType eventType = WrapperEventType.DELETE;
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
     * Delete the wrapped object of the given {@code ModelWrapper}. Necessary
     * because DeleteExampleQuery does not return the model object.
     * 
     * @author jferland
     * 
     * @param <E>
     */
    private static class DeleteAction<E> extends BiobankWrapperAction<E> {
        private static final long serialVersionUID = 1L;

        public DeleteAction(ModelWrapper<E> wrapper) {
            super(wrapper);
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            E model = getModel();
            session.delete(model);
            return model;
        }
    }
}
