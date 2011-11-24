package edu.ualberta.med.biobank.common.action.patient;

import edu.ualberta.med.biobank.common.action.ActionResult;

public class PatientNextVisitNumberResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final Integer nextVisitNumber;

    public PatientNextVisitNumberResult(Integer nextVisitNumber) {
        this.nextVisitNumber = nextVisitNumber;
    }

    public Integer getNextVisitNumber() {
        return nextVisitNumber;
    }
}
