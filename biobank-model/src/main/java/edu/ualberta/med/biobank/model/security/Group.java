package edu.ualberta.med.biobank.model.security;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.HasDescription;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.util.NullUtil;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@DiscriminatorValue("G")
@Unique(properties = "name", groups = PrePersist.class)
public class Group extends Principal
    implements HasName, HasDescription {
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

    @Override
    @NotEmpty(message = "{Group.name.NotEmpty}")
    @Column(name = "NAME", unique = true)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "{Group.description.NotEmpty}")
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
