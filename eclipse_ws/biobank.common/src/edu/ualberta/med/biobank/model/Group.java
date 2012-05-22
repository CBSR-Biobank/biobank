package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.util.NullUtil;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@DiscriminatorValue("BbGroup")
@Unique(properties = "name", groups = PrePersist.class)
public class Group extends Principal
    implements HasName {
    public static final NameComparator NAME_COMPARATOR = new NameComparator();

    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Group",
        "Groups");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString DESCRIPTION = bundle.trc(
            "model",
            "Description").format();
    }

    private String name;
    private String description;
    private Set<User> users = new HashSet<User>(0);

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.BbGroup.name.NotEmpty}")
    @Column(name = "NAME", unique = true)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    // TODO: enforce this again, someday
    // @NotEmpty(message =
    // "{edu.ualberta.med.biobank.model.BbGroup.description.NotEmpty}")
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "GROUP_USER",
        joinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) })
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    private static class NameComparator
        implements Comparator<Group>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Group a, Group b) {
            if (a == null && b == null) return 0;
            if (a == null ^ b == null) return (a == null) ? -1 : 1;
            return NullUtil.cmp(a.getName(), b.getName());
        }
    }
}
