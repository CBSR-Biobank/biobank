package edu.ualberta.med.biobank.common.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionsUtil {
    public static <T extends Comparable<T>> int compareTo(Collection<T> lhs,
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
}
