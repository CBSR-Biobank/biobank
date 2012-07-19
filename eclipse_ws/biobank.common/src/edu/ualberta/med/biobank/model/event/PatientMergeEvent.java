package edu.ualberta.med.biobank.model.event;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import edu.ualberta.med.biobank.model.Patient;

public class PatientMergeEvent extends Event {
    public PatientMergeEvent() {
    }

    public PatientMergeEvent(Patient srcPatient, Patient dstPatient) {
        srcPatient.getStudy();
    }

    @OneToOne
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn),
        @JoinColumnOrFormula(formula = @JoinFormula(value = ""))
    })
    @JoinFormula(value = "1", referencedColumnName = "list_index")
    public EventObject getSourcePatient() {
        return null;
    }

    public void setSourcePatient(EventObject eventObject) {

    }
}
