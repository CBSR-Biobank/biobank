package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_EVENT_ANNOTATION")
public class SpecimenProcessingEventAnnotation
    extends AbstractAnnotation<SpecimenProcessingEventAnnotationType> {
    private static final long serialVersionUID = 1L;

    // TODO: split into separate Specimen and ProcessingEvent fields
    private SpecimenProcessingEvent specimenProcessingEvent;

    @NotNull(message = "{SpecimenProcessingEventAnnotation.specimenProcessingEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_PROCESSING_EVENT_ID")
    public SpecimenProcessingEvent getSpecimenProcessingEvent() {
        return specimenProcessingEvent;
    }

    public void setSpecimenProcessingEvent(
        SpecimenProcessingEvent specimenProcessingEvent) {
        this.specimenProcessingEvent = specimenProcessingEvent;
    }

}
