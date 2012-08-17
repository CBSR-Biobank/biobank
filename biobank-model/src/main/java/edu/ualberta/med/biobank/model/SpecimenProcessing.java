package edu.ualberta.med.biobank.model;

import java.util.Date;

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

import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

/**
 * This record could be interpreted as a parent-child relationship between two
 * {@link Specimen}s.
 * <p>
 * A {@link SpecimenProcessing} is a record of some process or procedure
 * (optionally represented by a {@link SpecimenProcessingStep}) being done on a
 * specific input {@link Specimen} (i.e. {@link #getInput()}) with a resulting
 * output {@link Specimen} (i.e. {@link #getOutput()}). There are two cases:
 * <ol>
 * <li>{@link #getInput()} <em>equals</em> {@link #getOutput()} - when a process
 * modifies the {@link #getInput()} itself.</li>
 * <li>{@link #getInput()} <em>does not</em> equal {@link #getOutput()} - when a
 * process yields a new {@link Specimen}.</li>
 * </ol>
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING")
public class SpecimenProcessing
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Specimen input;
    private Specimen output;
    private Amount amountAdded;
    private Date timeDone;
    private SpecimenProcessingStep specimenProcessingStep;
    private ProcessingEvent processingEvent;

    /**
     * @return the {@link Specimen} that was processed.
     */
    @NotNull(message = "{SpecimenProcessing.input.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @Index(name = "SPECIMEN_IO")
    @Column(name = "INPUT_SPECIMEN_ID", nullable = false)
    public Specimen getInput() {
        return input;
    }

    public void setInput(Specimen input) {
        this.input = input;
    }

    /**
     * @return the {@link Specimen} that resulted from the {@link #getInput()}
     *         {@link Specimen} being processed.
     */
    @NotNull(message = "{SpecimenProcessing.ouput.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @Index(name = "SPECIMEN_IO")
    @Column(name = "OUTPUT_SPECIMEN_ID", nullable = false)
    public Specimen getOutput() {
        return output;
    }

    public void setOutput(Specimen output) {
        this.output = output;
    }

    /**
     * @return the <em>actual</em> amount of substance added to the
     *         {@link #output} {@link Specimen} (as opposed to the
     *         <em>expected</em> amount), or null if unknown.
     */
    @Valid
    @Embedded
    public Amount getAmountAdded() {
        return amountAdded;
    }

    public void setAmountAdded(Amount amountAdded) {
        this.amountAdded = amountAdded;
    }

    /**
     * @return when this process was done, or null if unknown or null if the
     *         time is not different from the (optionally) associated
     *         {@link #getProcessingEvent()} and its
     *         {@link ProcessingEvent#getTimeDone()}.
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
     * @return the {@link SpecimenProcessingStep} that this
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "SPECIMEN_PROCESSING_STEP_ID")
    public SpecimenProcessingStep getSpecimenProcessingStep() {
        return specimenProcessingStep;
    }

    public void setSpecimenProcessingStep(
        SpecimenProcessingStep specimenProcessingStep) {
        this.specimenProcessingStep = specimenProcessingStep;
    }

    /**
     * @return a {@link ProcessingEvent} this {@link SpecimenProcessing} was
     *         done in and recorded in, or null if none.
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
