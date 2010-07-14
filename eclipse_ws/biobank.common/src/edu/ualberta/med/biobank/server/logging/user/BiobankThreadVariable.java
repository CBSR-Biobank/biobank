package edu.ualberta.med.biobank.server.logging.user;

public class BiobankThreadVariable {

    private static ThreadLocal<UserInfo> userInfo = new ThreadLocal<UserInfo>();

    public static UserInfo get() {
        return userInfo.get();
    }

    public static void set(UserInfo user) {
        userInfo.set(user);
    }

}
