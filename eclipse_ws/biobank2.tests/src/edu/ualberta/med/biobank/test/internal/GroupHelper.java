package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;

@Deprecated
public class GroupHelper extends PrincipalHelper {

    private static List<BbGroupWrapper> createdGroups =
        new ArrayList<BbGroupWrapper>();

    public static BbGroupWrapper newGroup(String name) {
        BbGroupWrapper group = new BbGroupWrapper(appService);
        group.setName(name);
        return group;
    }

    public static BbGroupWrapper addGroup(String name, boolean addToCreatedList)
        throws Exception {
        BbGroupWrapper group = newGroup(name);
        group.persist();
        if (addToCreatedList)
            createdGroups.add(group);
        return group;
    }

    public static void deleteCreatedGroups() throws Exception {
        for (BbGroupWrapper group : createdGroups)
            group.delete();
        createdGroups.clear();
    }
}
