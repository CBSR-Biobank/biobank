package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The names of these enumerations are saved in the database. Therefore, DO NOT
 * CHANGE THESE ENUM INSTANCE/ VARIABLE NAMES (unless you are prepared to write
 * an upgrade script). However, order does not matter and can be changed.
 * 
 * @author jferland
 * 
 */
public enum PermissionEnum implements Serializable {
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

    DISPATCH_CREATE(29),
    DISPATCH_READ(30),
    DISPATCH_CHANGE_STATE(31),
    DISPATCH_UPDATE(32),
    DISPATCH_DELETE(33),

    RESEARCH_GROUP_CREATE(34),
    RESEARCH_GROUP_READ(35),
    RESEARCH_GROUP_UPDATE(36),
    RESEARCH_GROUP_DELETE(37),

    STUDY_CREATE(38),
    STUDY_READ(39),
    STUDY_UPDATE(40),
    STUDY_DELETE(41),

    REQUEST_CREATE(42),
    REQUEST_READ(43),
    REQUEST_UPDATE(44),
    REQUEST_DELETE(45),

    REQUEST_PROCESS(46),

    CLINIC_CREATE(47),
    CLINIC_READ(48),
    CLINIC_UPDATE(49),
    CLINIC_DELETE(50),

    USER_MANAGEMENT(51),

    CONTAINER_TYPE_CREATE(52),
    CONTAINER_TYPE_READ(53),
    CONTAINER_TYPE_UPDATE(54),
    CONTAINER_TYPE_DELETE(55),

    CONTAINER_CREATE(56),
    CONTAINER_READ(57),
    CONTAINER_UPDATE(58),
    CONTAINER_DELETE(59),

    SPECIMEN_TYPE_CREATE(60),
    SPECIMEN_TYPE_READ(61),
    SPECIMEN_TYPE_UPDATE(62),
    SPECIMEN_TYPE_DELETE(63),

    LOGGING(64),
    REPORTS(65),

    SPECIMEN_LIST(66);

    private static final List<PermissionEnum> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    private final Integer permissionId;

    private PermissionEnum(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public static List<PermissionEnum> valuesList() {
        return VALUES_LIST;
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

        Set<BbGroup> groups = user.getGroupCollection();
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
        Set<Membership> membs = principal.getMembershipCollection();
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

        Set<PermissionEnum> permissions =
            membership.getPermissionCollection();
        boolean hasPermission =
            ((permissions != null) && isPermissionAllowed(permissions));

        boolean result = hasCenter && hasStudy && hasPermission;

        return result;
    }

    private boolean isPermissionAllowed(Set<PermissionEnum> permissions) {
        for (PermissionEnum permission : permissions) {
            if (equals(permission) || ADMINISTRATION.equals(permission)) {
                return true;
            }
        }
        return false;
    }
}
