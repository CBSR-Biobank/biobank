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

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Amount;

/**
 * A record of the actual {@link Specimen} and amount involved in a
 * {@link SpecimenProcessingType}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING")
public class SpecimenProcessing
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private SpecimenProcessingType type;
    private Date timeDone;
    private Amount actualAmountChange;
    private ProcessingEvent processingEvent;

    /**
     * @return the {@link Specimen} that was processed.
     */
    @NotNull(message = "{SpecimenProcessing.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    /**
     * @return the {@link SpecimenProcessingType} that this {@link Specimen}
     *         underwent, or null if unspecified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_TYPE_ID")
    public SpecimenProcessingType getType() {
        return type;
    }

    public void setType(SpecimenProcessingType type) {
        this.type = type;
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
     * @return he actual amount added to the {@link #specimen}.
     */
    @Valid
    @Embedded
    public Amount getActualAmountChange() {
        return actualAmountChange;
    }

    public void setActualAmountChange(Amount actualAmountChange) {
        this.actualAmountChange = actualAmountChange;
    }

    /**
     * @return a {@link ProcessingEvent} this {@link SpecimenProcessing} was
     *         done in and recorded in, or null if none, or null if unspecified.
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
