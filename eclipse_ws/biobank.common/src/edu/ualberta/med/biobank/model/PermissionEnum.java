package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.NotAProxy;

/**
 * The id of these enumerations are saved in the database. Therefore, DO NOT
 * CHANGE THESE ENUM IDS (unless you are prepared to write an upgrade script).
 * However, order and enum name can be modified freely.
 * <p>
 * Also, these enums should probably never be deleted, unless they are not used
 * in <em>any</em> database. Instead, they should be deprecated and probably
 * always return false when checking allow-ability.
 * 
 * @author Jonathan Ferland
 * 
 */
@SuppressWarnings("nls")
public enum PermissionEnum implements NotAProxy, Serializable {
    SPECIMEN_CREATE(2,
        Loader.i18n.tr("Specimen Create")),
    SPECIMEN_READ(3,
        Loader.i18n.tr("Specimen Read")),
    SPECIMEN_UPDATE(4,
        Loader.i18n.tr("Specimen Update")),
    SPECIMEN_DELETE(5,
        Loader.i18n.tr("Specimen Delete")),
    SPECIMEN_LINK(6,
        Loader.i18n.tr("Specimen Link")),
    SPECIMEN_ASSIGN(7,
        Loader.i18n.tr("Specimen Assign")),

    SITE_CREATE(8,
        Loader.i18n.tr("Site Create"),
        Require.ALL_CENTERS),
    SITE_READ(9,
        Loader.i18n.tr("Site Read")),
    SITE_UPDATE(10,
        Loader.i18n.tr("Site Update")),
    SITE_DELETE(11,
        Loader.i18n.tr("Site Delete")),

    PATIENT_CREATE(12,
        Loader.i18n.tr("Patient Create")),
    PATIENT_READ(13,
        Loader.i18n.tr("Patient Read")),
    PATIENT_UPDATE(14,
        Loader.i18n.tr("Patient Update")),
    PATIENT_DELETE(15,
        Loader.i18n.tr("Patient Delete")),
    PATIENT_MERGE(16,
        Loader.i18n.tr("Patient Merge")),

    COLLECTION_EVENT_CREATE(17,
        Loader.i18n.tr("Collection Event Create")),
    COLLECTION_EVENT_READ(18,
        Loader.i18n.tr("Collection Event Read")),
    COLLECTION_EVENT_UPDATE(19,
        Loader.i18n.tr("Collection Event Update")),
    COLLECTION_EVENT_DELETE(20,
        Loader.i18n.tr("Collection Event Delete")),

    PROCESSING_EVENT_CREATE(21,
        Loader.i18n.tr("Processing Event Create")),
    PROCESSING_EVENT_READ(22,
        Loader.i18n.tr("Processing Event Read")),
    PROCESSING_EVENT_UPDATE(23,
        Loader.i18n.tr("Processing Event Update")),
    PROCESSING_EVENT_DELETE(24,
        Loader.i18n.tr("Processing Event Delete")),

    ORIGIN_INFO_CREATE(25,
        Loader.i18n.tr("Origin Information Create")),
    ORIGIN_INFO_READ(26,
        Loader.i18n.tr("Origin Information Read")),
    ORIGIN_INFO_UPDATE(27,
        Loader.i18n.tr("Origin Information Update")),
    ORIGIN_INFO_DELETE(28,
        Loader.i18n.tr("Origin Information Delete")),

    DISPATCH_CREATE(29,
        Loader.i18n.tr("Dispatch Create")),
    DISPATCH_READ(30,
        Loader.i18n.tr("Dispatch Read")),
    DISPATCH_CHANGE_STATE(31,
        Loader.i18n.tr("Dispatch Change State")),
    DISPATCH_UPDATE(32,
        Loader.i18n.tr("Dispatch Update")),
    DISPATCH_DELETE(33,
        Loader.i18n.tr("Dispatch Delete")),

    RESEARCH_GROUP_CREATE(34,
        Loader.i18n.tr("Research Group Create"),
        Require.ALL_CENTERS),
    RESEARCH_GROUP_READ(35,
        Loader.i18n.tr("Research Group Read")),
    RESEARCH_GROUP_UPDATE(36,
        Loader.i18n.tr("Research Group Update")),
    RESEARCH_GROUP_DELETE(37,
        Loader.i18n.tr("Research Group Delete")),

    STUDY_CREATE(38,
        Loader.i18n.tr("Study Create"),
        Require.ALL_STUDIES),
    STUDY_READ(39,
        Loader.i18n.tr("Study Read")),
    STUDY_UPDATE(40,
        Loader.i18n.tr("Study Update")),
    STUDY_DELETE(41,
        Loader.i18n.tr("Study Delete")),

    REQUEST_CREATE(42,
        Loader.i18n.tr("Request Create")),
    REQUEST_READ(43,
        Loader.i18n.tr("Request Read")),
    REQUEST_UPDATE(44,
        Loader.i18n.tr("Request Update")),
    REQUEST_DELETE(45,
        Loader.i18n.tr("Request Delete")),
    REQUEST_PROCESS(46,
        Loader.i18n.tr("Request Process")),

    CLINIC_CREATE(47,
        Loader.i18n.tr("Clinic Create"),
        Require.ALL_CENTERS),
    CLINIC_READ(48,
        Loader.i18n.tr("Clinic Read")),
    CLINIC_UPDATE(49,
        Loader.i18n.tr("Clinic Update")),
    CLINIC_DELETE(50,
        Loader.i18n.tr("Clinic Delete")),

    CONTAINER_TYPE_CREATE(52,
        Loader.i18n.tr("Container Type Create")),
    CONTAINER_TYPE_READ(53,
        Loader.i18n.tr("Container Type Read")),
    CONTAINER_TYPE_UPDATE(54,
        Loader.i18n.tr("Container Type Update")),
    CONTAINER_TYPE_DELETE(55,
        Loader.i18n.tr("Container Type Delete")),

    CONTAINER_CREATE(56,
        Loader.i18n.tr("Container Create")),
    CONTAINER_READ(57,
        Loader.i18n.tr("Container Read")),
    CONTAINER_UPDATE(58,
        Loader.i18n.tr("Container Update")),
    CONTAINER_DELETE(59,
        Loader.i18n.tr("Container Delete")),

    SPECIMEN_TYPE_CREATE(60,
        Loader.i18n.tr("Specimen Type Create"),
        Require.ALL_CENTERS, Require.ALL_STUDIES),
    SPECIMEN_TYPE_READ(61,
        Loader.i18n.tr("Specimen Type Read")),
    SPECIMEN_TYPE_UPDATE(62,
        Loader.i18n.tr("Specimen Type Update"),
        Require.ALL_CENTERS, Require.ALL_STUDIES),
    SPECIMEN_TYPE_DELETE(63,
        Loader.i18n.tr("Specimen Type Delete"),
        Require.ALL_CENTERS, Require.ALL_STUDIES),

    LOGGING(64,
        Loader.i18n.tr("Logging")),
    REPORTS(65,
        Loader.i18n.tr("Reports")),

    SPECIMEN_LIST(66,
        Loader.i18n.tr("Specimen List")),
    LABEL_PRINTING(67,
        Loader.i18n.tr("Label Printing")),

    SPECIMEN_CSV_IMPORT(68,
        Loader.i18n.tr("Specimen CSV Import"));

    private static final List<PermissionEnum> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));
    private static final Map<Integer, PermissionEnum> VALUES_MAP;

    static {
        Map<Integer, PermissionEnum> map =
            new HashMap<Integer, PermissionEnum>();

        for (PermissionEnum permissionEnum : values()) {
            PermissionEnum check = map.get(permissionEnum.getId());
            if (check != null) {
                throw new IllegalStateException("permission enum value "
                    + permissionEnum.getId() + " used multiple times");
            }

            map.put(permissionEnum.getId(), permissionEnum);
        }

        VALUES_MAP = Collections.unmodifiableMap(map);
    }

    private final Integer id;
    private final String name;
    private final EnumSet<Require> requires;

    private PermissionEnum(Integer id, String name, Require... requires) {
        this.id = id;
        this.name = name;
        this.requires = EnumSet.of(Require.DEFAULT, requires);
    }

    public static List<PermissionEnum> valuesList() {
        return VALUES_LIST;
    }

    public static Map<Integer, PermissionEnum> valuesMap() {
        return VALUES_MAP;
    }

    public Integer getId() {
        return id;
    }

    public EnumSet<Require> getRequires() {
        return EnumSet.copyOf(requires);
    }

    public String getName() {
        return name;
    }

    public static PermissionEnum fromId(Integer id) {
        return valuesMap().get(id);
    }

    /**
     * Whether the given {@link User} has this {@link PermissionEnum} on
     * <em>any</em> {@link Center} or {@link Study}.
     * 
     * @see {@link #isMembershipAllowed(Membership, Center, Study)}
     * @param user
     * @return
     */
    public boolean isAllowed(User user) {
        return isAllowed(user, null, null);
    }

    /**
     * Whether the given {@link User} has this {@link PermissionEnum} on
     * <em>any</em> {@link Center}, but a specific {@link Study}.
     * 
     * @see {@link #isAllowed(User)}
     * @param user
     * @return
     */
    public boolean isAllowed(User user, Study study) {
        return isAllowed(user, null, study);
    }

    /**
     * Whether the given {@link User} has this {@link PermissionEnum} on
     * <em>any</em> {@link Study}, but a specific {@link Center}.
     * 
     * @see {@link #isAllowed(User)}
     * @param user
     * @return
     */
    public boolean isAllowed(User user, Center center) {
        return isAllowed(user, center, null);
    }

    /**
     * 
     * @param user
     * @param center if null, {@link Center} does not matter.
     * @param study if null, {@link Study} does not matter.
     * @return
     */
    public boolean isAllowed(User user, Center center, Study study) {
        for (Membership m : user.getAllMemberships()) {
            if (isMembershipAllowed(m, center, study)) return true;
        }
        return false;
    }

    /**
     * Return true if special requirements (i.e. {@link Require}-s) are met on
     * the given {@link Membership}, otherwise false.
     * 
     * @param m
     * @return
     */
    public boolean isRequirementsMet(Membership m) {
        boolean reqsMet = true;
        Domain d = m.getDomain();
        reqsMet &= !requires.contains(Require.ALL_CENTERS) || d.isAllCenters();
        reqsMet &= !requires.contains(Require.ALL_STUDIES) || d.isAllStudies();
        return reqsMet;
    }

    /**
     * This is a confusing check. If {@link Center} is null, it means we do not
     * care about its value, otherwise, {@link Domain#contains(Center)} must be
     * true. The same applies to {@link Study}.
     * 
     * @param m
     * @param c
     * @param s
     * @return
     */
    private boolean isMembershipAllowed(Membership m, Center c, Study s) {
        boolean requiresMet = isRequirementsMet(m);
        boolean hasCenter = c == null || m.getDomain().contains(c);
        boolean hasStudy = s == null || m.getDomain().contains(s);
        boolean hasPermission = m.getAllPermissions().contains(this);

        boolean allowed = requiresMet && hasCenter && hasStudy && hasPermission;
        return allowed;
    }

    /**
     * Defines special requirements for a {@link PermissionEnum}.
     * 
     * @author jferland
     * 
     */
    public enum Require implements NotAProxy, Serializable {
        /**
         * Does nothing but make creating {@link EnumSet}-s easier.
         */
        DEFAULT,

        /**
         * If present, the {@link PermissionEnum} must exist in a
         * {@link Membership} for which its {@link Domain#isAllCenters()}
         * returns true.
         */
        ALL_CENTERS,

        /**
         * If present, the {@link PermissionEnum} must exist in a
         * {@link Membership} for which its {@link Domain#isAllStudies()}
         * returns true.
         */
        ALL_STUDIES;
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
