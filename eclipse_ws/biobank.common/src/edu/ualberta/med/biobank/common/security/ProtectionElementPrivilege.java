package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ProtectionElementPrivilege implements Serializable,
    BiobankSecurity {

    private static final long serialVersionUID = 1L;

    private String objectName;

    private Set<Privilege> privileges;

    private String objectId;

    public ProtectionElementPrivilege(String objectName, String objectId) {
        this.objectName = objectName;
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectId() {
        return objectId;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void addPrivileges(Set<Privilege> privileges) {
        if (this.privileges == null) {
            this.privileges = new HashSet<Privilege>();
        }
        this.privileges.addAll(privileges);
    }

}
