package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * For Queue-ing up modifications to collections so that the modifications can
 * be applied only if and when a collection property is accessed through a
 * getter. This is to avoid elements causing their collector to potentially
 * expensively load all its sister elements when bi-directionally setting a
 * link.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class ElementQueue<E> {
    private final Map<Property<?, ? super E>, List<Action>> map = new HashMap<Property<?, ? super E>, List<Action>>();
    private final ModelWrapper<E> wrapper;

    public ElementQueue(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Adds actions/ commands to remember to add the given {@link ModelWrapper}
     * elements to the given collection {@link Property} when it is loaded.
     * 
     * @param <W> wrapper class
     * @param <M> model class wrapped by {@link W}
     * @param property
     * @param elements
     */
    public <W extends ModelWrapper<? extends M>, M> void add(
        Property<? extends Collection<? extends M>, ? super E> property,
        Collection<W> elements) {
        List<Action> actions = getActions(property);
        for (W element : elements) {
            actions.add(new Action(Action.Type.ADD, element));
        }
    }

    /**
     * Adds actions/ commands to remember to remove the given
     * {@link ModelWrapper} elements to the given collection {@link Property}
     * when it is loaded.
     * 
     * @param <W> wrapper class
     * @param <M> model class wrapped by {@link W}
     * @param property
     * @param wrappers
     */
    public <W extends ModelWrapper<? extends M>, M> void remove(
        Property<? extends Collection<? extends M>, ? super E> property,
        Collection<W> wrappers) {
        List<Action> actions = getActions(property);
        for (W wrapper : wrappers) {
            actions.add(new Action(Action.Type.REMOVE, wrapper));
        }
    }

    /**
     * Flushes the {@link List} of {@link Action}-s for the given collection
     * {@link Property} into the given {@link Collection}. That is, perform the
     * queued up actions (e.g. add elements, delete elements) then clear the
     * action queue.
     * 
     * @param <W> wrapper class
     * @param <M> model class wrapped by {@link W}
     * @param property
     */
    public <W extends ModelWrapper<? extends M>, M> void flush(
        Property<? extends Collection<? extends M>, ? super E> property) {
        @SuppressWarnings("unchecked")
        Collection<W> collection = (Collection<W>) wrapper
            .recallProperty(property);

        List<Action> actions = getActions(property);
        for (Action action : actions) {
            @SuppressWarnings("unchecked")
            W wrapper = (W) action.wrapper;

            if (action.type == Action.Type.ADD) {
                // remove then add in case the element already exists
                collection.remove(wrapper);
                collection.add(wrapper);
            } else if (action.type == Action.Type.REMOVE) {
                collection.remove(action.wrapper);
            }
        }

        map.remove(property);
    }

    /**
     * Clear all queues.
     */
    public void clear() {
        map.clear();
    }

    private List<Action> getActions(Property<?, ? super E> property) {
        List<Action> queue = map.get(property);

        if (queue == null) {
            queue = new ArrayList<Action>();
            map.put(property, queue);
        }

        return queue;
    }

    public static class Action {
        public enum Type {
            ADD, REMOVE;
        }

        public final Type type;
        public final ModelWrapper<?> wrapper;

        public Action(Type type, ModelWrapper<?> wrapper) {
            this.type = type;
            this.wrapper = wrapper;
        }
    }
}
