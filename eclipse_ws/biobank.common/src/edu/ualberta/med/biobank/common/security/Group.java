package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Group implements Serializable, BiobankSecurity {

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

    @Override
    public String toString() {
        return getId() + "/" + getName();
    }

    public boolean hasPrivilegeOnObject(Privilege privilege, String objectName) {
        ProtectionElementPrivilege per = pePrivilegeMap.get(objectName);
        if (per == null) {
            return false;
        }
        return per.getPrivileges().contains(privilege);
    }

}
