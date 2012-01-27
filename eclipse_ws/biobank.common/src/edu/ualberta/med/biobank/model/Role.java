package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ROLE")
public class Role extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Collection<Permission> permissionCollection =
        new HashSet<Permission>(0);

    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ROLE_PERMISSION",
        joinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "PERMISSION_ID", nullable = false, updatable = false) })
    public Collection<Permission> getPermissionCollection() {
        return this.permissionCollection;
    }

    public void setPermissionCollection(
        Collection<Permission> permissionCollection) {
        this.permissionCollection = permissionCollection;
    }
}
