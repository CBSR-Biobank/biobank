package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Amount;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents a regularly performed procedure carried out on a specific input
 * {@link SpecimenGroup} (i.e. {@link #getInputGroup()}) with a resulting
 * specific output {@link SpecimenGroup} (i.e. {@link #getOutputGroup()}). Each
 * combination of {@link #inputGroup} and {@link #outputGroup}) may exist only
 * once per {@link #type}, to avoid redundancy.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "PROCESSING_TYPE_ID",
        "SPECIMEN_GROUP_ID" })
})
@Unique(properties = { "type", "group" }, groups = PrePersist.class)
@NotUsed(by = SpecimenProcessing.class, property = "type", groups = PreDelete.class)
public class SpecimenProcessingType
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private ProcessingType type;
    private SpecimenGroup group;
    private Amount expectedAmountChange;
    private final Set<AnnotationType> annotationTypes =
        new HashSet<AnnotationType>(0);

    /**
     * @return the {@link ProcessingType} that this
     *         {@link SpecimenProcessingType} belongs to, which contains
     *         additional textual information and descriptions.
     */
    @NotNull(message = "{SpecimenProcessingType.type.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_TYPE_ID", nullable = false)
    public ProcessingType getType() {
        return type;
    }

    public void setType(ProcessingType type) {
        this.type = type;
    }

    /**
     * @return the {@link SpecimenGroup} that can be processed as part of the
     *         {@link #type}.
     */
    @NotNull(message = "{SpecimenProcessingType.group.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getGroup() {
        return group;
    }

    public void setGroup(SpecimenGroup group) {
        this.group = group;
    }
}
