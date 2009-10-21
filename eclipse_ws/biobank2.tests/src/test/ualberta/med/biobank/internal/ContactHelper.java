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

    public static int addContacts(StudyWrapper study, String name, int min)
        throws Exception {
        SiteWrapper site = study.getSite();
        ClinicHelper.addClinics(site, name, r.nextInt(15) + min + 1, true);
        List<ClinicWrapper> clinics = site.getClinicCollection();
        int nber = r.nextInt(clinics.size() - 1) + min;
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

    public static int addContacts(StudyWrapper study, String name)
        throws Exception {
        return addContacts(study, name, 1);
    }

}
