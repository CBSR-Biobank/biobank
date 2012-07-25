package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

/**
 * A {@link User} should only be able to create
 * {@link getManageablePermissionsMembership}-s on other {@link User}-s that are
 * of a lesser {@link Rank} and {@link #getLevel()} than his or her own.
 * <p>
 * Having a {@link Role} grants manager access on all getManageablePermissionsof
 * the individual {@link PermissionEnum}-s in that {@link Role}; however, having
 * all of the {@link PermissionEnum}-s in a {@link Role} does <em>not</em> mean
 * a {@link Membership} has that role.
 * <p>
 * Manageable means that <em>some</em> portion of the {@link Membership} can be
 * manipulated (e.g. some {@link PermissionEnum}-s or {@link Role}-s can be
 * added or removed).
 * <p>
 * Also note that it is easier to <em>Directly</em> modify the elements of a
 * collection (e.g. a {@link Membership})
 * 
 * @author Jonathan Ferland
 */
@Entity
@Table(name = "MEMBERSHIP")
public class Membership extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Membership",
        "Memberships");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString EVERY_PERMISSION = bundle.trc(
            "model",
            "Has Every Permission").format();
        public static final LString USER_MANAGER = bundle.trc(
            "model",
            "Can Manage Users").format();
    }

    private Domain domain = new Domain();
    private Set<PermissionEnum> permissions = new HashSet<PermissionEnum>(0);
    private Set<Role> roles = new HashSet<Role>(0);
    private boolean userManager = false;
    private boolean everyPermission = false;

    public Membership() {
    }

    @Deprecated
    public Membership(Membership m, Principal p) {
        p.getMemberships().add(this);

        setDomain(new Domain(m.getDomain()));

        setUserManager(m.isUserManager());
        setEveryPermission(m.isEveryPermission());

        getPermissions().addAll(m.getPermissions());
        getRoles().addAll(m.getRoles());
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Membership.domain.NotNull}")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DOMAIN_ID", unique = true)
    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @ElementCollection(targetClass = PermissionEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "MEMBERSHIP_PERMISSION",
        joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "PERMISSION_ID", nullable = false)
    @Type(type = "permissionEnum")
    public Set<PermissionEnum> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<PermissionEnum> permissions) {
        this.permissions = permissions;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MEMBERSHIP_ROLE",
        joinColumns = { @JoinColumn(name = "MEMBERSHIP_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) })
    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Column(name = "USER_MANAGER")
    public boolean isUserManager() {
        return userManager;
    }

    public void setUserManager(boolean userManager) {
        this.userManager = userManager;
        if (userManager) setEveryPermission(true);
    }

    @Column(name = "EVERY_PERMISSION")
    public boolean isEveryPermission() {
        return everyPermission || isUserManager();
    }

    public void setEveryPermission(boolean everyPermission) {
        this.everyPermission = everyPermission;
        if (!everyPermission) setUserManager(false);
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
        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        int maxSize = PermissionEnum.valuesList().size();
        for (Membership membership : user.getAllMemberships()) {
            if (isManageable(membership)) {
                permissions.addAll(membership.getAllPermissions());
            }
            // early out if we already have all permissions
            if (permissions.size() == maxSize) break;
        }
        return permissions;
    }

    /**
     * Get a {@link Set} of <em>all</em> {@link PermissionEnum}-s that this
     * {@link Membership} has directly, through its {@link Role}-s, and
     * considering its {{@link #isEveryPermission()} value (if true, then all
     * {@link PermissionEnum}-s are included).
     * 
     * @return
     */
    @Transient
    public Set<PermissionEnum> getAllPermissions() {
        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        if (isEveryPermission()) {
            permissions.addAll(PermissionEnum.valuesList());
        } else {
            permissions.addAll(getPermissions());
            for (Role role : getRoles()) {
                permissions.addAll(role.getPermissions());
            }
        }
        return permissions;
    }

    /**
     * Removes redundant {@link PermissionEnum}-s that are already reachable
     * through {@link #getRoles()} and {@link #isEveryPermission()}.
     */
    @Transient
    public void reducePermissions() {
        Set<PermissionEnum> nonRedundantPerms = new HashSet<PermissionEnum>();
        nonRedundantPerms.addAll(getPermissions());
        for (Role role : getRoles()) {
            nonRedundantPerms.removeAll(role.getPermissions());
        }
        if (isEveryPermission()) {
            nonRedundantPerms.clear();
        }
        getPermissions().retainAll(nonRedundantPerms);
    }

    /**
     * Returns a {@link Set} of all the <strong>existing</strong> {@link Role}-s
     * on this {@link Membership} that the given {@link User} is allowed to
     * manage (add or remove from this {@link Membership}).
     * <p>
     * Note that if person X can manage all the {@link PermissionEnum}-s in a
     * {@link Role} that does </em>not</em> mean that person X can manage the
     * {@link Role}, as the {@link Role} may be changed later to include other
     * {@link PermissionEnum}-s.
     * 
     * @param user the manager, who is allowed to modify the returned
     *            {@link Role}-s
     * @param defaultAdminRoles which {@link Role}-s to add to the set if {
     *            {@link #isEveryPermission()} returns true
     * 
     * @return the {@link Role}-s that the manager can manipulate
     */
    @Transient
    public Set<Role> getManageableRoles(User user, Set<Role> defaultAdminRoles) {
        Set<Role> roles = new HashSet<Role>();
        for (Membership membership : user.getAllMemberships()) {
            if (isManageable(membership)) {
                if (membership.isEveryPermission()) {
                    roles.addAll(defaultAdminRoles);
                }
                roles.addAll(membership.getRoles());
            }
        }
        return roles;
    }

    /**
     * Return true if the given {@link User} is able to manage <em>all</em> of
     * the {@link PermissionEnum}-s and {@link Role}-s that this
     * {@link Membership} has, otherwise false.
     * <p>
     * Require at least one {@link PermissionEnum} or {@link Role} to be
     * manageable, otherwise an empty {@link Membership} is fully manageable by
     * default.
     * 
     * @param u other
     * @return
     */
    @Transient
    public boolean isFullyManageable(User u) {
        Set<PermissionEnum> manageablePerms = getManageablePermissions(u);
        if (!manageablePerms.containsAll(getPermissions())) return false;

        Set<Role> manageableRoles = getManageableRoles(u, getRoles());
        if (!manageableRoles.containsAll(getRoles())) return false;

        // otherwise an empty membership is fully manageable by default
        if (manageablePerms.isEmpty() && manageableRoles.isEmpty())
            return false;

        return true;
    }

    @Transient
    public boolean isPartiallyManageable(User u) {
        for (Membership membership : u.getAllMemberships()) {
            if (isManageable(membership)) return true;
        }
        return false;
    }

    /**
     * Return true if at least one {@link PermissionEnum} or {@link Role} in
     * this {@link Membership} can be managed by the given {@link Membership},
     * otherwise false. Several criteria must be met:
     * <ol>
     * <li>the given {@link Membership} must contain at least one
     * {@link PermissionEnum} or {@link Role}, otherwise it can't manage
     * anything.
     * <li>the given {@link Membership} must be able to manage users (i.e.
     * {@link Membership#isUserManager()} must be true)</li>
     * <li>if this {@link Membership} has every {@link PermissionEnum} (i.e.
     * {@link Membership#isEveryPermission()} return true), then that
     * {@link Membership} must also have it</li>
     * <li>that {@link Domain} must be a superset of this {@link Domain}</li>
     * </ol>
     * 
     * @param membership
     * @return
     */
    @Transient
    public boolean isManageable(Membership that) {
        if (that.getAllPermissions().isEmpty()
            && that.getRoles().isEmpty()) return false;

        if (!that.isUserManager()) return false;
        if (isEveryPermission() && !that.isEveryPermission()) return false;

        if (!that.getDomain().isSuperset(getDomain())) return false;

        return true;
    }
}
