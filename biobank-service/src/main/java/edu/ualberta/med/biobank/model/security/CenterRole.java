package edu.ualberta.med.biobank.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.util.CustomEnumType;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "CENTER_ROLE")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed(by = CenterMembership.class, property = "roles", groups = PreDelete.class)
public class CenterRole
    extends Role<CenterPermission> {
    private static final long serialVersionUID = 1L;

    private Set<CenterPermission> perms = new HashSet<CenterPermission>(0);

    @Override
    @ElementCollection(targetClass = CenterPermission.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "CENTER_ROLE_PERMISSION",
        joinColumns = @JoinColumn(name = "CENTER_ROLE_ID"))
    @Column(name = "CENTER_PERMISSION_ID", nullable = false)
    @Type(
        type = "edu.ualberta.med.biobank.model.util.CustomEnumType",
        parameters = {
            @Parameter(
                name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.security.CenterPermission"
            )
        })
    public Set<CenterPermission> getPermissions() {
        return this.perms;
    }

    @Override
    public void setPermissions(Set<CenterPermission> permissions) {
        this.perms = permissions;
    }
}
