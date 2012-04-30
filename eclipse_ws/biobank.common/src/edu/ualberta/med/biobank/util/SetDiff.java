package edu.ualberta.med.biobank.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable difference between two {@link Set}-s: an old one and a new one.
 * Maintains another {@link Set} of the intersection for the old and new, making
 * merging easier.
 * 
 * @author Jonathan Ferland
 * @author Nelson Loyola
 * 
 * @param <T>
 */
public class SetDiff<T> {
    private final Set<Pair<T>> intersection;
    private final Set<T> removals;
    private final Set<T> additions;

    public static class Pair<T> {
        private final T oldObject;
        private final T newObject;

        public Pair(T oldObject, T newObject) {
            this.oldObject = oldObject;
            this.newObject = newObject;
        }

        public T getOld() {
            return oldObject;
        }

        public T getNew() {
            return newObject;
        }
    }

    public SetDiff(Set<T> oldSet, Set<T> newSet) {
        Set<Pair<T>> pairs = new HashSet<Pair<T>>();
        for (T oldObject : oldSet) {
            for (T newObject : newSet) {
                if (oldObject.equals(newObject)) {
                    Pair<T> pair = new Pair<T>(oldObject, newObject);
                    pairs.add(pair);
                }
            }
        }
        this.intersection = Collections.unmodifiableSet(pairs);

        Set<T> removed = new HashSet<T>(oldSet);
        removed.removeAll(newSet);
        this.removals = Collections.unmodifiableSet(removed);

        Set<T> added = new HashSet<T>(newSet);
        added.removeAll(oldSet);
        this.additions = Collections.unmodifiableSet(added);
    }

    public Set<Pair<T>> getIntersection() {
        return intersection;
    }

    public Set<T> getRemovals() {
        return removals;
    }

    public Set<T> getAdditions() {
        return additions;
    }
}
