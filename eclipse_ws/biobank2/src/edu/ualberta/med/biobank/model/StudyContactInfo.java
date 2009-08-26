package edu.ualberta.med.biobank.model;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;

public class StudyContactInfo {

    public String clinicName;
    public Contact contact;

    public void performDoubleClick() {
        Assert.isNotNull(contact, "contact is null");
        Clinic clinic = contact.getClinic();
        Assert.isNotNull(clinic, "clinic is null");
        SessionManager.getInstance().openViewForm(clinic);
    }

}
