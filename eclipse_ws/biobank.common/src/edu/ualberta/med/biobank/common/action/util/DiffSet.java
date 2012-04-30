package edu.ualberta.med.biobank.common.action.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.ualberta.med.biobank.model.HasId;
import edu.ualberta.med.biobank.model.util.IdUtil;

/**
 * Immutable snapshot of the difference between two {@link Set}-s at a given
 * point in time.
 * 
 * @author Jonathan Ferland
 * 
 * @param <E> element type
 */
public class DiffSet<E> implements Set<E> {
    private static final String NOT_MUTEX_MSG = "Sets not mutually exclusive."
        + " The additions and removals sets must not share any equal values.";
    private final Set<E> additions;
    private final Set<E> removals;
    private final Set<E> difference;

    private DiffSet(Set<E> additions, Set<E> removals) {
        this.additions = Collections.unmodifiableSet(additions);
        this.removals = Collections.unmodifiableSet(removals);

        Set<E> difference = new HashSet<E>();
        difference.addAll(additions);
        difference.addAll(removals);

        if (difference.size() != additions.size() + removals.size()) {
            throw new IllegalArgumentException(NOT_MUTEX_MSG);
        }

        this.difference = Collections.unmodifiableSet(difference);
    }

    public Set<E> getAdditions() {
        return additions;
    }

    public Set<E> getRemovals() {
        return removals;
    }

    /**
     * Apply the difference to the given {@link Set}.
     * 
     * @param s {@link Set} to apply this {@link DiffSet} to.
     */
    public void apply(Set<E> s) {
        s.removeAll(removals);
        s.addAll(additions);
    }

    public static <E> DiffSet<E> copy(Set<E> additions, Set<E> removals) {
        return new DiffSet<E>(additions, removals);
    }

    public static <E> DiffSet<E> of(Set<E> before, Set<E> after) {
        Set<E> additions = new HashSet<E>(after);
        additions.removeAll(before);

        Set<E> removals = new HashSet<E>(before);
        additions.removeAll(after);

        return new DiffSet<E>(additions, removals);
    }

    public static <U extends Serializable, E extends HasId<U>> DiffSet<U> ofIds(
        Set<E> before, Set<E> after) {
        return of(IdUtil.getIds(before), IdUtil.getIds(after));
    }

    @Override
    public boolean add(E e) {
        return difference.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return difference.addAll(c);
    }

    @Override
    public void clear() {
        difference.clear();
    }

    @Override
    public boolean contains(Object o) {
        return difference.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return difference.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return difference.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return difference.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return difference.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return difference.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return difference.retainAll(c);
    }

    @Override
    public int size() {
        return difference.size();
    }

    @Override
    public Object[] toArray() {
        return difference.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return difference.toArray(a);
    }
}
