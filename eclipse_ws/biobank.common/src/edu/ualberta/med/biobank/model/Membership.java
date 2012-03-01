package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "MEMBERSHIP",
    uniqueConstraints = {
        // this unique constraint only works when no value is null
        // TODO: could fix the null problem by having an in-between table?
        @UniqueConstraint(columnNames = { "PRINCIPAL_ID", "CENTER_ID",
            "STUDY_ID" }) })
@Unique(properties = { "principal", "center", "study" }, groups = PrePersist.class)
public class Membership extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Set<PermissionEnum> permissionCollection =
        new HashSet<PermissionEnum>(0);
    private Center center;
    private Set<Role> roleCollection = new HashSet<Role>(0);
    private Study study;
    private Principal principal;

    @ElementCollection(targetClass = PermissionEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "MEMBERSHIP_PERMISSION",
        joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "PERMISSION_ID", nullable = false)
    @Type(type = "permissionEnum")
    public Set<PermissionEnum> getPermissionCollection() {
        return this.permissionCollection;
    }

    public void setPermissionCollection(Set<PermissionEnum> permissionCollection) {
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
    public Set<Role> getRoleCollection() {
        return this.roleCollection;
    }

    public void setRoleCollection(Set<Role> roleCollection) {
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

    @NotNull(message = "{edu.ualberta.med.biobank.model.Membership.principal.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRINCIPAL_ID", nullable = false)
    public Principal getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * Returns a {@link Set} of all the {@link PermissionEnum}-s on this
     * {@link Membership} that the given {@link User} is allowed to manage (add
     * or remove from this {@link Membership}).
     * 
     * @param user the manager, who is allowed to modify the returned
     *            {@link PermissionEnum}-s
     * @return the {@link PermissionEnum}-s that the manager can manipulate
     */
    @Transient
    public Set<PermissionEnum> getManageablePermissions(User user) {
        // THIS IS WRONG!!! Need to collect the sister properties where
        // USER_MANAGEMENT is given.
        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        for (PermissionEnum permission : PermissionEnum.values()) {
            // There is probably a much more efficient way to do this, only, I
            // don't have the time right now, so, clearer is better.
            permission.isAllowed(user, center, study);
        }
        return permissions;
    }

    @Transient
    public boolean isRemovable(User user) {
        for (PermissionEnum permission : getPermissionCollection()) {
            // for (Membership)
        }
        for (Role role : getRoleCollection()) {

        }
        return true;
    }
}
