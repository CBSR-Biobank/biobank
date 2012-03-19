package edu.ualberta.med.biobank.util;

public class NullHelper {
    public static boolean safeEquals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static <T> T ifNull(T o, T defaultValue) {
        return (o != null) ? o : defaultValue;
    }
}
