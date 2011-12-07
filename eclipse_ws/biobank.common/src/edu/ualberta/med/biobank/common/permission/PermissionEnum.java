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
    SPECIMEN_LINK(6),
    SPECIMEN_ASSIGN(7),

    SITE_CREATE(8),
    SITE_READ(9),
    SITE_UPDATE(10),
    SITE_DELETE(11),

    PATIENT_CREATE(12),
    PATIENT_READ(13),
    PATIENT_UPDATE(14),
    PATIENT_DELETE(15),
    PATIENT_MERGE(16),

    COLLECTION_EVENT_CREATE(17),
    COLLECTION_EVENT_READ(18),
    COLLECTION_EVENT_UPDATE(19),
    COLLECTION_EVENT_DELETE(20),

    PROCESSING_EVENT_CREATE(21),
    PROCESSING_EVENT_READ(22),
    PROCESSING_EVENT_UPDATE(23),
    PROCESSING_EVENT_DELETE(24),

    ORIGIN_INFO_CREATE(25),
    ORIGIN_INFO_READ(26),
    ORIGIN_INFO_UPDATE(27),
    ORIGIN_INFO_DELETE(28),

    DISPATCH_READ(29),
    DISPATCH_CHANGE_STATE(30),
    DISPATCH_UPDATE(31),
    DISPATCH_DELETE(32),

    RESEARCH_GROUP_CREATE(33),
    RESEARCH_GROUP_READ(34),
    RESEARCH_GROUP_UPDATE(35),
    RESEARCH_GROUP_DELETE(36),

    STUDY_CREATE(37),
    STUDY_READ(38),
    STUDY_UPDATE(39),
    STUDY_DELETE(40),

    REQUEST_READ(41),
    REQUEST_UPDATE(42),
    REQUEST_SAVE(43),

    CLINIC_CREATE(44),
    CLINIC_READ(45),
    CLINIC_UPDATE(46),
    CLINIC_DELETE(47),

    USER_MANAGEMENT(48);

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
                boolean x = isMembershipAllowed(memb, center, study);
                if (x) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMembershipAllowed(Membership membership, Center center,
        Study study) {

        boolean hasCenter =
            center == null || membership.getCenter() == null
                || membership.getCenter().equals(center);
        boolean hasStudy =
            study == null || membership.getStudy() == null
                || membership.getStudy().equals(study);

        Collection<Permission> permissions =
            membership.getPermissionCollection();
        boolean hasPermission =
            ((permissions != null) && isPermissionAllowed(permissions));

        boolean result = hasCenter && hasStudy && hasPermission;

        return result;
    }

    private boolean isPermissionAllowed(Collection<Permission> permissions) {
        for (Permission permission : permissions) {
            if (permission.getId().equals(getId())
                || permission.getId().equals(ADMINISTRATION.getId())) {
                return true;
            }
        }
        return false;
    }
}
