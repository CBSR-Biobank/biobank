package edu.ualberta.med.biobank.common.exception;

public class ExceptionUtils {

    public static Throwable findCauseInThrowable(Throwable t,
        Class<? extends Throwable> causeClass) {
        Throwable tmp = t;
        while (tmp != null && !(causeClass.isAssignableFrom(tmp.getClass())))
            tmp = tmp.getCause();
        return tmp;
    }
}
