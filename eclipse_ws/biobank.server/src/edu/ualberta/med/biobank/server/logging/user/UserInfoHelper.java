package edu.ualberta.med.biobank.server.logging.user;

public class UserInfoHelper {

    public static void setUserName(String userName) {
        UserInfo userInfo = (UserInfo) BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
        if (!(null == userName || userName.trim().length() == 0)) {
            userInfo.setUsername(userName);
        }
        BiobankThreadVariable.set(userInfo);
    }

    public static void setObjectID(String objectIDKey) {
        UserInfo userInfo = (UserInfo) BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
        if (!(null == objectIDKey || objectIDKey.trim().length() == 0)) {
            userInfo.setObjectIDKey(objectIDKey);
        }
        BiobankThreadVariable.set(userInfo);
    }

}
