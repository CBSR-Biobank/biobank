package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
import edu.ualberta.med.biobank.common.wrappers.tasks.RebindableWrapperQueryTask;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

import org.hibernate.Session;

/**
 * Delete the wrapped object of the given {@link ModelWrapper} on the server.
 * Also sets the given {@link ModelWrapper}'s wrapped model object to be the
 * object result of the {@link SDKQueryResult}, when informed and notifies
 * listeners.
 * 
 * @author jferland
 * 
 */
public class DeleteModelWrapperQueryTask<E> implements RebindableWrapperQueryTask {
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
        // TODO: not sure this is necessary.
        modelWrapper.setId(null);

        WrapperEventType eventType = WrapperEventType.DELETE;
        WrapperEvent event = new WrapperEvent(eventType, modelWrapper);
        modelWrapper.notifyListeners(event);
    }

    @Override
    public ModelWrapper<?> getWrapperToRebind() {
        return modelWrapper;
    }

    /**
     * Delete the wrapped object of the given {@link ModelWrapper}. Necessary
     * because {@link DeleteExampleQuery} does not return the model object.
     * 
     * @author jferland
     * 
     * @param <E>
     */
    private static class DeleteAction<E> extends WrapperAction<E> {
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
