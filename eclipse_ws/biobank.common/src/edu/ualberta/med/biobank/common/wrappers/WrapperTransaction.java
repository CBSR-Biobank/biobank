package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.loggers.LogGroup;
import edu.ualberta.med.biobank.common.wrappers.tasks.InactiveQueryTask;
import edu.ualberta.med.biobank.common.wrappers.tasks.PreQueryTask;
import edu.ualberta.med.biobank.common.wrappers.tasks.QueryTask;
import edu.ualberta.med.biobank.common.wrappers.tasks.RebindableWrapperQueryTask;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            PERSIST,
            DELETE;
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

    public void persist(Collection<? extends ModelWrapper<?>> wrappers) {
        // TODO: check that wrapper not already added?
        for (ModelWrapper<?> wrapper : wrappers) {
            persist(wrapper);
        }
    }

    public void delete(ModelWrapper<?> wrapper) {
        // TODO: check that wrapper not already added?
        actions.add(new Action(Action.Type.DELETE, wrapper));
    }

    public void delete(Collection<? extends ModelWrapper<?>> wrappers) {
        // TODO: check that wrapper not already added?
        for (ModelWrapper<?> wrapper : wrappers) {
            delete(wrapper);
        }
    }

    @Deprecated
    public void commit() throws BiobankException, ApplicationException {
        // don't build the TaskList until now because it may depend on the state
        // of the wrappers, which may have been changed before now, but after
        // being added to our list of actions.
        TaskList tasks = new TaskList();

        for (Action action : actions) {
            switch (action.type) {
            case PERSIST:
                action.wrapper.addPersistAndLogTasks(tasks);
                break;
            case DELETE:
                action.wrapper.addDeleteAndLogTasks(tasks);
            }
        }

        addRebindTask(tasks);

        execute(tasks);
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

    private void addRebindTask(TaskList tasks) {
        Set<ModelWrapper<?>> wrappers = new HashSet<ModelWrapper<?>>();
        for (QueryTask task : tasks.getQueryTasks()) {
            if (task instanceof RebindableWrapperQueryTask) {
                ModelWrapper<?> wrapper = ((RebindableWrapperQueryTask) task)
                    .getWrapperToRebind();
                wrappers.add(wrapper);
            }
        }

        tasks.add(new RebindWrappersQueryTask(wrappers));
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

    /**
     * Manages several {@link List}-s of tasks, such as {@link QueryTask}-s and
     * {@link PreQueryTask}-s used by a {@link ModelWrapperTransaction} to
     * persist or delete a {@link ModelWrapper<?>}.
     * 
     * @author jferland
     * 
     * @see ModelWrapper
     * @see QueryTask
     * @see PreQueryTask
     * @see WrapperTransaction
     * 
     */
    public static class TaskList {
        private final Map<ModelWrapper<?>, ModelWrapper<?>> cascaded =
            new IdentityHashMap<ModelWrapper<?>, ModelWrapper<?>>();
        private final LinkedList<QueryTask> queryTasks =
            new LinkedList<QueryTask>();
        private final List<PreQueryTask> preQueryTasks =
            new ArrayList<PreQueryTask>();
        private final LogGroup logGroup;

        private TaskList() {
            this(new LogGroup());
        }

        private TaskList(LogGroup logGroup) {
            this.logGroup = logGroup;
        }

        /**
         * Add a {@link QueryTask} to the end of the {@link QueryTask} list.
         * 
         * @param task
         */
        public void add(QueryTask task) {
            queryTasks.add(task);
        }

        /**
         * Wrap a {@link SDKQuery} in an actionless {@link QueryTask} (e.g.
         * {@link InactiveQueryTask}) and add it to the end of the
         * {@link QueryTask} list.
         * 
         * @param task
         */
        public void add(SDKQuery query) {
            QueryTask task = new InactiveQueryTask(query);
            queryTasks.add(task);
        }

        /**
         * Add a {@link PreQueryTask} to the end of the {@link PreQueryTask}
         * list.
         * 
         * @param task
         */
        public void add(PreQueryTask task) {
            preQueryTasks.add(task);
        }

        /**
         * Get the {@link LogGroup} that is associated with this transaction.
         * 
         * @return
         */
        public LogGroup getLogGroup() {
            return logGroup;
        }

        public List<QueryTask> getQueryTasks() {
            return queryTasks;
        }

        public List<PreQueryTask> getPreQueryTasks() {
            return preQueryTasks;
        }

        //
        // START CASCADE METHODS
        //

        /**
         * Adds the tasks to persist a {@link ModelWrapper}'s {@link Property}.
         * This is only done if the given {@link Property} has been set (even if
         * it has only been loaded), even if it has not been modified.
         * 
         * @param wrapper which has the property
         * @param property to persist
         */
        public <M, P> void persist(ModelWrapper<M> wrapper,
            Property<P, M> property) {
            // If the property is initialised (in the wrapped object) but not in
            // the wrapper, then persist it anyways since the data will be sent
            // anyways and a Hibernate cascade may take place and we may as well
            // be aware of it.
            if (wrapper.isPropertyCached(property)
                || wrapper.isInitialized(property)) {
                if (property.isCollection()) {
                    @SuppressWarnings("unchecked")
                    Property<Collection<P>, ? super M> tmp =
                        (Property<Collection<P>, ? super M>) property;
                    Collection<ModelWrapper<P>> list = wrapper
                        .getWrapperCollection(tmp, null, false);
                    for (ModelWrapper<?> wrappedProperty : list) {
                        addPersistTasks(wrappedProperty);
                    }
                } else {
                    ModelWrapper<P> wrappedProperty = wrapper
                        .getWrappedProperty(property, null);
                    if (wrappedProperty != null) {
                        addPersistTasks(wrappedProperty);
                    }
                }
            }
        }

        /**
         * Adds the tasks to delete a {@link ModelWrapper}'s {@link Property}.
         * This will load the {@link Property} to delete it, whether it has
         * already been loaded or not.
         * <p>
         * This method is <em>potentially network expensive</em> since it may
         * require information to be lazily loaded from the database when
         * called. Consider using {@link deleteRemovedUnchecked()} if checks and
         * persists are unnecessary.
         * 
         * @param wrapper which has the property
         * @param property to delete
         */
        public <M, P> void delete(ModelWrapper<M> wrapper,
            Property<P, M> property) {
            if (property.isCollection()) {
                @SuppressWarnings("unchecked")
                Property<Collection<P>, ? super M> tmp =
                    (Property<Collection<P>, ? super M>) property;
                Collection<ModelWrapper<P>> list = wrapper
                    .getWrapperCollection(tmp, null, false);
                for (ModelWrapper<?> wrappedProperty : list) {
                    addDeleteTasks(wrappedProperty);
                }
            } else {
                ModelWrapper<P> wrappedProperty = wrapper.getWrappedProperty(
                    property, null);
                if (wrappedProperty != null) {
                    addDeleteTasks(wrappedProperty);
                }
            }
        }

        /**
         * Adds tasks to persist all newly added elements in the collection for
         * the given {@link ModelWrapper}'s {@link Property}.
         * <p>
         * This is <em>dangerous</em> because if a property is added and
         * modified then <em>all</em> changes will be persisted, not just it
         * being added to the collection (e.g. if the name was changed, the name
         * change will be persisted as well).
         * 
         * @param wrapper which has the property
         * @param property to persist the added elements of
         */
        public <M, P> void persistAdded(ModelWrapper<M> wrapper,
            Property<Collection<P>, M> property) {
            Collection<ModelWrapper<P>> elements = wrapper.getElementTracker()
                .getAddedElements(property);
            for (ModelWrapper<P> element : elements) {
                addPersistTasks(element);
            }
        }

        /**
         * Adds tasks to persist all newly removed elements in the collection
         * for the given {@link ModelWrapper}'s {@link Property}.
         * 
         * @param wrapper which has the property
         * @param property to persist the removed elements of
         */
        public <M, P> void persistRemoved(ModelWrapper<M> wrapper,
            Property<Collection<P>, M> property) {
            Collection<ModelWrapper<P>> elements = wrapper.getElementTracker()
                .getRemovedElements(property);
            for (ModelWrapper<P> element : elements) {
                addPersistTasks(element);
            }
        }

        /**
         * Adds tasks to remove elements from the given {@link ModelWrapper}'s
         * {@link Property} for a collection.
         * 
         * @param wrapper which has the property
         * @param property to persist the removed elements of
         */
        public <M, P> void deleteRemoved(ModelWrapper<M> wrapper,
            Property<Collection<P>, ? super M> property) {
            Collection<ModelWrapper<P>> elements = wrapper.getElementTracker()
                .getRemovedElements(property);
            for (ModelWrapper<P> element : elements) {
                addDeleteTasks(element);
            }
        }

        /**
         * Adds tasks to delete a removed wrapped property value from the given
         * {@link ModelWrapper}'s {@link Property}.
         * 
         * @param wrapper which has the property
         * @param property to delete
         */
        public <M, P> void deleteRemovedValue(ModelWrapper<M> wrapper,
            Property<P, M> property) {
            ModelWrapper<P> removedValue = wrapper.getElementTracker()
                .getRemovedValue(property);
            if (removedValue != null) {
                addDeleteTasks(removedValue);
            }
        }

        //
        // END CASCADE METHODS
        //

        @Deprecated
        private void addPersistTasks(ModelWrapper<?> wrapper) {
            if (!cascaded.containsKey(wrapper)) {
                cascaded.put(wrapper, wrapper);
                wrapper.addPersistAndLogTasks(this);
            }
        }

        @Deprecated
        private void addDeleteTasks(ModelWrapper<?> wrapper) {
            if (!cascaded.containsKey(wrapper)) {
                cascaded.put(wrapper, wrapper);
                wrapper.addDeleteAndLogTasks(this);
            }
        }
    }
}