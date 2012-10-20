package edu.ualberta.med.biobank.model.security;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.LongIdModel;
import edu.ualberta.med.biobank.model.type.PermissionEnum;
import edu.ualberta.med.biobank.model.util.NullUtil;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "ROLE")
@Unique(properties = "name", groups = PrePersist.class)
// TODO: check that no Membership uses this role before deleting
public class Role extends LongIdModel {
    public static final NameComparator NAME_COMPARATOR = new NameComparator();

    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Role",
        "Roles");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString NAME = bundle.trc(
            "model",
            "Name").format();
    }

    private String name;
    private Set<PermissionEnum> permissions = new HashSet<PermissionEnum>(0);

    @NotEmpty(message = "{Role.name.NotEmpty}")
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
    @Column(name = "PERMISSION_ID", nullable = false)
    @Type(type = "permissionEnum")
    public Set<PermissionEnum> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<PermissionEnum> permissions) {
        this.permissions = permissions;
    }

    private static class NameComparator
        implements Comparator<Role>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Role a, Role b) {
            if (a == null && b == null) return 0;
            if (a == null ^ b == null) return (a == null) ? -1 : 1;
            return NullUtil.cmp(a.getName(), b.getName());
        }
    }
}
