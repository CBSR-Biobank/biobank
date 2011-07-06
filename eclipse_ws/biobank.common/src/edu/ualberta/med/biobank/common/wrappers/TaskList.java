package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.wrappers.actions.DeleteRemovedAction;
import edu.ualberta.med.biobank.common.wrappers.tasks.InactiveQueryTask;
import edu.ualberta.med.biobank.common.wrappers.tasks.PreQueryTask;
import edu.ualberta.med.biobank.common.wrappers.tasks.QueryTask;
import gov.nih.nci.system.query.SDKQuery;

/**
 * Manages several {@link List}-s of tasks, such as {@link QueryTask}-s and
 * {@link PreQueryTask}-s used by a {@link ModelWrapperTransaciton} to persist
 * or delete a {@link ModelWrapper<?>}.
 * 
 * @author jferland
 * 
 * @see ModelWrapper
 * @see QueryTask
 * @see PreQueryTask
 * @see WrapperTransaction
 * 
 */
// TODO: only public because of the internal package for wrappers. What is that
// package necessary?
public class TaskList {
    private final Set<CascadeRecord> cascades = new HashSet<CascadeRecord>();
    private final LinkedList<QueryTask> queryTasks = new LinkedList<QueryTask>();
    private final List<PreQueryTask> preQueryTasks = new ArrayList<PreQueryTask>();

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
     * {@link InactiveQueryTask}) and add it to the end of the {@link QueryTask}
     * list.
     * 
     * @param task
     */
    public void add(SDKQuery query) {
        QueryTask task = new InactiveQueryTask(query);
        queryTasks.add(task);
    }

    /**
     * Add a {@link PreQueryTask} to the end of the {@link PreQueryTask} list.
     * 
     * @param task
     */
    public void add(PreQueryTask task) {
        preQueryTasks.add(task);
    }

    /**
     * Adds all of the given {@link TaskList}'s internal lists into this
     * {@link TaskList}.
     * 
     * @param list
     */
    public void add(TaskList list) {
        queryTasks.addAll(list.queryTasks);
        preQueryTasks.addAll(list.preQueryTasks);
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
     * Adds the tasks to persist a {@link ModelWrapper}'s {@link Property}. This
     * is only done if the given {@link Property} has been set (even if it has
     * only been loaded), even if it has not been modified.
     * 
     * @param wrapper which has the property
     * @param property to persist
     */
    public <M, P> void persist(ModelWrapper<M> wrapper, Property<P, M> property) {
        if (recordCascade(wrapper, property))
            return;

        // If the property is initialised (in the wrapped object) but not in the
        // wrapper, then persist it anyways since the data will be sent anyways
        // and a Hibernate cascade may take place and we may as well be aware of
        // it.
        if (wrapper.isPropertyCached(property)
            || wrapper.isInitialized(property)) {
            if (property.isCollection()) {
                @SuppressWarnings("unchecked")
                Property<? extends Collection<P>, ? super M> tmp = (Property<? extends Collection<P>, ? super M>) property;
                Collection<ModelWrapper<P>> list = wrapper
                    .getWrapperCollection(tmp, null, false);
                for (ModelWrapper<?> wrappedProperty : list) {
                    wrappedProperty.addPersistTasks(this);
                }
            } else {
                ModelWrapper<P> wrapperProperty = wrapper.getWrappedProperty(
                    property, null);
                if (wrapperProperty != null) {
                    wrapperProperty.addPersistTasks(this);
                }
            }
        }
    }

    /**
     * Adds the tasks to delete a {@link ModelWrapper}'s {@link Property}. This
     * will load the {@link Property} to delete it, whether it has already been
     * loaded or not.
     * <p>
     * This method is <em>potentially network expensive</em> since it may
     * require information to be lazily loaded from the database when called.
     * Consider using {@link deleteRemovedUnchecked()} if checks and persists
     * are unnecessary.
     * 
     * @param wrapper which has the property
     * @param property to delete
     */
    public <M, P> void delete(ModelWrapper<M> wrapper, Property<P, M> property) {
        if (recordCascade(wrapper, property))
            return;

        if (property.isCollection()) {
            @SuppressWarnings("unchecked")
            Property<? extends Collection<P>, ? super M> tmp = (Property<? extends Collection<P>, ? super M>) property;
            Collection<ModelWrapper<P>> list = wrapper.getWrapperCollection(
                tmp, null, false);
            for (ModelWrapper<?> wrappedProperty : list) {
                wrappedProperty.addDeleteTasks(this);
            }
        } else {
            ModelWrapper<P> wrapperProperty = wrapper.getWrappedProperty(
                property, null);
            if (wrapperProperty != null) {
                wrapperProperty.addDeleteTasks(this);
            }
        }
    }

    /**
     * Adds tasks to persist all newly added elements in the collection for the
     * given {@link ModelWrapper}'s {@link Property}.
     * <p>
     * This is <em>dangerous</em> because if a property is added and modified
     * then <em>all</em> changes will be persisted, not just it being added to
     * the collection (e.g. if the name was changed, the name change will be
     * persisted as well).
     * 
     * @param wrapper which has the property
     * @param property to persist the added elements of
     */
    public <M, P> void persistAdded(ModelWrapper<M> wrapper,
        Property<? extends Collection<P>, M> property) {
        if (recordCascade(wrapper, property))
            return;

        Collection<ModelWrapper<P>> elements = wrapper.getElementTracker()
            .getAddedElements(property);
        for (ModelWrapper<P> element : elements) {
            element.addPersistTasks(this);
        }
    }

    /**
     * Adds tasks to persist all newly removed elements in the collection for
     * the given {@link ModelWrapper}'s {@link Property}.
     * 
     * @param wrapper which has the property
     * @param property to persist the removed elements of
     */
    public <M, P> void persistRemoved(ModelWrapper<M> wrapper,
        Property<? extends Collection<P>, M> property) {
        if (recordCascade(wrapper, property))
            return;

        Collection<ModelWrapper<P>> elements = wrapper.getElementTracker()
            .getRemovedElements(property);
        for (ModelWrapper<P> element : elements) {
            element.addPersistTasks(this);
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
        Property<? extends Collection<P>, M> property) {
        if (recordCascade(wrapper, property))
            return;

        Collection<ModelWrapper<P>> elements = wrapper.getElementTracker()
            .getRemovedElements(property);
        for (ModelWrapper<P> element : elements) {
            element.addDeleteTasks(this);
        }
    }

    /**
     * Deletes old values of the given property (that do not exist in the new
     * set of values) WITHOUT PERFORMING CHECKS (specifically, getDeleteTasks()
     * will not be called).
     * 
     * @see {@link DeleteRemovedAction}
     * @param property
     * @return
     */
    public <M, P> void deleteRemovedUnchecked(ModelWrapper<M> wrapper,
        Property<P, M> property) {
        if (!wrapper.isNew() && wrapper.isInitialized(property)) {
            add(new DeleteRemovedAction<M>(wrapper, property));
        }
    }

    //
    // END CASCADE METHODS
    //

    /**
     * Records a cascade for the given {@link ModelWrapper} and its
     * {@link Property}.
     * 
     * @param wrapper which made the cascade call
     * @param property of the wrapper to cascade
     * @return true if a cascade has already been recorded for the given
     *         {@link ModelWrapper} and its {@link Property}. Otherwise return
     *         false.
     */
    private boolean recordCascade(ModelWrapper<?> wrapper,
        Property<?, ?> property) {
        CascadeRecord record = new CascadeRecord(wrapper, property);
        if (cascades.contains(record)) {
            return true;
        } else {
            cascades.add(record);
            return false;
        }
    }

    private static final class CascadeRecord {
        private final ModelWrapper<?> wrapper;
        private final Property<?, ?> property;

        public CascadeRecord(ModelWrapper<?> wrapper, Property<?, ?> property) {
            this.wrapper = wrapper;
            this.property = property;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((property == null) ? 0 : property.hashCode());
            result = prime * result
                + ((wrapper == null) ? 0 : wrapper.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CascadeRecord other = (CascadeRecord) obj;
            if (property != other.property)
                return false;
            if (wrapper != other.wrapper)
                return false;
            return true;
        }
    }
}
