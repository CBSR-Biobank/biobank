package edu.ualberta.med.biobank.common.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class CollectionsUtil {
    public static <T extends Comparable<T>> int compare(Collection<T> lhs,
        Collection<T> rhs) {
        int cmp;
        Iterator<T> lit = lhs.iterator();
        Iterator<T> rit = rhs.iterator();
        while (lit.hasNext() && rit.hasNext()) {
            cmp = lit.next().compareTo(rit.next());
            if (cmp != 0) {
                return cmp;
            }
        }
        return lhs.size() - rhs.size();
    }

    public static <T extends Comparable<T>> int compare(Collection<T> lhs,
        Collection<T> rhs, Comparator<T> comparator) {
        int cmp;
        Iterator<T> lit = lhs.iterator();
        Iterator<T> rit = rhs.iterator();
        while (lit.hasNext() && rit.hasNext()) {
            cmp = comparator.compare(lit.next(), rit.next());
            if (cmp != 0) {
                return cmp;
            }
        }
        return lhs.size() - rhs.size();
    }
}
