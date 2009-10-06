package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContactWrapper extends ModelWrapper<Contact> implements
    Comparable<ContactWrapper> {

    public ContactWrapper(WritableApplicationService appService,
        Contact wrappedObject) {
        super(appService, wrappedObject);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getTitle() {
        return wrappedObject.getTitle();
    }

    public void setTitle(String title) {
        String oldTitle = getTitle();
        wrappedObject.setTitle(title);
        propertyChangeSupport.firePropertyChange("title", oldTitle, title);
    }

    public String getPhoneNumber() {
        return wrappedObject.getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        String oldPhoneNumber = getPhoneNumber();
        wrappedObject.setPhoneNumber(phoneNumber);
        propertyChangeSupport.firePropertyChange("phoneNumber", oldPhoneNumber,
            phoneNumber);
    }

    public String getFaxNumber() {
        return wrappedObject.getFaxNumber();
    }

    public void setFaxNumber(String faxNumber) {
        String oldFaxNumber = getFaxNumber();
        wrappedObject.setFaxNumber(faxNumber);
        propertyChangeSupport.firePropertyChange("faxNumber", oldFaxNumber,
            faxNumber);
    }

    public String getEmailAddress() {
        return wrappedObject.getEmailAddress();
    }

    public void setEmailAddress(String emailAddress) {
        String oldEmailAddress = getPhoneNumber();
        wrappedObject.setEmailAddress(emailAddress);
        propertyChangeSupport.firePropertyChange("emailAddress",
            oldEmailAddress, emailAddress);
    }

    public ClinicWrapper getClinicWrapper() {
        return new ClinicWrapper(appService, wrappedObject.getClinic());
    }

    public void setClinicWrapper(ClinicWrapper clinicWrapper) {
        Clinic oldClinic = wrappedObject.getClinic();
        Clinic newClinic = clinicWrapper.getWrappedObject();
        wrappedObject.setClinic(newClinic);
        propertyChangeSupport
            .firePropertyChange("clinic", oldClinic, newClinic);
    }

    public Collection<Study> getStudyCollection() {
        return wrappedObject.getStudyCollection();
    }

    public void setStudyCollection(Collection<Study> collection) {
        wrappedObject.setStudyCollection(collection);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // no checks required for contacts
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "title", "phoneNumber", "faxNumber",
            "emailAddress", "clinic" };
    }

    @Override
    protected Class<Contact> getWrappedClass() {
        return Contact.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // no checks required for contacts
    }

    public int compareTo(ContactWrapper wrapper) {
        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();
        return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
            .equals(wrapperName) ? 0 : -1));
    }

}
