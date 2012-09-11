package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@DiscriminatorValue("U")
@Unique.List({
    @Unique(properties = "email", groups = PrePersist.class),
    @Unique(properties = "login", groups = PrePersist.class)
})
public class User
    extends Principal {
    private static final long serialVersionUID = 1L;

    // TODO: when a user registers, check existing UserContacts and update them
    // to point to the registered user. But careful about extra information that
    // is exposed as a result.
    private String login;
    private String fullName;
    private String email;
    private Boolean passwordChangeNeeded;
    private Boolean mailingListSubscriber;
    private Set<Group> groups = new HashSet<Group>(0);

    @NotEmpty(message = "{User.login.NotEmpty}")
    @Column(name = "LOGIN", unique = true)
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull(message = "{User.passwordChangeNeeded.NotNull}")
    @Column(name = "IS_PASSWORD_CHANGE_NEEDED", nullable = false)
    public Boolean getPasswordChangeNeeded() {
        return passwordChangeNeeded;
    }

    public void setPasswordChangeNeeded(Boolean passwordChangeNeeded) {
        this.passwordChangeNeeded = passwordChangeNeeded;
    }

    @NotNull(message = "{User.mailingListSubscriber.NotNull}")
    @Column(name = "IS_MAILING_LIST_SUBSCRIBER", nullable = false)
    public Boolean getMailingListSubscriber() {
        return mailingListSubscriber;
    }

    public void setMailingListSubscriber(Boolean mailingListSubscriber) {
        this.mailingListSubscriber = mailingListSubscriber;
    }

    @Column(name = "FULL_NAME")
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Email
    @Column(name = "EMAIL", unique = true)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_GROUP",
        joinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) })
    public Set<Group> getGroups() {
        return this.groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean isFullyManageable(User user) {
        if (!super.isFullyManageable(user)) return false;
        for (Group group : getGroups()) {
            if (!group.isFullyManageable(user)) return false;
        }
        return true;
    }

    /**
     * Returns all of this {@link User}'s {@link Memberships}, i.e. from both
     * the {@link User} directly and from the {@link Group}-s.
     * 
     * @return
     */
    @Transient
    public Set<Membership> getAllMemberships() {
        Set<Membership> memberships = new HashSet<Membership>();
        memberships.addAll(getMemberships());

        for (Group group : getGroups()) {
            memberships.addAll(group.getMemberships());
        }

        return memberships;
    }

    @Transient
    public Set<Domain> getManageableDomains() {
        Set<Domain> domains = new HashSet<Domain>();
        for (Membership membership : getAllMemberships()) {
            if (membership.isUserManager()) {
                domains.add(membership.getDomain());
            }
        }
        return domains;
    }

    /**
     * Get a {@link Set} of all the {@link Membership}-s the given {@link User}
     * is able to partially manage (i.e. for which
     * {@link Membership#isPartiallyManageable(User)} returns true).
     * 
     * @param u
     * @return
     */
    @Transient
    public Set<Membership> getManageableMemberships(User u) {
        Set<Membership> manageable = new HashSet<Membership>();
        for (Membership membership : getMemberships()) {
            if (membership.isPartiallyManageable(u)) {
                manageable.add(membership);
            }
        }
        return manageable;
    }
}
