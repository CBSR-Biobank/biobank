package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.study.Preservation;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A standardised set of methods for preserving and storing {@link Specimen}s.
 * Potential examples include: frozen specimen, RNA later, fresh specimen,
 * slide, etc.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "PRESERVATION_TYPE")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed.List({
    @NotUsed(by = Preservation.class, property = "type", groups = PreDelete.class)
})
public class PreservationType
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
    @NotEmpty(message = "{PreservationType.name.NotEmpty}")
    @Size(max = MAX_NAME_LENGTH, message = "{PreservationType.name.Size}")
    @Column(name = "NAME", nullable = false, unique = true, length = MAX_NAME_LENGTH)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "{PreservationType.description.Size}")
    @Column(name = "DESCRIPTION", columnDefinition = "TEXT", length = MAX_DESCRIPTION_LENGTH)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
