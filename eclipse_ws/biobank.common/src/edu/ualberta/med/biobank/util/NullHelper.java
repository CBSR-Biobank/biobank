package edu.ualberta.med.biobank.util;

import java.util.Comparator;

public class NullHelper {
    public static boolean safeEquals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static <T> T ifNull(T o, T defaultValue) {
        return (o != null) ? o : defaultValue;
    }

    /**
     * Compare two {@link Comparable} Object-s, even if either one is null.
     * 
     * @param <T>
     * @param one
     * @param two
     * @return
     */
    public static <T extends Comparable<T>> int safeCompareTo(
        final T one, final T two) {
        if (one == null ^ two == null) {
            return (one == null) ? -1 : 1;
        }

        if (one == null || two == null) {
            return 0;
        }
        return one.compareTo(two);
    }

    /**
     * Compare two Comparable Object-s, even if either one is null, using the
     * given {@link Comparator}.
     * 
     * @param one
     * @param two
     * @param comparator
     * @return
     */
    public static <T> int safeCompareTo(final T one, final T two,
        Comparator<T> comparator) {
        if (one == null ^ two == null) {
            return (one == null) ? -1 : 1;
        }

        if (one == null || two == null) {
            return 0;
        }
        return comparator.compare(one, two);
    }
}
