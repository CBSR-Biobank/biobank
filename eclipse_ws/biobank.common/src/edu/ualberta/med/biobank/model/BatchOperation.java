package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Immutable
@Entity
@Table(name = "BATCH_OPERATION")
public class BatchOperation
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private FileData input;
    private User executedBy;
    private Date timeExecuted;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FILE_DATA_ID")
    public FileData getInput() {
        return input;
    }

    public void setInput(FileData input) {
        this.input = input;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperation.executedBy.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXECUTED_BY_USER_ID", nullable = false)
    public User getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(User executedBy) {
        this.executedBy = executedBy;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperation.timeExecuted.NotNull}")
    @Column(name = "TIME_EXECUTED", nullable = false)
    public Date getTimeExecuted() {
        return timeExecuted;
    }

    public void setTimeExecuted(Date timeExecuted) {
        this.timeExecuted = timeExecuted;
    }
}
