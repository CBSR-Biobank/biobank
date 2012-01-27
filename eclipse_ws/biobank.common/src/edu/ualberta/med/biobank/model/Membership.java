package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "MEMBERSHIP")
public class Membership extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Collection<Permission> permissionCollection =
        new HashSet<Permission>(0);
    private Center center;
    private Collection<Role> roleCollection = new HashSet<Role>(0);
    private Study study;
    private Principal principal;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MEMBERSHIP_PERMISSION",
        joinColumns = { @JoinColumn(name = "MEMBERSHIP_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "PERMISSION_ID", nullable = false, updatable = false) })
    public Collection<Permission> getPermissionCollection() {
        return this.permissionCollection;
    }

    public void setPermissionCollection(
        Collection<Permission> permissionCollection) {
        this.permissionCollection = permissionCollection;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID")
    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MEMBERSHIP_ROLE",
        joinColumns = { @JoinColumn(name = "MEMBERSHIP_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) })
    public Collection<Role> getRoleCollection() {
        return this.roleCollection;
    }

    public void setRoleCollection(Collection<Role> roleCollection) {
        this.roleCollection = roleCollection;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRINCIPAL_ID", nullable = false)
    public Principal getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
