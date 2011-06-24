package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
}
