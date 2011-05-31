package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import java.util.Collection;

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
        actions.add(new Action(Action.Type.PERSIST, wrapper));
    }

    public void delete(ModelWrapper<?> Wrapper) {
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

            allTasks.add(tasks);
        }

        allTasks.execute(service);
    }
}