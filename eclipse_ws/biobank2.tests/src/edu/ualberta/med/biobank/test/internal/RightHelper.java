package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;

public class RightHelper extends DbHelper {
    public static List<BbRightWrapper> createdRights = new ArrayList<BbRightWrapper>();

    public static BbRightWrapper newRight(String name, String keyDesc) {
        BbRightWrapper right = new BbRightWrapper(appService);
        right.setName(name);
        right.setKeyDesc(keyDesc);
        return right;
    }

    public static BbRightWrapper addRight(String name, String keyDesc,
        boolean addToCreatedRights) throws Exception {
        BbRightWrapper right = newRight(name, keyDesc);
        right.persist();
        if (addToCreatedRights)
            createdRights.add(right);
        return right;
    }

    public static void deleteCreatedRights() throws Exception {
        for (BbRightWrapper right : createdRights) {
            right.reload();
            right.delete();
        }
        createdRights.clear();
    }
}
