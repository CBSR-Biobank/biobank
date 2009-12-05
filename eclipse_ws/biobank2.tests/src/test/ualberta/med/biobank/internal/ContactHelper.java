package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ContactHelper extends DbHelper {

    public static ContactWrapper newContact(ClinicWrapper clinic, String name) {
        ContactWrapper contact = new ContactWrapper(appService);
        contact.setClinicWrapper(clinic);
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

    public static int addContactsToStudy(StudyWrapper study, String name)
        throws Exception {
        SiteWrapper site = study.getSite();
        ClinicHelper.addClinics(site, name, r.nextInt(15) + 3, true);
        List<ClinicWrapper> clinics = site.getClinicCollection();
        int nber = r.nextInt(clinics.size() - 2) + 1;
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        for (int i = 0; i < nber; i++) {
            ClinicWrapper clinic = clinics.get(i);
            ContactWrapper contact = chooseRandomlyInList(clinic
                .getContactCollection());
            contacts.add(contact);
        }
        study.setContactCollection(contacts);
        study.persist();
        return nber;
    }

    public static int addContactsToClinic(ClinicWrapper clinic, String name)
        throws Exception {
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        return nber;
    }
}
