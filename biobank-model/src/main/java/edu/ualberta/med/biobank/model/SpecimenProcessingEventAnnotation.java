package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;
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

@Audited
@Entity
@DiscriminatorValue("SPPE")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "SPECIMEN_PROCESSING_EVENT_ID",
        "ANNOTATION_TYPE_ID" })
})
@Unique(properties = { "specimenProcessingEvent", "type" }, groups = PrePersist.class)
public class SpecimenProcessingEventAnnotation
    extends AbstractAnnotation {
    private static final long serialVersionUID = 1L;

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
