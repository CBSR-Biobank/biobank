package edu.ualberta.med.biobank.common.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Will not work if oldSet or newSet are modified after creating this object.
 * 
 * Symetric difference can be obtained by performing a union on removeSet and
 * addSet.
 * 
 * @param <T>
 */
public class SetDifference<T> {

    private Set<T> oldSet;
    private Set<T> newSet;
    private Set<T> intersection;
    private Set<T> removeSet;
    private Set<T> addSet;

    public SetDifference(Set<T> oldSet, Set<T> newSet) {
        this.oldSet = oldSet;
        this.newSet = newSet;

        intersection = new HashSet<T>(oldSet);
        intersection.retainAll(newSet);

        removeSet = new HashSet<T>(oldSet);
        removeSet.removeAll(intersection);

        addSet = new HashSet<T>(newSet);
        addSet.removeAll(intersection);
    }

    public SetDifference(Collection<T> oldSet, Collection<T> newSet) {
        this(new HashSet<T>(oldSet), new HashSet<T>(newSet));
    }

    public Set<T> getOldSet() {
        return oldSet;
    }

    public Set<T> getNewSet() {
        return newSet;
    }

    public Set<T> getIntersection() {
        return intersection;
    }

    public Set<T> getRemoveSet() {
        return removeSet;
    }

    public Set<T> getAddSet() {
        return addSet;
    }
}
