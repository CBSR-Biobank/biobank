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

    private Map<String, ProtectionElementRole> peRolesMap;

    public Group(Long id, String name) {
        this.id = id;
        this.name = name;
        peRolesMap = new HashMap<String, ProtectionElementRole>();
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

    public void addProtectionElementRole(String objectName, Set<Role> roles,
        String objectId) {
        ProtectionElementRole per = peRolesMap.get(objectName);
        if (per == null) {
            per = new ProtectionElementRole(objectName, objectId);
            peRolesMap.put(objectName, per);
        }
        per.addRoles(roles);
    }

    public Collection<ProtectionElementRole> getProtectionElementRole() {
        return peRolesMap.values();
    }

    @Override
    public String toString() {
        return getId() + "/" + getName();
    }

    public boolean hasRoleOnObject(Role role, String objectName, String objectId) {
        ProtectionElementRole per = peRolesMap.get(objectName);
        if (per == null) {
            return false;
        }
        if (per.getRoles().contains(role)) {
            if (objectId == null) {
                return true;
            }
            return objectId.equals(per.getObjectId());
        }
        return false;
    }

}
