package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.util.NotAProxy;

/**
 * represent a protection group and its privileges
 */
public class ProtectionGroupPrivilege implements Serializable, NotAProxy {

    private static final long serialVersionUID = 1L;

    private String name;

    /**
     * List of privileges associated to this protection group
     */
    private Set<Privilege> privileges;

    public ProtectionGroupPrivilege(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void addPrivileges(Set<Privilege> newPrivileges) {
        if (privileges == null) {
            privileges = new HashSet<Privilege>();
        }
        privileges.addAll(newPrivileges);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProtectionGroupPrivilege) {
            ProtectionGroupPrivilege pgp = (ProtectionGroupPrivilege) obj;
            return getName().equals(pgp.getName());
        }
        return false;
    }

    @Override
    public String toString() {
        return getName() + "/" + getPrivileges();
    }

}
