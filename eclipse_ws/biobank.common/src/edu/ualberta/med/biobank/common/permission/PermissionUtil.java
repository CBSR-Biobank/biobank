package edu.ualberta.med.biobank.common.permission;

import java.util.Collection;

import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class PermissionUtil {
    public static boolean isAllowed(User user, Permission permission) {
        return isAllowed(user, permission, null, null);
    }

    public static boolean isAllowed(User user, Permission permission,
        Study study) {
        return isAllowed(user, permission, null, study);
    }

    public static boolean isAllowed(User user, Permission permission,
        Center center) {
        return isAllowed(user, permission, center, null);
    }

    public static boolean isAllowed(User user, Permission permission,
        Center center, Study study) {

        boolean allowed = isAllowed(user.getMembershipCollection(), permission,
            center, study);

        if (!allowed) {
            Collection<BbGroup> groups = user.getGroupCollection();
            for (BbGroup group : groups) {
                allowed = isAllowed(group.getMembershipCollection(),
                    permission, center, study);
                if (allowed) {
                    break;
                }
            }
        }

        return allowed;
    }

    private static boolean isAllowed(Collection<Membership> memberships,
        Permission permission, Center center, Study study) {
        if (memberships != null) {
            for (Membership membership : memberships) {
                // TODO: compare Permission interface and the Permission model
                // object, somehow...
                // Collection<Permission> perms =
                // membership.getPermissionCollection();
                if (center != null
                    && !nullSafeEquals(center, membership.getCenter())) {
                    continue;
                }
            }
        }

        return false;
    }

    private static boolean nullSafeEquals(Object a, Object b) {
        return a == b || a.equals(b);
    }
}
