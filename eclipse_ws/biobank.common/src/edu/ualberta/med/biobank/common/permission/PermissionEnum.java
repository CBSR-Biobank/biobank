package edu.ualberta.med.biobank.common.permission;

import java.util.Collection;

import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

/**
 * Corresponds to rows in the database for {@link Permission} model objects
 * through the "id" property.
 * 
 * @author jferland
 * 
 */
public enum PermissionEnum {
    ADMINISTRATION(1),

    SPECIMEN_CREATE(2),
    SPECIMEN_READ(3),
    SPECIMEN_UPDATE(4),
    SPECIMEN_DELETE(5),

    SITE_CREATE(6),
    SITE_READ(7),
    SITE_UPDATE(8),
    SITE_DELETE(9),

    PATIENT_CREATE(10),
    PATIENT_READ(11),
    PATIENT_UPDATE(12),
    PATIENT_DELETE(13),
    PATIENT_MERGE(14),

    COLLECTION_EVENT_CREATE(15),
    COLLECTION_EVENT_READ(16),
    COLLECTION_EVENT_UPDATE(17),
    COLLECTION_EVENT_DELETE(18),

    PROCESSING_EVENT_CREATE(19),
    PROCESSING_EVENT_READ(20),
    PROCESSING_EVENT_UPDATE(21),
    PROCESSING_EVENT_DELETE(22);

    private final Integer permissionId;

    private PermissionEnum(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getId() {
        return permissionId;
    }

    public boolean isAllowed(User user) {
        return isAllowed(user, null, null);
    }

    public boolean isAllowed(User user, Study study) {
        return isAllowed(user, null, study);
    }

    public boolean isAllowed(User user, Center center) {
        return isAllowed(user, center, null);
    }

    public boolean isAllowed(User user, Center center, Study study) {

        if (isPrincipalAllowed(user, center, study)) {
            return true;
        }

        Collection<BbGroup> groups = user.getGroupCollection();
        if (groups != null) {
            for (BbGroup group : groups) {
                if (isPrincipalAllowed(group, center, study)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPrincipalAllowed(Principal principal, Center center,
        Study study) {
        Collection<Membership> membs = principal.getMembershipCollection();
        if (membs != null) {
            for (Membership memb : membs) {
                if (isMembershipAllowed(memb, center, study)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMembershipAllowed(Membership membership,
        Center center, Study study) {

        boolean hasCenter = center == null || membership.getCenter() == null
            || membership.getCenter().equals(center);
        boolean hasStudy = study == null || membership.getStudy() == null
            || membership.getStudy().equals(study);

        Collection<Permission> permissions = membership
            .getPermissionCollection();
        boolean hasPermission = permissions != null
            && isPermissionAllowed(permissions);

        return hasCenter && hasStudy && hasPermission;
    }

    private boolean isPermissionAllowed(Collection<Permission> permissions) {
        for (Permission permission : permissions) {
            if (permission.getId().equals(getId())) {
                return true;
            }
        }
        return false;
    }
}
