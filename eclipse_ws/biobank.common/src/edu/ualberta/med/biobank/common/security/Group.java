package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Site;

public class Group implements Serializable, NotAProxy {

    private static final long serialVersionUID = 1L;

    // FIXME just remember the ID that should never change ?
    public static final String GROUP_WEBSITE_ADMINISTRATOR = "Website Administrator";
    // need the id if is trying to rename it. What is the best ? Are we sure
    // this will be always initialized that way ?
    public static final Long GROUP_WEBSITE_ADMINISTRATOR_ID = 5L;

    // FIXME just remember the ID that should never change ?
    public static final String PG_SITE_ADMINISTRATION = "Site Administration Features";

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

    public Group() {

    }

    public Group(Long id, String name) {
        this.id = id;
        this.name = name;
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>();
        pgMap = new HashMap<String, ProtectionGroupPrivilege>();
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

    public boolean isWebsiteAdministrator() {
        return name != null && name.equals(GROUP_WEBSITE_ADMINISTRATOR);
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

    public boolean isSiteAdministrator(Integer siteId) {
        return hasPrivilegeOnProtectionGroup(Privilege.UPDATE,
            PG_SITE_ADMINISTRATION, siteId);
    }

    public boolean hasPrivilegeOnProtectionGroup(Privilege privilege,
        String protectionGroupName, Integer siteId) {
        ProtectionGroupPrivilege pgv = pgMap.get(protectionGroupName);
        if (pgv == null) {
            return false;
        }
        return pgv.getPrivileges().contains(privilege)
            && (siteId == null || hasPrivilegeOnObject(privilege,
                Site.class.getName(), siteId));
    }

    public void copy(Group group) {
        id = group.getId();
        name = group.getName();
    }

    public boolean canBeDeleted() {
        return !GROUP_WEBSITE_ADMINISTRATOR.equals(name);
    }

    public boolean canBeEdited() {
        return !GROUP_WEBSITE_ADMINISTRATOR.equals(name)
            && !GROUP_WEBSITE_ADMINISTRATOR_ID.equals(id);
    }
}
