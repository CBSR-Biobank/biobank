package edu.ualberta.med.biobank.common.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class DiffUtils<T> {

    private Collection<T> collection;

    private HashSet<T> addOrKeep;

    public DiffUtils(Collection<T> collection) {
        this.collection = collection;
        addOrKeep = new HashSet<T>();
    }

    public void add(T object) {
        collection.add(object);
        addOrKeep.add(object);
    }

    /**
     * return the list of the removed object, and removed them from the
     * collection as well.
     */
    public Collection<T> pullRemoved() {
        Collection<T> removed = getRemoved();
        collection.removeAll(removed);
        return removed;
    }

    /**
     * return the list of the removed object, the object will stil be in the
     * original list
     */
    public Collection<T> getRemoved() {
        Collection<T> removed = new ArrayList<T>(collection);
        removed.removeAll(addOrKeep);
        return removed;
    }

}
