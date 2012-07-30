package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "CENTER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR",
    discriminatorType = DiscriminatorType.STRING)
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = ProcessingEvent.class, property = "center", groups = PreDelete.class),
    @NotUsed(by = Dispatch.class, property = "senderCenter", groups = PreDelete.class),
    @NotUsed(by = Dispatch.class, property = "receiverCenter", groups = PreDelete.class),
    @NotUsed(by = Container.class, property = "center", groups = PreDelete.class),
    @NotUsed(by = ContainerType.class, property = "center", groups = PreDelete.class)
})
public class Center extends AbstractModel
    implements HasName, HasDescription, HasComments, HasAddress {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Center",
        "Centers");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString DST_DISPATCHES = bundle.trc(
            "model",
            "Destination Dispatches").format();
        public static final LString SRC_DISPATCHES = bundle.trc(
            "model",
            "Source Dispatches").format();
    }

    private String name;
    private String description;
    private Address address = new Address();
    private Boolean enabled;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @Override
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @NotEmpty(message = "{Center.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @NotNull(message = "{Center.address.NotNull}")
    @Embedded
    public Address getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @NotNull(message = "{Center.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "CENTER_COMMENT",
        joinColumns = { @JoinColumn(name = "CENTER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
