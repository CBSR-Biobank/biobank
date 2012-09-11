package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Associates a {@link Specimen} with a {@link ProcessingEvent} and is an anchor
 * for {@link SpecimenProcessingEventAnnotation}s.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_EVENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "SPECIMEN_ID", "PROCESSING_EVENT_ID" })
    })
@Unique(properties = { "specimen", "processingEvent" }, groups = PrePersist.class)
public class SpecimenProcessingEvent
    extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private ProcessingEvent processingEvent;

    @NotNull(message = "{SpecimenProcessingEvent.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NotNull(message = "{SpecimenProcessingEvent.processingEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID", nullable = false)
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }
}
