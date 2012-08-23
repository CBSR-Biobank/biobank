package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Amount;

/**
 * A record of the actual {@link Specimen}s and amounts involved in a
 * {@link SpecimenProcessingLinkType}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_LINK")
public class SpecimenProcessingLink
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Specimen input;
    private Specimen output;
    private SpecimenProcessingLinkType type;
    private Date timeDone;
    private Amount actualInputAmountChange;
    private Amount actualOutputAmountChange;
    private ProcessingEvent processingEvent;

    /**
     * @return the {@link Specimen} that was processed.
     */
    @NotNull(message = "{SpecimenProcessingLink.input.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_SPECIMEN_ID", nullable = false)
    public Specimen getInput() {
        return input;
    }

    public void setInput(Specimen input) {
        this.input = input;
    }

    /**
     * @return the {@link Specimen} that resulted from the process.
     */
    @NotNull(message = "{SpecimenProcessingLink.output.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_SPECIMEN_ID", nullable = false)
    public Specimen getOutput() {
        return output;
    }

    public void setOutput(Specimen output) {
        this.output = output;
    }

    /**
     * @return the type of processing that the involed {@link Specimen}s
     *         underwent.
     */
    @NotNull(message = "{SpecimenProcessingLink.type.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_PROCESSING_LINK_TYPE_ID", nullable = false)
    public SpecimenProcessingLinkType getType() {
        return type;
    }

    public void setType(SpecimenProcessingLinkType type) {
        this.type = type;
    }

    /**
     * @return when the processing happened, or null if unspecified.
     */
    @Past
    @Column(name = "TIME_DONE")
    public Date getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(Date timeDone) {
        this.timeDone = timeDone;
    }

    /**
     * @return the actual amount removed from the {@link #input}.
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value.value", column = @Column(name = "ACTUAL_INPUT_CHANGE_VALUE")),
        @AttributeOverride(name = "value.scale", column = @Column(name = "ACTUAL_INPUT_CHANGE_SCALE")),
        @AttributeOverride(name = "unit", column = @Column(name = "ACTUAL_INPUT_CHANGE_UNIT"))
    })
    public Amount getActualInputAmountChange() {
        return actualInputAmountChange;
    }

    public void setActualInputAmountChange(Amount actualInputAmountChange) {
        this.actualInputAmountChange = actualInputAmountChange;
    }

    /**
     * @return the actual amount added to the {@link #output}.
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value.value", column = @Column(name = "ACTUAL_OUTPUT_CHANGE_VALUE")),
        @AttributeOverride(name = "value.scale", column = @Column(name = "ACTUAL_OUTPUT_CHANGE_SCALE")),
        @AttributeOverride(name = "unit", column = @Column(name = "ACTUAL_OUTPUT_CHANGE_UNIT"))
    })
    public Amount getActualOutputAmountChange() {
        return actualOutputAmountChange;
    }

    public void setActualOutputAmountChange(Amount actualOutputAmountChange) {
        this.actualOutputAmountChange = actualOutputAmountChange;
    }

    /**
     * @return the {@link ProcessingEvent} that this process was carried out in,
     *         or null if none or if unknown.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID")
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }
}
