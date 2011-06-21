package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.query.SDKQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages several {@code List}-s of tasks, such as {@code QueryTask}-s and
 * {@code PreQueryTask}-s used by a {@code ModelWrapperTransaciton} to persist
 * or delete a {@code ModelWrapper<?>}.
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
     * Add a {@code QueryTask} to the end of the {@code QueryTask} list.
     * 
     * @param task
     */
    public void add(QueryTask task) {
        queryTasks.add(task);
    }

    /**
     * Wrap a {@code SDKQuery} in an actionless {@code QueryTask} (e.g.
     * {@code InactiveQueryTask}) and add it to the end of the {@code QueryTask}
     * list.
     * 
     * @param task
     */
    public void add(SDKQuery query) {
        QueryTask task = new InactiveQueryTask(query);
        queryTasks.add(task);
    }

    /**
     * Add a {@code PreQueryTask} to the end of the {@code PreQueryTask} list.
     * 
     * @param task
     */
    public void add(PreQueryTask task) {
        preQueryTasks.add(task);
    }

    /**
     * Adds all of the given {@code TaskList}'s internal lists into this
     * {@code TaskList}.
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
