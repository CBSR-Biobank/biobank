package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;

public class ContactHelper extends DbHelper {

    protected static ContactWrapper newContact(ClinicWrapper clinic, String name) {
        ContactWrapper contact = new ContactWrapper(appService);
        contact.setClinicWrapper(clinic);
        contact.setName(name + r.nextInt());
        contact.setEmailAddress("toto@gmail.com");
        return contact;
    }

    protected static ContactWrapper addContact(ClinicWrapper clinic, String name)
        throws Exception {
        ContactWrapper contact = newContact(clinic, name);
        contact.persist();
        return contact;
    }
}
