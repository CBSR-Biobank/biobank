package edu.ualberta.med.biobank.model;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;

public class StudyContactAndPatientInfo {

    public String clinicName;

    public int patients;

    public Long patientVisits;

    public Contact contact;

    public void performDoubleClick() {
        Assert.isNotNull(contact, "contact is null");
        Clinic clinic = contact.getClinic();
        Assert.isNotNull(clinic, "clinic is null");
        SessionManager.getInstance().openViewForm(clinic);
    }

}
