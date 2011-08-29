package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to {@link ModelWrapper} to track the original set of collection
 * {@link Property}-s (if they're about to change) so that it can be easily
 * determined later what the added and removed elements of the collection were,
 * if any. Tracks a single given {@link ModelWrapper}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
// TODO: should add a class to check that the elements that we think were added
// and removed were actually added and removed (as this could be wrong if the
// underlying model object was modified then given to the wrapper).
public class ElementTracker<E> {
    private final Map<Property<? extends Collection<?>, ? super E>, Object> originalElementsMap = new HashMap<Property<? extends Collection<?>, ? super E>, Object>();
    private final Map<Property<?, ? super E>, Object> originalValueMap = new HashMap<Property<?, ? super E>, Object>();
    private final ModelWrapper<E> wrapper;

    /**
     * @param wrapper the {@link ModelWrapper} to track
     */
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
    public <T> void trackProperty(Property<T, ? super E> property) {
        if (!originalValueMap.containsKey(property)) {
            ModelWrapper<T> originalValue = wrapper.getWrappedProperty(
                property, null);
            originalValueMap.put(property, originalValue);
        }
    }

    public <T> ModelWrapper<T> getRemovedValue(Property<T, ? super E> property) {
        ModelWrapper<T> removedValue = null;

        ModelWrapper<T> oldValue = getOldValue(property);
        ModelWrapper<T> newValue = getCurrentValue(property);

        if (oldValue != null && !oldValue.equals(newValue)) {
            removedValue = oldValue;
        }

        return removedValue;
    }

    /**
     * This method only does something the first time it is called for any given
     * property.
     * 
     * @param <T>
     * @param property
     */
    public <T> void trackCollection(
        Property<? extends Collection<T>, ? super E> property) {
        if (!originalElementsMap.containsKey(property)) {
            Collection<ModelWrapper<T>> originalValues = getCurrentElements(property);
            originalElementsMap.put(property, originalValues);
        }
    }

    /**
     * Gets a list of {@link ModelWrapper}-ed elements that were added to the
     * given collection {@link Property}.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> Collection<ModelWrapper<T>> getAddedElements(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> addedElements = new ArrayList<ModelWrapper<T>>();

        if (originalElementsMap.containsKey(property)) {
            Collection<ModelWrapper<T>> originalValues = getOriginalElements(property);
            Collection<ModelWrapper<T>> newValues = getCurrentElements(property);

            addedElements.addAll(newValues);
            addedElements.removeAll(originalValues);
        } else {
            // the property getter (on the wrapper) was never called if the map
            // is missing the property; however, there might still be added
            // elements in the ElementQueue.
            Collection<ModelWrapper<T>> queued = wrapper.getElementQueue()
                .getAdded(property);
            addedElements.addAll(queued);
        }

        return addedElements;
    }

    /**
     * Gets a list of {@link ModelWrapper}-ed elements that were removed from
     * the given collection {@link Property}.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> Collection<ModelWrapper<T>> getRemovedElements(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> removedElements = new ArrayList<ModelWrapper<T>>();

        if (originalElementsMap.containsKey(property)) {
            Collection<ModelWrapper<T>> originalValues = getOriginalElements(property);
            Collection<ModelWrapper<T>> newValues = getCurrentElements(property);

            removedElements.addAll(originalValues);
            removedElements.removeAll(newValues);
        } else {
            // the property getter (on the wrapper) was never called if the map
            // is missing the property; however, there might still be removed
            // elements in the ElementQueue.
            Collection<ModelWrapper<T>> queued = wrapper.getElementQueue()
                .getRemoved(property);
            removedElements.addAll(queued);
        }

        return removedElements;
    }

    /**
     * Clears the tracked collection {@link Property} list.
     */
    public void clear() {
        originalElementsMap.clear();
        originalValueMap.clear();
    }

    private <T> ModelWrapper<T> getCurrentValue(Property<T, ? super E> property) {
        @SuppressWarnings("unchecked")
        ModelWrapper<T> currentValue = (ModelWrapper<T>) wrapper
            .getWrappedProperty(property, null);
        return currentValue;
    }

    private <T> ModelWrapper<T> getOldValue(Property<T, ? super E> property) {
        ModelWrapper<T> oldValue = null;

        if (originalValueMap.containsKey(property)) {
            @SuppressWarnings("unchecked")
            ModelWrapper<T> tmp = (ModelWrapper<T>) originalValueMap
                .get(property);
            oldValue = tmp;
        }

        return oldValue;
    }

    private <T> Collection<ModelWrapper<T>> getCurrentElements(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> current = new ArrayList<ModelWrapper<T>>();

        List<ModelWrapper<T>> tmp = wrapper.getWrapperCollection(property,
            null, false);
        current.addAll(tmp);

        return current;
    }

    private <T> Collection<ModelWrapper<T>> getOriginalElements(
        Property<? extends Collection<? extends T>, ? super E> property) {
        Collection<ModelWrapper<T>> original = new ArrayList<ModelWrapper<T>>();

        if (originalElementsMap.containsKey(property)) {
            @SuppressWarnings("unchecked")
            Collection<ModelWrapper<T>> tmp = (Collection<ModelWrapper<T>>) originalElementsMap
                .get(property);
            original.addAll(tmp);
        }

        return original;
    }
}
