package edu.ualberta.med.biobank.server.logging;

public class BiobankThreadVariable {

    private static ThreadLocal<LocalInfo> userInfo = new ThreadLocal<LocalInfo>();

    public static LocalInfo get() {
        return userInfo.get();
    }

    public static void set(LocalInfo user) {
        userInfo.set(user);
    }

}
