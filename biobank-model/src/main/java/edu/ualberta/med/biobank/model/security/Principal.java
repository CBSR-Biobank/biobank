package edu.ualberta.med.biobank.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.util.CustomEnumType;

@Entity
@Table(name = "PRINCIPAL")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DISCRIMINATOR", columnDefinition = "CHAR(4)", discriminatorType = DiscriminatorType.STRING)
public abstract class Principal
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private Boolean admin;
    private Set<GlobalPermission> perms = new HashSet<GlobalPermission>(0);

    @NotNull(message = "{Principal.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return true if this {@link User} has absolute power in the system, like
     *         root, otherwise false if not an administrator.
     */
    @NotNull(message = "{User.admin.NotNull}")
    @Column(name = "IS_ADMIN", nullable = false)
    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @ElementCollection(targetClass = GlobalPermission.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "STUDY_MEMBERSHIP_PERMISSION",
        joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "PERMISSION_ID", nullable = false)
    @Type(
        type = "edu.ualberta.med.biobank.model.util.CustomEnumType",
        parameters = {
            @Parameter(
                name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.security.GlobalPermission"
            )
        })
    public Set<GlobalPermission> getPermissions() {
        return perms;
    }

    public void setPermissions(Set<GlobalPermission> permissions) {
        this.perms = permissions;
    }
}
