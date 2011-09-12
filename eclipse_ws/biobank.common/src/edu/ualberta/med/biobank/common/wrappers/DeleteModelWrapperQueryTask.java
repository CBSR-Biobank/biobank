package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
import edu.ualberta.med.biobank.common.wrappers.tasks.RebindableWrapperQueryTask;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

import java.io.Serializable;
import java.text.MessageFormat;

import org.hibernate.PropertyValueException;
import org.hibernate.Query;
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
public class DeleteModelWrapperQueryTask<E> implements
    RebindableWrapperQueryTask {
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
        private static final String HQL = "DELETE FROM {0} WHERE {1} = ?";

        public DeleteAction(ModelWrapper<E> wrapper) {
            super(wrapper);
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            E model = getModel();

            // Calling session.delete() does not sometimes work because it
            // null-checks values, and can fail. For example, trying to delete a
            // contact whose clinic value is null will not work. The values were
            // nulled by a bi-directional setter/ getter. Further, deleting with
            // HQL does not consider cascades, so, reload the object before
            // deleting it (see
            // https://forum.hibernate.org/viewtopic.php?f=1&t=944722)
            try {
                Serializable id = getIdProperty().get(model);
                session.delete(session.load(getModelClass(), id));
                // TODO: version check and throw a StaleStateException?
            } catch (PropertyValueException e) {
                // TODO: determine why the HQL delete is still necessary, test
                // fail if this is removed. Is there a better option?
                doHqlDelete(session);
            }

            // set the id to null so that the object is not loaded and so that
            // Hibernate won't do any cascades with it because it has no
            // identifier
            getIdProperty().set(model, null);

            return model;
        }

        private void doHqlDelete(Session session) {
            Integer id = getIdProperty().get(getModel());

            if (id != null) {
                String className = getModelClass().getName();
                String idPropName = getIdProperty().getName();
                String hql = MessageFormat.format(HQL, className, idPropName);

                Query query = session.createQuery(hql);
                query.setParameter(0, id);
                query.executeUpdate();
            }
        }
    }
}
