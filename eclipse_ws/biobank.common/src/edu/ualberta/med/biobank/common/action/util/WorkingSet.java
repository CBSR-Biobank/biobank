package edu.ualberta.med.biobank.common.action.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Set} that remembers the original working set, so after this
 * {@link Set} is modified, the added and removed elements can be determined.
 * This is useful if the original set is actually a subset. Then it is easier to
 * track the changes an individual makes to a subset of elements in collection.
 * <p>
 * This class is intended to be used with relatively small sets.
 * <p>
 * Note that it probably wouldn't save much room to track the additions and
 * removals separately (vs simply tracking the originals) as the most possible
 * room saved would be around double the size of the original set. Twice the
 * references is not a big deal.
 * 
 * @author Jonathan Ferland
 * 
 * @param <T> element type
 */
public class WorkingSet<T> implements Set<T> {
    private final Set<T> original;
    private final Set<T> delegate = new HashSet<T>();

    public static <E> WorkingSet<E> of(Set<E> original) {
        return new WorkingSet<E>(original);
    }

    public WorkingSet(Set<T> original) {
        this.original = Collections.unmodifiableSet(original);
    }

    public void setValues(Set<T> s) {
        delegate.clear();
        delegate.addAll(s);
    }

    public Set<T> getAdditions() {
        Set<T> additions = new HashSet<T>(delegate);
        additions.removeAll(original);
        return additions;
    }

    public Set<T> getRemovals() {
        Set<T> removals = new HashSet<T>(original);
        removals.removeAll(delegate);
        return removals;
    }

    public SetDiff<T> getDiff() {
        return SetDiff.of(original, delegate);
    }

    public Set<T> getOriginal() {
        return original;
    }

    /**
     * Reset to the original {@link Set}.
     */
    public void reset() {
        delegate.clear();
        delegate.addAll(original);
    }

    @Override
    public boolean add(T e) {
        return delegate.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return delegate.addAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return delegate.toArray(a);
    }
}
