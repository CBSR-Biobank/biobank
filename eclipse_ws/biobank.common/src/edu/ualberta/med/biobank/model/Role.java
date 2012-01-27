package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

public class Role extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Collection<Permission> permissionCollection =
        new HashSet<Permission>();

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Permission> getPermissionCollection() {
        return permissionCollection;
    }

    public void setPermissionCollection(
        Collection<Permission> permissionCollection) {
        this.permissionCollection = permissionCollection;
    }
}
