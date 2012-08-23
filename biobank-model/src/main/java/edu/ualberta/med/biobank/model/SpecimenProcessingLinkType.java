package edu.ualberta.med.biobank.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Amount;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents a regularly performed procedure involving two {@link Specimen}s:
 * an input, which must be in a specific {@link SpecimenGroup} (via
 * {@link #inputGroup}), and an output, which must be in a specific
 * {@link SpecimenGroup} (via {@link #outputGroup}). Each combination of
 * {@link #inputGroup} and {@link #outputGroup}) may exist only once per
 * {@link #type}, to avoid redundancy.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_LINK_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "PROCESSING_TYPE_ID",
        "INPUT_SPECIMEN_GROUP_ID",
        "OUTPUT_SPECIMEN_GROUP_ID"
    })
})
@Unique(properties = { "processingType", "inputGroup", "outputGroup" }, groups = PrePersist.class)
@NotUsed(by = SpecimenProcessingLink.class, property = "specimenProcessingLinkType", groups = PreDelete.class)
public class SpecimenProcessingLinkType
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private ProcessingType type;
    private SpecimenGroup inputGroup;
    private SpecimenGroup outputGroup;
    private Amount expectedInputChange;
    private Amount expectedOutputChange;
    private Vessel outputVessel;
    private Integer outputCount;

    /**
     * @return the {@link ProcessingType} that this
     *         {@link SpecimenProcessingLinkType} belongs to, which contains
     *         additional textual information and descriptions.
     */
    @NotNull(message = "{SpecimenProcessingLinkType.type.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_TYPE_ID", nullable = false)
    public ProcessingType getType() {
        return type;
    }

    public void setType(ProcessingType type) {
        this.type = type;
    }

    /**
     * @return the {@link SpecimenGroup} that the input {@link Specimen}(s) of
     *         this process must be in.
     */
    @NotNull(message = "{SpecimenProcessingLinkType.inputGroup.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getInputGroup() {
        return inputGroup;
    }

    public void setInputGroup(SpecimenGroup inputGroup) {
        this.inputGroup = inputGroup;
    }

    /**
     * @return the {@link SpecimenGroup} that the output {@link Specimen}(s) of
     *         this process will be in.
     */
    @NotNull(message = "{SpecimenProcessingLinkType.outputGroup.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getOutputGroup() {
        return outputGroup;
    }

    public void setOutputGroup(SpecimenGroup outputGroup) {
        this.outputGroup = outputGroup;
    }

    /**
     * @return the amount expected to be removed from each input
     *         {@link Specimen} that undergoes this process, or null if
     *         unspecified.
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value.value", column = @Column(name = "EXPECTED_INPUT_CHANGE_VALUE")),
        @AttributeOverride(name = "value.scale", column = @Column(name = "EXPECTED_INPUT_CHANGE_SCALE")),
        @AttributeOverride(name = "unit", column = @Column(name = "EXPECTED_INPUT_CHANGE_UNIT"))
    })
    public Amount getExpectedInputChange() {
        return expectedInputChange;
    }

    public void setExpectedInputChange(Amount expectedInputChange) {
        this.expectedInputChange = expectedInputChange;
    }

    /**
     * @return the amount expected to be added to each output {@link Specimen}
     *         resulting from this process, or null if unspecified.
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value.value", column = @Column(name = "EXPECTED_OUTPUT_CHANGE_VALUE")),
        @AttributeOverride(name = "value.scale", column = @Column(name = "EXPECTED_OUTPUT_CHANGE_SCALE")),
        @AttributeOverride(name = "unit", column = @Column(name = "EXPECTED_OUTPUT_CHANGE_UNIT"))
    })
    public Amount getExpectedOutputChange() {
        return expectedOutputChange;
    }

    public void setExpectedOutputChange(Amount expectedOutputChange) {
        this.expectedOutputChange = expectedOutputChange;
    }

    /**
     * @return the default type of {@link Vessel} that resulting
     *         {@link Specimen} outputs are expected to be put into.
     */
    @NotNull(message = "{SpecimenProcessingLinkType.outputVessel.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VESSEL_ID", nullable = false)
    public Vessel getOutputVessel() {
        return outputVessel;
    }

    public void setOutputVessel(Vessel outputVessel) {
        this.outputVessel = outputVessel;
    }

    /**
     * @return the number of expected resulting output {@link Specimen}s when
     *         this process is carried out, or null if unspecified.
     */
    @Min(value = 1, message = "{CollectionEvent.outputCount.Min}")
    @Column(name = "OUTPUT_COUNT")
    public Integer getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(Integer outputCount) {
        this.outputCount = outputCount;
    }
}
