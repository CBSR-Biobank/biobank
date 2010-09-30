package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.util.NotAProxy;

public class Group implements Serializable, NotAProxy {
    public static final String GROUP_NAME_WEBSITE_ADMINISTRATOR = "Website Administrator";
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Map<String, ProtectionElementPrivilege> pePrivilegeMap;

    public Group(Long id, String name) {
        this.id = id;
        this.name = name;
        pePrivilegeMap = new HashMap<String, ProtectionElementPrivilege>();
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
        return name != null && name.equals(GROUP_NAME_WEBSITE_ADMINISTRATOR);
    }

    public void addProtectionElementPrivilege(String objectName,
        Set<Privilege> privileges, String objectId) {
        ProtectionElementPrivilege per = pePrivilegeMap.get(objectName);
        if (per == null) {
            per = new ProtectionElementPrivilege(objectName, objectId);
            pePrivilegeMap.put(objectName, per);
        }
        per.addPrivileges(privileges);
    }

    public Collection<ProtectionElementPrivilege> getProtectionElementPrivileges() {
        return pePrivilegeMap.values();
    }

    public boolean hasPrivilegeOnObject(Privilege privilege, String objectName) {
        ProtectionElementPrivilege per = pePrivilegeMap.get(objectName);
        if (per == null) {
            return false;
        }
        return per.getPrivileges().contains(privilege);
    }

    @Override
    public String toString() {
        return getId() + "/" + getName();
    }
}
