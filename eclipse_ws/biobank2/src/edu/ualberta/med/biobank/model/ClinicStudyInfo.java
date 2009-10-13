package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ClinicStudyInfo {

    public StudyWrapper studyWrapper;

    public String studyShortName;

    public long patients;

    public long patientVisits;

    public void performDoubleClick() {
        SessionManager.getInstance().openViewForm(Study.class,
            studyWrapper.getId());
    }

}
