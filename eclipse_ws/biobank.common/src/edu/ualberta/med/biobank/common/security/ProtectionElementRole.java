package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ProtectionElementRole implements Serializable, BiobankSecurity {

    private static final long serialVersionUID = 1L;

    private String objectName;

    private Set<Role> roles;

    private String objectId;

    public ProtectionElementRole(String objectName, String objectId) {
        this.objectName = objectName;
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectId() {
        return objectId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRoles(Set<Role> roles) {
        if (this.roles == null) {
            this.roles = new HashSet<Role>();
        }
        this.roles.addAll(roles);
    }

}
