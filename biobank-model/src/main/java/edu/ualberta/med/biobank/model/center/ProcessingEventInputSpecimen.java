package edu.ualberta.med.biobank.model.center;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.LongIdModel;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Indicates {@link Specimen}s that should be used as input in a
 * {@link ProcessingEvent}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "PROCESSING_EVENT_INPUT_SPECIMEN",
    uniqueConstraints = @UniqueConstraint(columnNames = {
        "SPECIMEN_ID",
        "PROCESSING_EVENT_ID"
    }))
@Unique(properties = { "specimen", "processingEvent" }, groups = PrePersist.class)
public class ProcessingEventInputSpecimen
    extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private ProcessingEvent processingEvent;

    @NotNull(message = "{ProcessingEventInputSpecimen.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NotNull(message = "{ProcessingEventInputSpecimen.processingEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID", nullable = false)
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }
}
