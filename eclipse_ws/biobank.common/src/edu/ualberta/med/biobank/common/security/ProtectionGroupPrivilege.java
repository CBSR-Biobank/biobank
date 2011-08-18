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

    private Long id;

    private String name;

    private String description;

    /**
     * List of privileges associated to this protection group
     */
    private Set<Privilege> privileges;

    public ProtectionGroupPrivilege(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
            if (getId() != null && pgp.getId() != null)
                return getId().equals(pgp.getId());
            return getName().equals(pgp.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        assert false : "hashCode not designed"; //$NON-NLS-1$
        return 42; // any arbitrary constant will do
    }

    @Override
    public String toString() {
        return getName() + "/" + getPrivileges(); //$NON-NLS-1$
    }

}
