package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipWrapper extends MembershipBaseWrapper {

    public MembershipWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipWrapper(WritableApplicationService appService,
        Membership wrappedObject) {
        super(appService, wrappedObject);
    }

    public void addToPermissionCollection(
        Collection<PermissionEnum> addedPermissions) {
        wrappedObject.getPermissions().addAll(addedPermissions);
    }

    public void removeFromPermissionCollection(
        Collection<PermissionEnum> removedPermissions) {
        wrappedObject.getPermissions().removeAll(removedPermissions);
    }

    public Collection<PermissionEnum> getPermissionCollection() {
        return wrappedObject.getPermissions();
    }
}
