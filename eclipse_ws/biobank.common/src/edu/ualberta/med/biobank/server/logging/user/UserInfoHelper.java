package edu.ualberta.med.biobank.server.logging.user;

import edu.ualberta.med.biobank.server.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.LocalInfo;

public class UserInfoHelper {

    public static void setUserName(String userName) {
        LocalInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new LocalInfo();
        if (!(null == userName || userName.trim().length() == 0)) {
            userInfo.setUsername(userName);
        }
        BiobankThreadVariable.set(userInfo);
    }

}
