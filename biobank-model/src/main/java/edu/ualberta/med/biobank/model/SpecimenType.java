package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A standardised set of classifications that describe <em>what</em> a
 * {@link Specimen} is. Potential examples include: urine, whole blood, plasma,
 * nail, protein, etc.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_TYPE")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed.List({
    @NotUsed(by = SpecimenGroup.class, property = "specimenType", groups = PreDelete.class)
})
public class SpecimenType
    extends VersionedLongIdModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    private String name;
    private String description;

    /**
     * @return a <em>unique</em> name, across all persisted instances of the
     *         descendant type.
     */
    @Override
    @NotEmpty(message = "{SpecimenType.name.NotEmpty}")
    @Size(max = MAX_NAME_LENGTH, message = "{SpecimenType.name.Size}")
    @Column(name = "NAME", nullable = false, unique = true, length = MAX_NAME_LENGTH)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "{SpecimenType.description.Size}")
    @Column(name = "DESCRIPTION", columnDefinition = "TEXT", length = MAX_DESCRIPTION_LENGTH)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
