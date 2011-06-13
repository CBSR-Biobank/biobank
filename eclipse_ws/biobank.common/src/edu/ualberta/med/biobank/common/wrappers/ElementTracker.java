package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to {@code ModelWrapper} to track the original set of collection
 * {@code Property}-s (if they're about to change) so that it can be easily
 * determined later what the added and removed elements of the collection were,
 * if any. Tracks a single given {@code ModelWrapper}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
// TODO: should add a class to check that the elements that we think were added
// and removed were actually added and removed (as this could be wrong if the
// underlying model object was modified then given to the wrapper).
class ElementTracker<E> {
    private final Map<Property<? extends Collection<?>, ? super E>, Object> map = new HashMap<Property<? extends Collection<?>, ? super E>, Object>();
    private final ModelWrapper<E> wrapper;

    public ElementTracker(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * This method only does something the first time it is called for any given
     * property.
     * 
     * @param <T>
     * @param property
     */
    public <T> void track(Property<? extends Collection<T>, ? super E> property) {
        if (!map.containsKey(property)) {
            Collection<ModelWrapper<T>> originalValues = getCurrentValues(property);
            map.put(property, originalValues);
        }
    }

    /**
     * Gets a {@code Set} of all collection {@code Property}-s that were changed
     * through the associated {@code ModelWrapper}.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public Set<Property<? extends Collection<?>, ? super E>> getProperties() {
        return new HashSet<Property<? extends Collection<?>, ? super E>>(
            map.keySet());
    }

    /**
     * Gets a list of all {@code ModelWrapper}-ed elements that were added to
     * the any tracked collection {@code Property}.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public Collection<ModelWrapper<?>> getAllAddedElements() {
        Collection<ModelWrapper<?>> allAddedElements = new ArrayList<ModelWrapper<?>>();

        for (Property<? extends Collection<?>, ? super E> key : map.keySet()) {
            allAddedElements.addAll(getAddedElements(key));
        }

        return allAddedElements;
    }

    /**
     * Gets a list of {@code ModelWrapper}-ed elements that were added to the
     * given collection {@code Property}.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> Collection<ModelWrapper<T>> getAddedElements(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> addedElements = new ArrayList<ModelWrapper<T>>();

        if (map.containsKey(property)) {
            Collection<ModelWrapper<T>> originalValues = getOriginalValues(property);
            Collection<ModelWrapper<T>> newValues = getCurrentValues(property);

            addedElements.addAll(newValues);
            addedElements.removeAll(originalValues);
        }

        return addedElements;
    }

    /**
     * Gets a list of {@code ModelWrapper}-ed elements that were removed from
     * the given collection {@code Property}.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> Collection<ModelWrapper<T>> getRemovedElements(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> removedElements = new ArrayList<ModelWrapper<T>>();

        if (map.containsKey(property)) {
            Collection<ModelWrapper<T>> originalValues = getOriginalValues(property);
            Collection<ModelWrapper<T>> newValues = getCurrentValues(property);

            removedElements.addAll(originalValues);
            removedElements.removeAll(newValues);
        }

        return removedElements;
    }

    /**
     * Clears the tracked collection {@code Property} list.
     */
    public void clear() {
        map.clear();
    }

    private <T> Collection<ModelWrapper<T>> getCurrentValues(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> current = new ArrayList<ModelWrapper<T>>();

        List<ModelWrapper<T>> tmp = wrapper.getWrapperCollection(property,
            null, false);
        current.addAll(tmp);

        return current;
    }

    private <T> Collection<ModelWrapper<T>> getOriginalValues(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> original = new ArrayList<ModelWrapper<T>>();

        if (map.containsKey(property)) {
            @SuppressWarnings("unchecked")
            Collection<ModelWrapper<T>> tmp = (Collection<ModelWrapper<T>>) map
                .get(property);
            original.addAll(tmp);
        }

        return original;
    }
}
