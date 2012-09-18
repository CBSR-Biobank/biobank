package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Immutable;

import edu.ualberta.med.biobank.model.util.ProxyUtil;

@Immutable
@Entity
@Table(name = "BATCH_OPERATION_PROCESSING_EVENT")
public class BatchOperationProcessingEvent
    implements Serializable {
    private static final long serialVersionUID = 1L;

    private BatchOperation batch;
    private ProcessingEvent processingEvent;

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperationProcessingEvent.batch.NotNull}")
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BATCH_OPERATION_ID", nullable = false)
    public BatchOperation getBatch() {
        return batch;
    }

    public void setBatch(BatchOperation batch) {
        this.batch = batch;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperationProcessingEvent.processingEvent.NotNull}")
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "none")
    @JoinColumn(name = "PROCESSING_EVENT_ID", nullable = false)
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 41)
            .append(batch)
            .append(processingEvent)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!ProxyUtil.isClassEqual(this, obj)) return false;
        BatchOperationProcessingEvent rhs = (BatchOperationProcessingEvent) obj;
        return new EqualsBuilder()
            .append(batch, rhs.batch)
            .append(processingEvent, rhs.processingEvent)
            .isEquals();
    }
}
