package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.util.HashCodeBuilderProvider;
import edu.ualberta.med.biobank.model.util.ProxyUtil;
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
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "SPECIMEN_ID", "PROCESSING_EVENT_ID" })
    })
@Unique(properties = { "specimen", "processingEvent" }, groups = PrePersist.class)
public class ProcessingEventInputSpecimen
    extends AbstractModel {
    private static final long serialVersionUID = 1L;
    private static final HashCodeBuilderProvider hashCodeBuilderProvider =
        new HashCodeBuilderProvider(ProcessingEventInputSpecimen.class, 23, 29);

    private Specimen specimen;
    private ProcessingEvent processingEvent;

    @NaturalId
    @NotNull(message = "{ProcessingEventInputSpecimen.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NaturalId
    @NotNull(message = "{ProcessingEventInputSpecimen.processingEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID", nullable = false)
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    @Override
    public int hashCode() {
        return hashCodeBuilderProvider.get()
            .append(getSpecimen())
            .append(getProcessingEvent())
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!ProxyUtil.sameClass(this, obj)) return false;
        ProcessingEventInputSpecimen rhs = (ProcessingEventInputSpecimen) obj;
        return new EqualsBuilder()
            .append(getSpecimen(), rhs.getSpecimen())
            .append(getProcessingEvent(), rhs.getProcessingEvent())
            .isEquals();
    }
}
