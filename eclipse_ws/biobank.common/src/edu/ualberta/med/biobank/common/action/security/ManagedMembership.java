package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

/**
 * This is meant to be a <em>consistent</em> slice of a {@link Membership} that
 * the given {@link User} can manage.
 * <p>
 * The manager is remembered and used to determine
 * {@link #getPermissionOptions()} and {@link #getRoleOptions()} and may have
 * also been used to filter the manageable {@link PermissionEnum} and
 * {@link Role} sets, respectively. So, remember it for consistency.
 * 
 * @author Jonathan Ferland
 */
public class ManagedMembership implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final Membership delegate;
    private final User manager;
    private final Set<Role> allRoles;

    public ManagedMembership(Membership membership, User manager,
        Set<Role> allRoles) {
        this.manager = manager;
        this.allRoles = new HashSet<Role>(allRoles);

        delegate = new Membership();

        delegate.setId(membership.getId());
        delegate.setPrincipal(membership.getPrincipal());
        delegate.setCenter(membership.getCenter());
        delegate.setStudy(membership.getStudy());
        delegate.setRank(membership.getRank());
        delegate.setLevel(membership.getLevel());

        delegate.getPermissions().addAll(membership.getPermissions());
        delegate.getRoles().addAll(membership.getRoles());

        filter();
    }

    public Integer getId() {
        return delegate.getId();
    }

    public void setId(Integer id) {
        delegate.setId(id);
    }

    public Principal getPrincipal() {
        return delegate.getPrincipal();
    }

    public void setPrincipal(Principal principal) {
        delegate.setPrincipal(principal);
    }

    public Center getCenter() {
        return delegate.getCenter();
    }

    public void setCenter(Center center) {
        delegate.setCenter(center);
    }

    public Study getStudy() {
        return delegate.getStudy();
    }

    public void setStudy(Study study) {
        delegate.setStudy(study);
    }

    public Rank getRank() {
        return delegate.getRank();
    }

    public void setRank(Rank rank) {
        delegate.setRank(rank);
    }

    public short getLevel() {
        return delegate.getLevel();
    }

    public void setLevel(short level) {
        delegate.setLevel(level);
    }

    public Set<PermissionEnum> getPermissions() {
        return delegate.getPermissions();
    }

    public Set<Role> getRoles() {
        return delegate.getRoles();
    }

    public Set<PermissionEnum> getPermissionOptions() {
        return delegate.getManageablePermissions(manager);
    }

    public Set<Role> getRoleOptions() {
        return delegate.getManageableRoles(manager, allRoles);
    }

    /**
     * Retain only the {@link PermissionEnum}-s and {@link Role}-s that are
     * valid options.
     */
    private void filter() {
        delegate.getPermissions().retainAll(getPermissionOptions());
        delegate.getRoles().retainAll(getRoleOptions());
    }
}
