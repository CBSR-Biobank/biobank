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
import org.hibernate.validator.constraints.Range;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

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
@Table(name = "MEMBERSHIP",
    uniqueConstraints = {
        // TODO: consider adding 'RANK' so can be admin on 1, user manager on 1
        // and a normal user for all? But there's no point of being an admin and
        // a user manager on the same center/ study combo, since admin
        // completely encompases manager. Actually, how about an "isManager"
        // field and an "isAllPermissions" field?
        @UniqueConstraint(columnNames = { "PRINCIPAL_ID", "NOT_NULL_CENTER_ID",
            "NOT_NULL_STUDY_ID" }) })
@Unique(properties = { "principal", "notNullCenterId", "notNullStudyId" }, groups = PrePersist.class)
public class Membership extends AbstractBiobankModel {
    public static final short MIN_LEVEL = 1;
    public static final short MAX_LEVEL = 1000;
    private static final long serialVersionUID = 1L;

    private Set<PermissionEnum> permissions = new HashSet<PermissionEnum>(0);
    private Center center;
    private Set<Role> roles = new HashSet<Role>(0);
    private Study study;
    private Principal principal;
    private Rank rank = Rank.NORMAL;
    private short level = MIN_LEVEL;

    public Membership() {
    }

    public Membership(Membership m, Principal p) {
        setPrincipal(p);

        setCenter(m.getCenter());
        setStudy(m.getStudy());
        setRank(m.getRank());
        setLevel(m.getLevel());

        getPermissions().addAll(m.getPermissions());
        getRoles().addAll(m.getRoles());
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Membership.rank.NotNull}")
    @Column(name = "RANK", nullable = false)
    @Type(type = "rank")
    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Range(min = MIN_LEVEL, max = MAX_LEVEL, message = "{edu.ualberta.med.biobank.model.Membership.level.Range}")
    @Column(name = "LEVEL", nullable = false)
    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
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
    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
     * considering its {@link Rank} (if {@link Rank#ADMINISTRATOR}, then all
     * {@link PermissionEnum}-s are included).
     * 
     * @return
     */
    @Transient
    public Set<PermissionEnum> getAllPermissions() {
        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        if (Rank.ADMINISTRATOR.equals(getRank())) {
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
     * through a {@link Role}.
     */
    @Transient
    public void reducePermissions() {
        Set<PermissionEnum> nonRedundantPerms = new HashSet<PermissionEnum>();
        nonRedundantPerms.addAll(getPermissions());
        for (Role role : getRoles()) {
            nonRedundantPerms.removeAll(role.getPermissions());
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
     * @param defaultAdminRoles which {@link Role}-s to add to the set if the
     *            {@link User} is an {@link Rank#ADMINISTRATOR}.
     * @return the {@link Role}-s that the manager can manipulate
     */
    @Transient
    public Set<Role> getManageableRoles(User user, Set<Role> defaultAdminRoles) {
        Set<Role> roles = new HashSet<Role>();
        for (Membership membership : user.getAllMemberships()) {
            if (isManageable(membership)) {
                if (Rank.ADMINISTRATOR.equals(membership.getRank())) {
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

    @Transient
    public boolean isRankGe(Rank rank) {
        return rank.isLe(getRank());
    }

    @Transient
    public boolean isGlobal() {
        return isAllCenters() && isAllStudies();
    }

    @Transient
    public boolean isAllCenters() {
        return getCenter() == null;
    }

    @Transient
    public boolean isAllStudies() {
        return getStudy() == null;
    }

    /**
     * Return true if at least one {@link PermissionEnum} or {@link Role} in
     * this {@link Membership} can be managed by the given {@link Membership},
     * otherwise false. Several criteria must be met:
     * <ol>
     * <li>this {@link Membership} is <em>not</em> equal to the given
     * {@link Membership}, to prevent using given power to remove itself
     * <li>the given {@link Membership} must contain at least one
     * {@link PermissionEnum} or {@link Role}, otherwise it can't manage
     * anything.
     * <li>the given {@link Membership} has a {@link Rank} of at least
     * {@link Rank#MANAGER}</li>
     * <li>this {@link Membership} has a {@link Rank} less than that of the
     * given {@link Membership} or an equal {@link Rank} but a lesser
     * {@link #getLevel()} than the given {@link Membership}. This will prevent
     * peers of equal power from editing each other (or themselves)</li>
     * <li>this {@link Membership} is in a smaller or equal realm of influence,
     * i.e. the other {@link Membership} "contains" this {@link Center} and
     * "contains" this {@link Study}
     * </ol>
     * 
     * @param membership
     * @return
     */
    @Transient
    public boolean isManageable(Membership that) {
        if (equals(that)) return false;

        if (that.getAllPermissions().isEmpty()
            && that.getRoles().isEmpty()) return false;

        if (that.getRank().isLt(Rank.MANAGER)) return false;

        if (that.getRank().isLt(getRank())) return false;
        if (that.getRank().equals(getRank()) && that.getLevel() <= getLevel())
            return false;

        if (that.getCenter() != null && !that.getCenter().equals(getCenter()))
            return false;
        if (that.getStudy() != null && !that.getStudy().equals(getStudy()))
            return false;

        return true;
    }

    /**
     * Provides a never-null {@link Center} identifier that can be used to
     * create a unique index on. This allows a unique index to be created on (
     * {@link #getPrincipal()}, {@link #getNotNullCenterId()}) since a null
     * {@link Center} is converted to zero value. This is particularly important
     * for MySQL, which would allow multiple {@link Membership} instances for
     * the same {@link Principal} that have a null {@link Center}.
     * 
     * @see {@link http://bugs.mysql.com/bug.php?id=17825}
     * @see {@link #getNotNullStudyId()}
     * @author Jonathan Ferland
     * @return the {@link Center} id, or null if no {@link Center}.
     */
    @Column(name = "NOT_NULL_CENTER_ID", nullable = false)
    Integer getNotNullCenterId() {
        return getCenter() != null ? getCenter().getId() : 0;
    }

    void setNotNullCenterId(Integer centerId) {
    }

    /**
     * Functions similar to {@link #getNotNullCenterId()} for the same reason.
     * 
     * @see {@link #getNotNullCenterId()}
     * @return
     */
    @Column(name = "NOT_NULL_STUDY_ID", nullable = false)
    Integer getNotNullStudyId() {
        return getStudy() != null ? getStudy().getId() : 0;
    }

    void setNotNullStudyId(Integer studyId) {
    }
}
