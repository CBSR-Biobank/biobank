package edu.ualberta.med.biobank.common.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiffUtils {

    /**
     * return the list of removed objects from the origin list
     */
    public static <T> Collection<T> getRemoved(Collection<T> originList,
        Collection<T> newList) {
        if (originList == null)
            return new ArrayList<T>();
        if (newList == null)
            return originList;
        List<T> removed = new ArrayList<T>(originList);
        removed.removeAll(newList);
        return removed;
    }

    /**
     * return the list of added objects to the origin list
     */
    public static <T> Collection<T> getAdded(Collection<T> originList,
        Collection<T> newList) {
        if (originList == null)
            return newList;
        if (newList == null)
            return new ArrayList<T>();
        List<T> added = new ArrayList<T>(newList);
        added.removeAll(originList);
        return added;
    }
}
