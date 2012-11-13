package edu.ualberta.med.biobank.model.study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

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

    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    private String name;
    private String description;
    private Boolean enabled;

    @Override
    @NotEmpty(message = "{Study.name.NotEmpty}")
    @Length(max = MAX_NAME_LENGTH, message = "{Study.name.Length}")
    @Column(name = "NAME", unique = true, nullable = false, length = MAX_NAME_LENGTH)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @NotNull(message = "{Study.description.NotNull}")
    @Length(max = MAX_DESCRIPTION_LENGTH, message = "{Study.description.Length}")
    @Column(name = "DESCRIPTION", nullable = false, length = MAX_NAME_LENGTH)
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
}
