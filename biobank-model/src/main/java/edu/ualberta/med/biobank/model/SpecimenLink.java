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

import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * A record of the actual {@link Specimen}s and amounts involved in a
 * {@link SpecimenLinkType}.
 * <p>
 * This entity provides more detailed information about the parentage of a
 * {@link Specimen}, i.e. the {@link #input}-{@link #output} pair could be
 * considered a parent-child relationship. This is opposed to
 * {@link CollectionEvent}s, which provide much more general heritage
 * information. So, special care must be taken to ensure that
 * {@link StudySpecimen} and {@link SpecimenLink} entities are consistent. The
 * {@link #output} must be in all the same {@link CollectionEvent}s as the
 * {@link #input}, but if two {@link Specimen}s are in the same
 * {@link CollectionEvent} they do <em>not</em> need to be associated (directly
 * or transitively) through a {@link SpecimenLink}. Also note that the
 * {@link #input} does <em>not</em> need to be in the same
 * {@link CollectionEvent}(s) as the {@link #output}.
 * 
 * @author Jonathan Ferland
 * @see SpecimenCollectionEvent
 */
@Audited
@Entity
@Table(name = "SPECIMEN_LINK")
@Unique(properties = { "input", "output", "timeDone" }, groups = PrePersist.class)
public class SpecimenLink
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Specimen input;
    private Specimen output;
    private Date timeDone;
    private SpecimenLinkType type;
    private ProcessingEvent processingEvent;
    private Decimal actualInputAmountChange;
    private Decimal actualOutputAmountChange;

    /**
     * @return the {@link Specimen} that was processed.
     */
    @NaturalId
    @NotNull(message = "{SpecimenLink.input.NotNull}")
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
    @NaturalId
    @NotNull(message = "{SpecimenLink.output.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTPUT_SPECIMEN_ID", nullable = false)
    public Specimen getOutput() {
        return output;
    }

    public void setOutput(Specimen output) {
        this.output = output;
    }

    /**
     * @return when the processing happened.
     */
    @NaturalId(mutable = false)
    @NotNull(message = "{SpecimenLink.timeDone.NotNull}")
    @Past
    @Column(name = "TIME_DONE", nullable = false)
    public Date getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(Date timeDone) {
        this.timeDone = timeDone;
    }

    /**
     * @return the type of processing that the involved {@link Specimen}s
     *         underwent, or null if unspecified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_PROCESSING_LINK_TYPE_ID")
    public SpecimenLinkType getType() {
        return type;
    }

    public void setType(SpecimenLinkType type) {
        this.type = type;
    }

    /**
     * @return the {@link ProcessingEvent} this {@link SpecimenLink} was
     *         established in, or null if unspecified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID")
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    /**
     * @return the actual amount removed from the {@link #input}.
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "ACTUAL_INPUT_CHANGE_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "ACTUAL_INPUT_CHANGE_SCALE"))
    })
    public Decimal getActualInputAmountChange() {
        return actualInputAmountChange;
    }

    public void setActualInputAmountChange(Decimal actualInputAmountChange) {
        this.actualInputAmountChange = actualInputAmountChange;
    }

    /**
     * @return the actual amount added to the {@link #output}.
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "ACTUAL_OUTPUT_CHANGE_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "ACTUAL_OUTPUT_CHANGE_SCALE"))
    })
    public Decimal getActualOutputAmountChange() {
        return actualOutputAmountChange;
    }

    public void setActualOutputAmountChange(Decimal actualOutputAmountChange) {
        this.actualOutputAmountChange = actualOutputAmountChange;
    }
}
