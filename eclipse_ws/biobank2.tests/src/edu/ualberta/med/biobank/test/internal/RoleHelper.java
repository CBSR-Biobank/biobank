package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;

public class RoleHelper extends DbHelper {

    public static List<RoleWrapper> createdRoles = new ArrayList<RoleWrapper>();

    public static RoleWrapper newRole(String name) {
        RoleWrapper role = new RoleWrapper(appService);
        role.setName(name);
        return role;
    }

    public static RoleWrapper addRole(String name, boolean addToCreatedRoles)
        throws Exception {
        RoleWrapper role = newRole(name);
        role.persist();
        if (addToCreatedRoles)
            createdRoles.add(role);
        return role;
    }

    public static void deleteCreatedRoles() throws Exception {
        for (RoleWrapper role : createdRoles) {
            role.reload();
            role.delete();
        }
        createdRoles.clear();
    }
}
