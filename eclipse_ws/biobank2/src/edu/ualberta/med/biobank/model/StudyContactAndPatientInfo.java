package edu.ualberta.med.biobank.model;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;

public class StudyContactAndPatientInfo {

    public String clinicName;

    public int patients;

    public Long patientVisits;

    public ContactWrapper contact;

    public void performDoubleClick() {
        Assert.isNotNull(contact, "contact is null");
        ClinicWrapper clinic = contact.getClinicWrapper();
        Assert.isNotNull(clinic, "clinic is null");
        SessionManager.getInstance().openViewForm(Clinic.class, clinic.getId());
    }

}
