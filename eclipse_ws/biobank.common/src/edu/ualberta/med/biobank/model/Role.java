package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;


@Entity
@Table(name = "ROLE")
public class Role extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Collection<PermissionEnum> permissionCollection =
        new HashSet<PermissionEnum>(0);

    @NotEmpty
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ElementCollection(targetClass = PermissionEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ROLE_PERMISSION",
        joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "PERMISSION_NAME")
    @Enumerated(EnumType.STRING)
    public Collection<PermissionEnum> getPermissionCollection() {
        return this.permissionCollection;
    }

    public void setPermissionCollection(
        Collection<PermissionEnum> permissionCollection) {
        this.permissionCollection = permissionCollection;
    }
}
