package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    // same as above
    public static final Long PG_SITE_ADMINISTRATION_ID = 45L;

    public static final String SITE_FULL_ACCESS = "Site Full Access";
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

    private List<Integer> readOnlySitesId;
    private List<Integer> canUpdateSitesId;
    private List<Integer> featuresEnabledId;
    private Boolean isSiteAdministrator;

    public Group() {
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>();
        pgMap = new HashMap<String, ProtectionGroupPrivilege>();
        readOnlySitesId = new ArrayList<Integer>();
        canUpdateSitesId = new ArrayList<Integer>();
        featuresEnabledId = new ArrayList<Integer>();
        isSiteAdministrator = false;
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

    /**
     * @return true if this group is administrator of the site with id siteId
     */
    public boolean isSiteAdministrator(Integer siteId) {
        return hasPrivilegeOnProtectionGroup(Privilege.UPDATE,
            PG_SITE_ADMINISTRATION, siteId);
    }

    /**
     * @return true is is site administrator of sites the group can update.
     */
    public boolean getIsSiteAdministrator() {
        return isSiteAdministrator;
    }

    /**
     * set if the group is site administrator of sites the group can update.
     */
    public void setIsSiteAdministrator(boolean admin) {
        isSiteAdministrator = admin;
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
        isSiteAdministrator = group.isSiteAdministrator;
        pePrivilegeMap = new HashMap<ProtectionElement, Set<Privilege>>(
            group.pePrivilegeMap);
        pgMap = new HashMap<String, ProtectionGroupPrivilege>(group.pgMap);
        readOnlySitesId = new ArrayList<Integer>(group.readOnlySitesId);
        canUpdateSitesId = new ArrayList<Integer>(group.canUpdateSitesId);
    }

    public boolean canBeDeleted() {
        return !GROUP_WEBSITE_ADMINISTRATOR.equals(name);
    }

    public boolean canBeEdited() {
        return !GROUP_WEBSITE_ADMINISTRATOR.equals(name)
            && !GROUP_WEBSITE_ADMINISTRATOR_ID.equals(id);
    }

    public List<Integer> getReadOnlySites() {
        return readOnlySitesId;
    }

    public void setReadOnlySites(List<Integer> readOnlySitesId) {
        this.readOnlySitesId = readOnlySitesId;
    }

    public List<Integer> getCanUpdateSites() {
        return canUpdateSitesId;
    }

    public void setCanUpdateSites(List<Integer> readOnlySitesId) {
        this.canUpdateSitesId = readOnlySitesId;
    }

    public List<Integer> getFeaturesEnabled() {
        return featuresEnabledId;
    }

    public void setFeaturesEnabled(List<Integer> featuresEnabledId) {
        this.featuresEnabledId = featuresEnabledId;
    }
}
