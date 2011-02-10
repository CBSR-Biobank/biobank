package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.model.Contact;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContactWrapper extends ModelWrapper<Contact> {

    public ContactWrapper(WritableApplicationService appService,
        Contact wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContactWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getName() {
        return getProperty(ContactPeer.NAME);
    }

    public void setName(String name) {
        setProperty(ContactPeer.NAME, name);
    }

    public String getTitle() {
        return getProperty(ContactPeer.TITLE);
    }

    public void setTitle(String title) {
        setProperty(ContactPeer.TITLE, title);
    }

    public String getMobileNumber() {
        return getProperty(ContactPeer.MOBILE_NUMBER);
    }

    public void setMobileNumber(String mobileNumber) {
        setProperty(ContactPeer.MOBILE_NUMBER, mobileNumber);
    }

    public String getPagerNumber() {
        return getProperty(ContactPeer.PAGER_NUMBER);
    }

    public void setPagerNumber(String pagerNumber) {
        setProperty(ContactPeer.PAGER_NUMBER, pagerNumber);
    }

    public String getOfficeNumber() {
        return getProperty(ContactPeer.OFFICE_NUMBER);
    }

    public void setOfficeNumber(String officeNumber) {
        setProperty(ContactPeer.OFFICE_NUMBER, officeNumber);
    }

    public String getFaxNumber() {
        return getProperty(ContactPeer.FAX_NUMBER);
    }

    public void setFaxNumber(String faxNumber) {
        setProperty(ContactPeer.FAX_NUMBER, faxNumber);
    }

    public String getEmailAddress() {
        return getProperty(ContactPeer.EMAIL_ADDRESS);
    }

    public void setEmailAddress(String emailAddress) {
        setProperty(ContactPeer.EMAIL_ADDRESS, emailAddress);
    }

    public ClinicWrapper getClinic() {
        return getWrappedProperty(ContactPeer.CLINIC, ClinicWrapper.class);
    }

    public void setClinic(ClinicWrapper clinic) {
        setWrappedProperty(ContactPeer.CLINIC, clinic);
    }

    /**
     * Get the studyCollection. Use Study.setContactCollection to link study and
     * contact
     */
    public List<StudyWrapper> getStudyCollection(boolean sort) {
        return getWrapperCollection(ContactPeer.STUDY_COLLECTION,
            StudyWrapper.class, sort);
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(false);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (!deleteAllowed()) {
            throw new BiobankCheckException("Unable to delete contact "
                + getName() + ". No more study reference should exist.");
        }
    }

    public boolean deleteAllowed() {
        List<StudyWrapper> studies = getStudyCollection();
        return ((studies == null) || (studies.size() == 0));
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ContactPeer.PROP_NAMES;
    }

    @Override
    public Class<Contact> getWrappedClass() {
        return Contact.class;
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
