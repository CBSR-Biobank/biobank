package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.SessionManager;

public class StudyClinicInfo {

    public Clinic clinic;

    public String clinicName;

    public int patients;

    public Long patientVisits;

    public void performDoubleClick() {
        SessionManager.getInstance().openViewForm(clinic);
    }

}
