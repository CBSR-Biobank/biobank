package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;

@Deprecated
public class Group implements Serializable, NotAProxy {

    private static final long serialVersionUID = 1L;

    // FIXME just remember the ID that should never change ?
    public static final String GROUP_SUPER_ADMIN = "Super Administrator"; //$NON-NLS-1$
    // need the id if is trying to rename it. What is the best ? Are we sure
    // this will be always initialized that way ?
    public static final Long GROUP_SUPER_ADMIN_ID = 5L;

    // FIXME just remember the ID that should never change ?
    public static final String PG_CENTER_ADMINISTRATOR = "Internal: Center Administrator"; //$NON-NLS-1$
    // same as above
    public static final Long PG_CENTER_ADMINISTRATOR_ID = 45L;

    public static final String CENTER_FULL_ACCESS = "Center Full Access"; //$NON-NLS-1$
    public static final String READ_ONLY = "Read Only"; //$NON-NLS-1$
    public static final String OBJECT_FULL_ACCESS = "Object Full Access"; //$NON-NLS-1$

    private Long id;

    private String name;

    /**
     * Map a protection element to a list of privileges
     */
    private Map<ProtectionElement, Set<Privilege>> pePrivilegeMap;

    /**
     * Map a protection group name to a ProtectionGroupPrivilege object
     */
    private Map<String, ProtectionGroupPrivilege> pgMap;

    private List<Integer> workingCenterIds;
    private List<Integer> globalFeaturesEnabledId;
    private List<Integer> centerFeaturesEnabledId;
    private Boolean isWorkingCentersAdministrator;

    public Group() {
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>();
        pgMap = new HashMap<String, ProtectionGroupPrivilege>();
        workingCenterIds = new ArrayList<Integer>();
        globalFeaturesEnabledId = new ArrayList<Integer>();
        centerFeaturesEnabledId = new ArrayList<Integer>();
        isWorkingCentersAdministrator = false;
    }

    public Group(Long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSuperAdministratorGroup() {
        return name != null && name.equals(GROUP_SUPER_ADMIN);
    }

    public void addProtectionElementPrivilege(String type, String id,
        Set<Privilege> newPrivileges) {
        ProtectionElement pep = new ProtectionElement(type, id);
        Set<Privilege> privileges = pePrivilegeMap.get(pep);
        if (privileges == null) {
            privileges = new HashSet<Privilege>();
            pePrivilegeMap.put(pep, privileges);
        }
        privileges.addAll(newPrivileges);
    }

    public void addProtectionGroupPrivilege(Long id, String name,
        String description, Set<Privilege> newPrivileges) {
        ProtectionGroupPrivilege pgp = pgMap.get(name);
        if (pgp == null) {
            pgp = new ProtectionGroupPrivilege(id, name, description);
            pgMap.put(name, pgp);
            if (pgp.getName().startsWith(
                BiobankCSMSecurityUtil.CENTER_FEATURE_START_NAME)) {
                centerFeaturesEnabledId.add((int) pgp.getId().longValue());
            } else if (pgp.getName().startsWith(
                BiobankCSMSecurityUtil.GLOBAL_FEATURE_START_NAME)) {
                globalFeaturesEnabledId.add((int) pgp.getId().longValue());
            }
        }
        pgp.addPrivileges(newPrivileges);
    }

    /**
     * will check the privilege on the protection element with no id specified
     * on it.
     */
    public boolean hasPrivilegeOnObject(Privilege privilege,
        String objectClassName) {
        ProtectionElement pep = new ProtectionElement(objectClassName,
            (Integer) null);
        Set<Privilege> privileges = pePrivilegeMap.get(pep);
        if (privileges == null) {
            return false;
        }
        return privileges.contains(privilege);
    }

    @Override
    public String toString() {
        return getId() + "/" + getName(); //$NON-NLS-1$
    }

    public Map<ProtectionElement, Set<Privilege>> getPrivilegesMap() {
        return pePrivilegeMap;
    }

    public Map<String, ProtectionGroupPrivilege> getProtectionGroupMap() {
        return pgMap;
    }

    /**
     * @return true if this group is administrator of the center with id
     *         centerId
     */
    public boolean isAdministratorForCenter(CenterWrapper<?> center) {
        return hasPrivilegeOnProtectionGroup(Privilege.UPDATE,
            PG_CENTER_ADMINISTRATOR, center);
    }

    /**
     * @return true is is administrator of working centers of this group.
     */
    public boolean getIsWorkingCentersAdministrator() {
        return isWorkingCentersAdministrator;
    }

    /**
     * set if the group is administrator of working centers of this group.
     */
    public void setIsWorkingCentersAdministrator(boolean admin) {
        isWorkingCentersAdministrator = admin;
    }

    /**
     * Check privilege on a protection group for a specific center, if this
     * center is a working center for this group
     */
    public boolean hasPrivilegeOnProtectionGroup(Privilege privilege,
        String protectionGroupName, CenterWrapper<?> center) {
        if (center != null && getWorkingCenterIds().contains(center.getId())) {
            ProtectionGroupPrivilege pgv = pgMap.get(protectionGroupName);
            if (pgv == null) {
                return false;
            }
            return pgv.getPrivileges().contains(privilege);
        }
        return false;
    }

    public void copy(Group group) {
        id = group.getId();
        name = group.getName();
        isWorkingCentersAdministrator = group.isWorkingCentersAdministrator;
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>(
            group.pePrivilegeMap);
        pgMap = new HashMap<String, ProtectionGroupPrivilege>(group.pgMap);
        workingCenterIds = new ArrayList<Integer>(group.workingCenterIds);
        globalFeaturesEnabledId = new ArrayList<Integer>(
            group.globalFeaturesEnabledId);
        centerFeaturesEnabledId = new ArrayList<Integer>(
            group.centerFeaturesEnabledId);
    }

    public boolean canBeDeleted() {
        return !GROUP_SUPER_ADMIN.equals(name);
    }

    public boolean canBeEdited() {
        return !GROUP_SUPER_ADMIN.equals(name)
            && !GROUP_SUPER_ADMIN_ID.equals(id);
    }

    public void setWorkingCenterIds(List<Integer> workingCenterIds) {
        this.workingCenterIds = workingCenterIds;
    }

    public List<Integer> getWorkingCenterIds() {
        return workingCenterIds;
    }

    public List<Integer> getGlobalFeaturesEnabled() {
        return globalFeaturesEnabledId;
    }

    public void setGlobalFeaturesEnabled(List<Integer> globalFeaturesEnabledId) {
        this.globalFeaturesEnabledId = globalFeaturesEnabledId;
    }

    public List<Integer> getCenterFeaturesEnabled() {
        return centerFeaturesEnabledId;
    }

    public void setCenterFeaturesEnabled(List<Integer> centerFeaturesEnabledId) {
        this.centerFeaturesEnabledId = centerFeaturesEnabledId;
    }
}
