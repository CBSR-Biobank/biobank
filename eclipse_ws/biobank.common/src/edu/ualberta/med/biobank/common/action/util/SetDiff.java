package edu.ualberta.med.biobank.common.action.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.HasId;
import edu.ualberta.med.biobank.model.util.IdUtil;

/**
 * Immutable snapshot of the difference between two {@link Set}-s at a given
 * point in time.
 * 
 * @author Jonathan Ferland
 * 
 * @param <T>
 */
public class SetDiff<T> {
    private final Set<T> additions;
    private final Set<T> removals;
    private final Set<T> difference;

    private SetDiff(Set<T> additions, Set<T> removals) {
        this.additions = Collections.unmodifiableSet(additions);
        this.removals = Collections.unmodifiableSet(removals);

        Set<T> difference = new HashSet<T>();
        difference.addAll(additions);
        difference.addAll(removals);

        this.difference = Collections.unmodifiableSet(difference);
    }

    public Set<T> getAdditions() {
        return additions;
    }

    public Set<T> getRemovals() {
        return removals;
    }

    public Set<T> getDifference() {
        return difference;
    }

    /**
     * Apply the difference to the given {@link Set}.
     * 
     * @param s {@link Set} to apply this {@link SetDiff} to.
     */
    public void apply(Set<T> s) {
        s.removeAll(removals);
        s.addAll(additions);
    }

    public static <E> SetDiff<E> copy(Set<E> additions, Set<E> removals) {
        return new SetDiff<E>(additions, removals);
    }

    public static <E> SetDiff<E> of(Set<E> before, Set<E> after) {
        Set<E> additions = new HashSet<E>(after);
        additions.removeAll(before);

        Set<E> removals = new HashSet<E>(before);
        additions.removeAll(after);

        return new SetDiff<E>(additions, removals);
    }

    public static <U extends Serializable, E extends HasId<U>> SetDiff<U> ofIds(
        Set<E> before, Set<E> after) {
        return of(IdUtil.getIds(before), IdUtil.getIds(after));
    }
}
