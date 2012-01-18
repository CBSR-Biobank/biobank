package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.UserWrapper;

@Deprecated
public class UserHelper extends PrincipalHelper {

    public static List<UserWrapper> createdUsers = new ArrayList<UserWrapper>();

    public static UserWrapper newUser(String login, String password)
        throws Exception {
        UserWrapper user = new UserWrapper(appService);
        user.setLogin(login);
        user.setPassword(password);
        return user;
    }

    public static UserWrapper addUser(String login, String password,
        boolean addToCreatedList) throws Exception {
        UserWrapper user = newUser(login, password);
        user.persist();
        if (addToCreatedList)
            createdUsers.add(user);
        return user;
    }

    public static void deleteCreatedUsers() throws Exception {
        for (UserWrapper user : createdUsers) {
            user.reload();
            user.delete();
        }
        createdUsers.clear();
    }

}
