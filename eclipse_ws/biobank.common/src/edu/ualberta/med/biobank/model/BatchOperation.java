package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "BATCH_OPERATION")
public class BatchOperation
    extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Attachment input;
    private User user;
    private BatchInputType inputType;
    private BatchAction action;

    public enum BatchInputType {
        SPECIMEN("SP");

        private String id;

        private BatchInputType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
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
    }
}
