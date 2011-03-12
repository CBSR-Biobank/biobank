package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Center;

public class Group implements Serializable, NotAProxy {

    private static final long serialVersionUID = 1L;

    // FIXME just remember the ID that should never change ?
    public static final String GROUP_SUPER_ADMIN = "Super Administrator";
    // need the id if is trying to rename it. What is the best ? Are we sure
    // this will be always initialized that way ?
    public static final Long GROUP_SUPER_AMDIN_ID = 5L;

    // FIXME just remember the ID that should never change ?
    public static final String PG_CENTER_ADMINISTRATOR = "Internal: Center Administrator";
    // same as above
    public static final Long PG_CENTER_ADMINISTRATOR_ID = 45L;

    public static final String CENTER_FULL_ACCESS = "Center Full Access";
    public static final String READ_ONLY = "Read Only";
    public static final String OBJECT_FULL_ACCESS = "Object Full Access";

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
    private List<Integer> featuresEnabledId;
    private Boolean isCenterAdministrator;

    public Group() {
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>();
        pgMap = new HashMap<String, ProtectionGroupPrivilege>();
        workingCenterIds = new ArrayList<Integer>();
        featuresEnabledId = new ArrayList<Integer>();
        isCenterAdministrator = false;
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

    public boolean isSuperAdministrator() {
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

    public void addProtectionGroupPrivilege(String name,
        Set<Privilege> newPrivileges) {
        ProtectionGroupPrivilege pgp = pgMap.get(name);
        if (pgp == null) {
            pgp = new ProtectionGroupPrivilege(name);
            pgMap.put(name, pgp);
        }
        pgp.addPrivileges(newPrivileges);
    }

    /**
     * When no id is specified, type=name
     */
    public boolean hasPrivilegeOnObject(Privilege privilege, String type) {
        return hasPrivilegeOnObject(privilege, type, null);
    }

    /**
     * When no id is specified, type=name; otherwise, need to check the
     * protection element privilege type
     */
    public boolean hasPrivilegeOnObject(Privilege privilege, String type,
        Integer id) {
        ProtectionElement pep = new ProtectionElement(type, id);
        Set<Privilege> privileges = pePrivilegeMap.get(pep);
        if (privileges == null) {
            if (id == null) {
                return false;
            }
            return hasPrivilegeOnObject(privilege, type, null);
        }
        return privileges.contains(privilege);
    }

    @Override
    public String toString() {
        return getId() + "/" + getName();
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
    public boolean isAdministratorForCenter(Integer centerId,
        Class<? extends Center> centerClass) {
        return hasPrivilegeOnProtectionGroup(Privilege.UPDATE,
            PG_CENTER_ADMINISTRATOR, centerId, centerClass);
    }

    /**
     * @return true is is center administrator of centers the group can update.
     */
    public boolean getIsCenterAdministrator() {
        return isCenterAdministrator;
    }

    /**
     * set if the group is center administrator of centers the group can update.
     */
    public void setIsCenterAdministrator(boolean admin) {
        isCenterAdministrator = admin;
    }

    public boolean hasPrivilegeOnProtectionGroup(Privilege privilege,
        String protectionGroupName, Integer centerId,
        Class<? extends Center> centerClass) {
        ProtectionGroupPrivilege pgv = pgMap.get(protectionGroupName);
        if (pgv == null) {
            return false;
        }
        return pgv.getPrivileges().contains(privilege)
            && (centerId == null || hasPrivilegeOnObject(
                privilege,
                centerClass == null ? Center.class.getName() : centerClass
                    .getName(), centerId));
    }

    public void copy(Group group) {
        id = group.getId();
        name = group.getName();
        isCenterAdministrator = group.isCenterAdministrator;
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>(
            group.pePrivilegeMap);
        pgMap = new HashMap<String, ProtectionGroupPrivilege>(group.pgMap);
        workingCenterIds = new ArrayList<Integer>(group.workingCenterIds);
    }

    public boolean canBeDeleted() {
        return !GROUP_SUPER_ADMIN.equals(name);
    }

    public boolean canBeEdited() {
        return !GROUP_SUPER_ADMIN.equals(name)
            && !GROUP_SUPER_AMDIN_ID.equals(id);
    }

    public void setWorkingCenterIds(List<Integer> workingCenterIds) {
        this.workingCenterIds = workingCenterIds;
    }

    public List<Integer> getWorkingCenterIds() {
        return workingCenterIds;
    }

    public List<Integer> getFeaturesEnabled() {
        return featuresEnabledId;
    }

    public void setFeaturesEnabled(List<Integer> featuresEnabledId) {
        this.featuresEnabledId = featuresEnabledId;
    }

}
