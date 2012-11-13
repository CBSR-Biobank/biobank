package edu.ualberta.med.biobank.model.center;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Comment;

@Entity
@Table(name = "PROCESSING_EVENT_COMMENT")
public class ProcessingEventComment
    extends Comment<ProcessingEvent> {
    private static final long serialVersionUID = 1L;

    private ProcessingEvent processingEvent;

    @NotNull(message = "{ProcessingEventComment.processingEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID", nullable = false)
    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    @Override
    @Transient
    public ProcessingEvent getOwner() {
        return getProcessingEvent();
    }
}
