package edu.ualberta.med.biobank.common.exception;

public class ExceptionUtils {

    public static Throwable findCausesInThrowable(Throwable t,
        Class<?>... causeClasses) {
        Throwable tmp = t;
        while (tmp != null
            && !(inheritedAtLeastOne(tmp.getClass(), causeClasses)))
            tmp = tmp.getCause();
        return tmp;
    }

    private static boolean inheritedAtLeastOne(Class<?> clazz,
        Class<?>... parents) {
        for (Class<?> parent : parents) {
            boolean res = parent.isAssignableFrom(clazz);
            if (res == true)
                return true;
        }
        return false;
    }

    public static Throwable findFirstCause(Throwable t) {
        if (t == null)
            return null;
        if (t.getCause() == null)
            return t;
        return findFirstCause(t.getCause());
    }
}
