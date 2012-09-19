package edu.ualberta.med.biobank.model;

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

    private Attachment input;
    private User executedBy;
    private BatchInputType inputType;
    private BatchAction action;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "INPUT_ATTACHMENT_ID")
    public Attachment getInput() {
        return input;
    }

    public void setInput(Attachment input) {
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

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperation.inputType.NotNull}")
    @Type(type = "edu.ualberta.med.biobank.model.BatchOperation$BatchInputType",
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.BatchOperation$BatchInputType")
        })
    public BatchInputType getInputType() {
        return inputType;
    }

    public void setInputType(BatchInputType inputType) {
        this.inputType = inputType;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.BatchOperation.action.NotNull}")
    @Type(type = "edu.ualberta.med.biobank.model.BatchOperation$BatchAction",
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.BatchOperation$BatchAction")
        })
    public BatchAction getAction() {
        return action;
    }

    public void setAction(BatchAction action) {
        this.action = action;
    }

    public enum BatchInputType {
        SPECIMEN("SP");

        private String id;

        private BatchInputType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static BatchInputType fromId(Integer id) {
            for (BatchInputType item : values()) {
                if (item.id.equals(id)) return item;
            }
            return null;
        }
    }

    public enum BatchAction {
        INSERT("INS"),
        UPDATE("UPD"),
        DELETE("DEL");

        private String id;

        private BatchAction(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static BatchAction fromId(Integer id) {
            for (BatchAction item : values()) {
                if (item.id.equals(id)) return item;
            }
            return null;
        }
    }
}
