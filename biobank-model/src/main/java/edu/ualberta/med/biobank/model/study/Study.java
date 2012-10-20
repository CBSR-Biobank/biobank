package edu.ualberta.med.biobank.model.study;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.HasDescription;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * ET: Research conducted on a specific group of people to discover a determined
 * result; has one specific protocol
 * 
 * caTissue Term - Collection Protocol: A set of written procedures that
 * describe how a biospecimen is collected.
 * 
 */
@Audited
@Entity
@Table(name = "STUDY")
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class)
})
@NotUsed(by = Patient.class, property = "study", groups = PreDelete.class)
public class Study
    extends VersionedLongIdModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Study",
        "Studies");

    private String name;
    private String description;
    private Set<Contact> contacts = new HashSet<Contact>(0);
    private Boolean enabled;

    @Override
    @NotEmpty(message = "{Study.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull(message = "{Study.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "STUDY_CONTACT",
        joinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CONTACT_ID", nullable = false, updatable = false) })
    public Set<Contact> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }
}
