package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.tasks.QueryTask;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;

/**
 * Maintains an internal list of actions to perform on given
 * {@code ModelWrapper<?>} objects, which can then be performed atomically (all
 * or nothing).
 * 
 * @author jferland
 * 
 */
public class WrapperTransaction {
    private final WritableApplicationService service;
    private final Collection<Action> actions;

    private static class Action {
        private enum Type {
            PERSIST, DELETE;
        }

        public final Type type;
        public final ModelWrapper<?> wrapper;

        Action(Type type, ModelWrapper<?> wrapper) {
            this.type = type;
            this.wrapper = wrapper;
        }
    }

    public WrapperTransaction(WritableApplicationService service) {
        this.service = service;
        this.actions = new ArrayList<Action>();
    }

    public void persist(ModelWrapper<?> wrapper) {
        // TODO: check that wrapper not already added?
        actions.add(new Action(Action.Type.PERSIST, wrapper));
    }

    public void delete(ModelWrapper<?> Wrapper) {
        // TODO: check that wrapper not already added?
        actions.add(new Action(Action.Type.DELETE, Wrapper));
    }

    public void commit() throws BiobankException, ApplicationException {
        // don't build the TaskList until now because it may depend on the state
        // of the wrappers, which may have been changed before now, but after
        // being added to our list of actions.
        TaskList allTasks = new TaskList();

        for (Action action : actions) {
            TaskList tasks = new TaskList();

            switch (action.type) {
            case PERSIST:
                tasks = action.wrapper.getPersistTasks();
                break;
            case DELETE:
                tasks = action.wrapper.getDeleteTasks();
            }

            tasks.add(new RebindWrappersQueryTask(action.wrapper));

            allTasks.add(tasks);
        }

        // TODO: add on a task to re-attach ModelWrapper-s to their wrapped
        // objects AND IF SO, DO NOT clear the property cache of wrappers.

        execute(allTasks);
    }

    public static void persist(ModelWrapper<?> wrapper,
        WritableApplicationService appService) throws BiobankException,
        ApplicationException {
        WrapperTransaction tx = new WrapperTransaction(appService);
        tx.persist(wrapper);
        tx.commit();
    }

    public static void delete(ModelWrapper<?> wrapper,
        WritableApplicationService appService) throws BiobankException,
        ApplicationException {
        WrapperTransaction tx = new WrapperTransaction(appService);
        tx.delete(wrapper);
        tx.commit();
    }

    /**
     * Execute the tasks in this {@code TaskList}.
     * 
     * @param service
     * @throws ApplicationException
     */
    private void execute(TaskList tasks) throws BiobankException,
        ApplicationException {
        executePreQueryTasks(tasks.getPreQueryTasks());
        executeQueryTasks(tasks.getQueryTasks());
    }

    private void executePreQueryTasks(List<PreQueryTask> preQueryTasks)
        throws BiobankException {
        for (PreQueryTask task : preQueryTasks) {
            task.beforeExecute();
        }
    }

    private void executeQueryTasks(List<QueryTask> queryTasks)
        throws ApplicationException {
        if (!queryTasks.isEmpty()) {
            List<SDKQuery> queries = new ArrayList<SDKQuery>();
            for (QueryTask task : queryTasks) {
                SDKQuery query = task.getSDKQuery();
                queries.add(query);
            }

            List<SDKQueryResult> results = service.executeBatchQuery(queries);

            int i = 0;
            for (QueryTask task : queryTasks) {
                SDKQueryResult result = results.get(i);
                task.afterExecute(result);
                i++;
            }
        }
    }

    // TODO: should share the map/ list for ALL wrappers?
    // TODO: move out!
    public static class RebindWrappersQueryTask implements QueryTask {
        private final ModelWrapper<?> wrapper;
        private final List<ModelWrapper<?>> cachedWrappers = new ArrayList<ModelWrapper<?>>();
        private final List<Object> wrappedObjects = new ArrayList<Object>();

        public RebindWrappersQueryTask(ModelWrapper<?> wrapper) {
            this.wrapper = wrapper;
            init();
        }

        @Override
        public SDKQuery getSDKQuery() {
            return new ReturnAction(wrapper, wrappedObjects);
        }

        @Override
        public void afterExecute(SDKQueryResult result) {
            // TODO: If we reattach here, no need to do so in Delete or
            // Persist!! :-)
            @SuppressWarnings("unchecked")
            List<Object> newObjects = (List<Object>) result.getObjectResult();
            for (int i = 0, n = cachedWrappers.size(); i < n; i++) {
                setWrappedObject(cachedWrappers.get(i), newObjects.get(i));
                // TODO: only thing that may have changed is the id, notify
                // listeners?
                // TODO: direct properties may have changed, but not
                // associations (e.g. label if container rename) so should do
                // some sort of notification.
            }
        }

        private static <E> void setWrappedObject(ModelWrapper<E> wrapper,
            Object object) {
            wrapper.wrappedObject = wrapper.getWrappedClass().cast(object);
        }

        private void init() {
            Map<ModelWrapper<?>, Object> map = new IdentityHashMap<ModelWrapper<?>, Object>();
            readCache(wrapper, map);

            for (Entry<ModelWrapper<?>, Object> entry : map.entrySet()) {
                cachedWrappers.add(entry.getKey());
                wrappedObjects.add(entry.getValue());
            }
        }

        private void readCache(ModelWrapper<?> wrapper,
            Map<ModelWrapper<?>, Object> map) {
            if (!map.containsKey(wrapper)) {
                map.put(wrapper, wrapper.wrappedObject);
                for (Object o : wrapper.propertyCache.values()) {
                    if (o instanceof Collection) {
                        for (Object e : (Collection<?>) o) {
                            if (e instanceof ModelWrapper<?>) {
                                readCache((ModelWrapper<?>) e, map);
                            }
                        }
                    } else if (o instanceof ModelWrapper<?>) {
                        readCache((ModelWrapper<?>) o, map);
                    }
                }
            }
        }

        /**
         * Simply returns the given action. This is useful because we can pass a
         * list of wrapped objects, then get "new" references back after
         * deserialization.
         * 
         * @author jferland
         * 
         */
        @SuppressWarnings("rawtypes")
        public static class ReturnAction extends WrapperAction {
            private static final long serialVersionUID = 1L;

            private Object result;

            @SuppressWarnings("unchecked")
            public ReturnAction(ModelWrapper<?> wrapper, Object result) {
                super(wrapper);
                this.result = result;
            }

            public void setResult(Object result) {
                this.result = result;
            }

            public Object getResult() {
                return result;
            }

            @Override
            public Object doAction(Session session)
                throws BiobankSessionException {
                return result;
            }
        }
    }
}