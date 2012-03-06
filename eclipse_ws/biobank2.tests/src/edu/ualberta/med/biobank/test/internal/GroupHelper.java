package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.GroupWrapper;

@Deprecated
public class GroupHelper extends PrincipalHelper {

    private static List<GroupWrapper> createdGroups =
        new ArrayList<GroupWrapper>();

    public static GroupWrapper newGroup(String name) {
        GroupWrapper group = new GroupWrapper(appService);
        group.setName(name);
        return group;
    }

    public static GroupWrapper addGroup(String name, boolean addToCreatedList)
        throws Exception {
        GroupWrapper group = newGroup(name);
        group.persist();
        if (addToCreatedList)
            createdGroups.add(group);
        return group;
    }

    public static void deleteCreatedGroups() throws Exception {
        for (GroupWrapper group : createdGroups)
            group.delete();
        createdGroups.clear();
    }
}
