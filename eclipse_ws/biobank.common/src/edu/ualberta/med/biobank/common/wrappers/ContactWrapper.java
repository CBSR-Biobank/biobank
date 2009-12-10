package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContactWrapper extends ModelWrapper<Contact> {

    public ContactWrapper(WritableApplicationService appService,
        Contact wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContactWrapper(WritableApplicationService appService) {
        super(appService);
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

    public ClinicWrapper getClinic() {
        return new ClinicWrapper(appService, wrappedObject.getClinic());
    }

    public void setClinic(Clinic clinic) {
        Clinic oldClinic = wrappedObject.getClinic();
        wrappedObject.setClinic(clinic);
        propertyChangeSupport.firePropertyChange("clinic", oldClinic, clinic);

    }

    public void setClinic(ClinicWrapper clinic) {
        setClinic(clinic.getWrappedObject());
    }

    /**
     * Get the studyCollection. Use Study.setContactCollection to link study and
     * contact
     */
    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getStudyCollection(boolean sort) {
        List<StudyWrapper> clinicCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");
        if (clinicCollection == null) {
            Collection<Study> children = wrappedObject.getStudyCollection();
            if (children != null) {
                clinicCollection = new ArrayList<StudyWrapper>();
                for (Study study : children) {
                    clinicCollection.add(new StudyWrapper(appService, study));
                }
                propertiesMap.put("studyCollection", clinicCollection);
            }
        }
        if ((clinicCollection != null) && sort)
            Collections.sort(clinicCollection);
        return clinicCollection;
    }

    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(false);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (getStudyCollection() != null && getStudyCollection().size() > 0) {
            throw new BiobankCheckException("Unable to delete contact "
                + getName() + ". No more study reference should exist.");
        }
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "title", "phoneNumber", "faxNumber",
            "emailAddress", "clinic", "studyCollection" };
    }

    @Override
    public Class<Contact> getWrappedClass() {
        return Contact.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    public int compareTo(ModelWrapper<Contact> wrapper) {
        if (wrapper instanceof ContactWrapper) {
            String myName = wrappedObject.getName();
            String wrapperName = wrapper.wrappedObject.getName();
            return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
                .equals(wrapperName) ? 0 : -1));
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName() + " (" + getPhoneNumber() + ")";
    }
}
