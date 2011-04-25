package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ContactHelper extends DbHelper {

    public static ContactWrapper newContact(ClinicWrapper clinic, String name) {
        ContactWrapper contact = new ContactWrapper(appService);
        contact.setClinic(clinic);
        contact.setName(name + r.nextInt());
        contact.setEmailAddress("toto@gmail.com");
        return contact;
    }

    public static ContactWrapper addContact(ClinicWrapper clinic, String name)
        throws Exception {
        ContactWrapper contact = newContact(clinic, name);
        contact.persist();
        return contact;
    }

    public static List<ContactWrapper> addRandContactsToStudy(
        StudyWrapper study, String name) throws Exception {
        ClinicHelper.addClinics(name, r.nextInt(15) + 3, true);
        List<ClinicWrapper> clinics = ClinicWrapper.getAllClinics(appService);
        int nber = r.nextInt(clinics.size() - 2) + 1;
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        for (int i = 0; i < nber; i++) {
            ClinicWrapper clinic = clinics.get(i);
            ContactWrapper contact = chooseRandomlyInList(clinic
                .getContactCollection(false));
            contacts.add(contact);
        }
        study.addToContactCollection(contacts);
        study.persist();
        return contacts;
    }

    public static int addContactsToStudy(StudyWrapper study, String name)
        throws Exception {
        return addRandContactsToStudy(study, name).size();
    }

    public static int addContactsToClinic(ClinicWrapper clinic, String name,
        int min, int max) throws Exception {
        int nber = r.nextInt(max - min + 1) + min;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        return nber;
    }

    public static int addContactsToClinic(ClinicWrapper clinic, String name)
        throws Exception {
        return addContactsToClinic(clinic, name, 1, 5);
    }

}
