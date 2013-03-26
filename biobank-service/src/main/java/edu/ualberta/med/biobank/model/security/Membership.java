package edu.ualberta.med.biobank.model.security;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.Study;

@Audited
@MappedSuperclass
public abstract class Membership<T extends Permission, U extends Role<T>>
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Principal principal;
    private Boolean mananger;

    @NotNull(message = "{Membership.principal.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRINCIPAL_ID", nullable = false)
    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * @return true if the {@link Principal} has all associated permissions and is able to create,
     *         edit, or remove other user memberships with the same owner (e.g. {@link Center} or
     *         {@link Study}), otherwise false.
     */
    @NotNull(message = "{Membership.manager.NotNull}")
    @Column(name = "IS_MANAGER", nullable = false)
    public Boolean isManager() {
        return mananger;
    }

    public void setManager(Boolean mananger) {
        this.mananger = mananger;
    }

    @Transient
    public abstract Set<U> getRoles();

    public abstract void setRoles(Set<U> roles);

}
