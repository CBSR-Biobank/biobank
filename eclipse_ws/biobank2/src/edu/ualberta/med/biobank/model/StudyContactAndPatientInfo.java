package edu.ualberta.med.biobank.model;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;

public class StudyContactAndPatientInfo {

    public String clinicName;

    public long patients;

    public long patientVisits;

    public ContactWrapper contact;

    public void performDoubleClick() {
        Assert.isNotNull(contact, "contact is null");
        ClinicWrapper clinic = contact.getClinic();
        Assert.isNotNull(clinic, "clinic is null");
        SessionManager.getInstance().openViewForm(clinic);
    }

}
