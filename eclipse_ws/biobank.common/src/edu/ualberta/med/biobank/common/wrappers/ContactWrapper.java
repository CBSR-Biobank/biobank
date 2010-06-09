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

    private ClinicWrapper clinic;

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

    public String getMobileNumber() {
        return wrappedObject.getMobileNumber();
    }

    public void setMobileNumber(String mobileNumber) {
        String oldMobileNumber = getMobileNumber();
        wrappedObject.setMobileNumber(mobileNumber);
        propertyChangeSupport.firePropertyChange("mobileNumber",
            oldMobileNumber, mobileNumber);
    }

    public String getPagerNumber() {
        return wrappedObject.getPagerNumber();
    }

    public void setPagerNumber(String pagerNumber) {
        String oldPagerNumber = getPagerNumber();
        wrappedObject.setPagerNumber(pagerNumber);
        propertyChangeSupport.firePropertyChange("pagerNumber", oldPagerNumber,
            pagerNumber);
    }

    public String getOfficeNumber() {
        return wrappedObject.getOfficeNumber();
    }

    public void setOfficeNumber(String officeNumber) {
        String oldOfficeNumber = getOfficeNumber();
        wrappedObject.setOfficeNumber(officeNumber);
        propertyChangeSupport.firePropertyChange("officeNumber",
            oldOfficeNumber, officeNumber);
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
        String oldEmailAddress = getEmailAddress();
        wrappedObject.setEmailAddress(emailAddress);
        propertyChangeSupport.firePropertyChange("emailAddress",
            oldEmailAddress, emailAddress);
    }

    public ClinicWrapper getClinic() {
        if (clinic == null) {
            Clinic c = wrappedObject.getClinic();
            if (c == null)
                return null;
            clinic = new ClinicWrapper(appService, c);
        }
        return clinic;
    }

    public void setClinic(ClinicWrapper clinic) {
        this.clinic = clinic;
        Clinic oldClinic = wrappedObject.getClinic();
        Clinic newClinic = clinic.getWrappedObject();
        wrappedObject.setClinic(newClinic);
        propertyChangeSupport
            .firePropertyChange("clinic", oldClinic, newClinic);
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
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "title", "mobileNumber", "pagerNumber",
            "officeNumber", "faxNumber", "emailAddress", "clinic",
            "studyCollection" };
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

    @Override
    public void reload() throws Exception {
        super.reload();
        clinic = null;
    }

}
