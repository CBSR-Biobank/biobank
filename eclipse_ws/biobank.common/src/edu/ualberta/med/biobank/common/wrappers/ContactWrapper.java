package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ContactBaseWrapper;
import edu.ualberta.med.biobank.model.Contact;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Get the studyCollection. Use Study.setContactCollection to link study and
 * contact
 */
public class ContactWrapper extends ContactBaseWrapper {
    private static final String HAS_STUDIES_MSG = "Unable to delete contact {0}. No more study reference should exist.";

    public ContactWrapper(WritableApplicationService appService,
        Contact wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContactWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(false);
    }

    public boolean deleteAllowed() {
        List<StudyWrapper> studies = getStudyCollection(false);
        return ((studies == null) || (studies.size() == 0));
    }

    @Override
    public int compareTo(ModelWrapper<Contact> c2) {
        if (c2 instanceof ContactWrapper) {
            String myName = wrappedObject.getName();
            String c2Name = c2.wrappedObject.getName();
            if (myName != null && c2Name != null) {
                return myName.compareTo(c2Name);
            }
        }
        return 0;
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {
        String hasStudiesMsg = MessageFormat.format(HAS_STUDIES_MSG, getName());

        tasks.add(check().empty(ContactPeer.STUDY_COLLECTION, hasStudiesMsg));

        super.addDeleteTasks(tasks);
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }

    @Override
    public String toString() {
        return getName() + " (" + getMobileNumber() + ")";
    }

    private static final String ALL_CONTACTS_QRY = "from "
        + Contact.class.getName();

    public static List<ContactWrapper> getAllContacts(
        WritableApplicationService appService) throws ApplicationException {
        List<ContactWrapper> wrappers = new ArrayList<ContactWrapper>();
        HQLCriteria c = new HQLCriteria(ALL_CONTACTS_QRY);
        List<Contact> contacts = appService.query(c);
        for (Contact contact : contacts) {
            wrappers.add(new ContactWrapper(appService, contact));
        }
        return wrappers;
    }
}
