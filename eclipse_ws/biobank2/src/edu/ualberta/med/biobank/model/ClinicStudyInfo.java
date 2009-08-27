package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.SessionManager;

public class ClinicStudyInfo {

    public Study study;

    public String studyShortName;

    public int patients;

    public Long patientVisits;

    public void performDoubleClick() {
        SessionManager.getInstance().openViewForm(Study.class, study.getId());
    }

}
