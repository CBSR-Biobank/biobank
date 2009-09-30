package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContactWrapper extends ModelWrapper<Contact> {

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
        wrappedObject.setName(title);
        propertyChangeSupport.firePropertyChange("title", oldTitle, title);
    }

    public String getPhoneNumber() {
        return wrappedObject.getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        String oldPhoneNumber = getPhoneNumber();
        wrappedObject.setName(phoneNumber);
        propertyChangeSupport.firePropertyChange("phoneNumber", oldPhoneNumber,
            phoneNumber);
    }

    public String getFaxNumber() {
        return wrappedObject.getFaxNumber();
    }

    public void setFaxNumber(String faxNumber) {
        String oldFaxNumber = getFaxNumber();
        wrappedObject.setName(faxNumber);
        propertyChangeSupport.firePropertyChange("faxNumber", oldFaxNumber,
            faxNumber);
    }

    public String getEmailAddress() {
        return wrappedObject.getEmailAddress();
    }

    public void setEmailAddress(String emailAddress) {
        String oldEmailAddress = getPhoneNumber();
        wrappedObject.setName(emailAddress);
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
    protected void firePropertyChanges(Contact oldWrappedObject,
        Contact newWrappedObject) {
        String[] members = new String[] { "name", "title", "phoneNumber",
            "faxNumber", "emailAddress", "clinic" };

        try {
            firePropertyChanges(members, oldWrappedObject, newWrappedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<Contact> getWrappedClass() {
        return Contact.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // no checks required for contacts
    }

}
