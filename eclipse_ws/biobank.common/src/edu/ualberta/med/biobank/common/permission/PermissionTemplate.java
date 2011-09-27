package edu.ualberta.med.biobank.common.permission;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public abstract class PermissionTemplate implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public final boolean isAllowed(User user) {
        return true;
    }

    private static boolean isAllowed(Principal principal) {
        Collection<Membership> memberships = principal
            .getMembershipCollection();
        for (Membership membership : memberships) {
            // Collection<edu.ualberta.med.biobank.model.Permission> perms =
            // membership
            // .getPermissionCollection();
            // for ()
        }
        return false;
    }

    public Collection<Center> getCenters() {
        return new ArrayList<Center>();
    }

    public Collection<Study> getStudies() {
        return new ArrayList<Study>();
    }
}
