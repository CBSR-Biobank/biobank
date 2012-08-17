package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents a regularly performed procedure carried out on a specific input
 * {@link SpecimenGroup} (i.e. {@link #getInputGroup()}) with a resulting
 * specific output {@link SpecimenGroup} (i.e. {@link #getOutputGroup()}). Each
 * combination of {@link #inputGroup} and {@link #outputGroup}) may exist only
 * once per {@link #processingStep}, to avoid redundancy.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_STEP", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "PROCESSING_STEP_ID",
        "INPUT_SPECIMEN_GROUP_ID",
        "OUTPUT_SPECIMEN_GROUP_ID"
    })
})
@Unique(properties = { "processingStep", "inputGroup", "outputGroup" }, groups = PrePersist.class)
@NotUsed(by = SpecimenProcessing.class, property = "specimenProcessingStep", groups = PreDelete.class)
public class SpecimenProcessingStep
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private ProcessingStep processingStep;
    private Boolean enabled;
    private SpecimenGroup inputGroup;
    private SpecimenGroup outputGroup;
    private Amount amountToAdd;
    private Vessel outputVessel;
    private Integer outputCount;

    /**
     * @return the {@link ProcessingStep} that this
     *         {@link SpecimenProcessingStep} belongs to, which contains
     *         additional textual information and descriptions.
     */
    @NotNull(message = "{SpecimenProcessingStep.processingStep.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_ID", nullable = false)
    public ProcessingStep getProcessingStep() {
        return processingStep;
    }

    public void setProcessingStep(ProcessingStep processingStep) {
        this.processingStep = processingStep;
    }

    /**
     * @return true if this {@link SpecimenProcessingStep} is still being
     *         performed, or false if this {@link SpecimenProcessingStep} is no
     *         longer being carried out, but exists for historical and
     *         record-keeping purposes.
     */
    @NotNull(message = "{SpecimenProcessingStep.enabled.NotNull}")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the {@link SpecimenGroup} of a {@link Specimen} to be used as a
     *         source input in this {@link SpecimenProcessingStep}.
     */
    @NotNull(message = "{SpecimenProcessingStep.inputGroup.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getInputGroup() {
        return inputGroup;
    }

    public void setInputGroup(SpecimenGroup inputGroup) {
        this.inputGroup = inputGroup;
    }

    /**
     * @return the {@link SpecimenGroup} of a {@link Specimen} to be used as a
     *         result output in this {@link SpecimenProcessingStep}.
     */
    @NotNull(message = "{SpecimenProcessingStep.outputGroup.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getOutputGroup() {
        return outputGroup;
    }

    public void setOutputGroup(SpecimenGroup outputGroup) {
        this.outputGroup = outputGroup;
    }

    /**
     * @return the expected amount of substance to be added to each
     *         {@link Specimen}, or null if unspecified.
     */
    @Valid
    @Embedded
    public Amount getAmountToAdd() {
        return amountToAdd;
    }

    public void setAmountToAdd(Amount amountToAdd) {
        this.amountToAdd = amountToAdd;
    }

    /**
     * @return the expected (or default) {@link Vessel} of the {@link Specimen}s
     *         resulting from this process being carried out.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_VESSEL_ID")
    public Vessel getOutputVessel() {
        return outputVessel;
    }

    public void setOutputVessel(Vessel outputVessel) {
        this.outputVessel = outputVessel;
    }

    /**
     * @return the number of {@link Specimen}s that are expected to result from
     *         this process being carried out, or null if unspecified.
     */
    @Column(name = "OUTPUT_COUNT")
    public Integer getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(Integer outputCount) {
        this.outputCount = outputCount;
    }
}
