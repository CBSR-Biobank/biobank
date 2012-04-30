/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ClinicBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudyBaseWrapper;
import java.util.Arrays;

public class ContactBaseWrapper extends ModelWrapper<Contact> {

    public ContactBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContactBaseWrapper(WritableApplicationService appService,
        Contact wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Contact> getWrappedClass() {
        return Contact.class;
    }

    @Override
    public Property<Integer, ? super Contact> getIdProperty() {
        return ContactPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Contact>> getProperties() {
        return ContactPeer.PROPERTIES;
    }

    public String getTitle() {
        return getProperty(ContactPeer.TITLE);
    }

    public void setTitle(String title) {
        String trimmed = title == null ? null : title.trim();
        setProperty(ContactPeer.TITLE, trimmed);
    }

    public String getFaxNumber() {
        return getProperty(ContactPeer.FAX_NUMBER);
    }

    public void setFaxNumber(String faxNumber) {
        String trimmed = faxNumber == null ? null : faxNumber.trim();
        setProperty(ContactPeer.FAX_NUMBER, trimmed);
    }

    public String getName() {
        return getProperty(ContactPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(ContactPeer.NAME, trimmed);
    }

    public String getOfficeNumber() {
        return getProperty(ContactPeer.OFFICE_NUMBER);
    }

    public void setOfficeNumber(String officeNumber) {
        String trimmed = officeNumber == null ? null : officeNumber.trim();
        setProperty(ContactPeer.OFFICE_NUMBER, trimmed);
    }

    public String getPagerNumber() {
        return getProperty(ContactPeer.PAGER_NUMBER);
    }

    public void setPagerNumber(String pagerNumber) {
        String trimmed = pagerNumber == null ? null : pagerNumber.trim();
        setProperty(ContactPeer.PAGER_NUMBER, trimmed);
    }

    public String getEmailAddress() {
        return getProperty(ContactPeer.EMAIL_ADDRESS);
    }

    public void setEmailAddress(String emailAddress) {
        String trimmed = emailAddress == null ? null : emailAddress.trim();
        setProperty(ContactPeer.EMAIL_ADDRESS, trimmed);
    }

    public String getMobileNumber() {
        return getProperty(ContactPeer.MOBILE_NUMBER);
    }

    public void setMobileNumber(String mobileNumber) {
        String trimmed = mobileNumber == null ? null : mobileNumber.trim();
        setProperty(ContactPeer.MOBILE_NUMBER, trimmed);
    }

    public ClinicWrapper getClinic() {
        boolean notCached = !isPropertyCached(ContactPeer.CLINIC);
        ClinicWrapper clinic = getWrappedProperty(ContactPeer.CLINIC, ClinicWrapper.class);
        if (clinic != null && notCached) ((ClinicBaseWrapper) clinic).addToContactCollectionInternal(Arrays.asList(this));
        return clinic;
    }

    public void setClinic(ClinicBaseWrapper clinic) {
        if (isInitialized(ContactPeer.CLINIC)) {
            ClinicBaseWrapper oldClinic = getClinic();
            if (oldClinic != null) oldClinic.removeFromContactCollectionInternal(Arrays.asList(this));
        }
        if (clinic != null) clinic.addToContactCollectionInternal(Arrays.asList(this));
        setWrappedProperty(ContactPeer.CLINIC, clinic);
    }

    void setClinicInternal(ClinicBaseWrapper clinic) {
        setWrappedProperty(ContactPeer.CLINIC, clinic);
    }

    public List<StudyWrapper> getStudyCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ContactPeer.STUDIES);
        List<StudyWrapper> studyCollection = getWrapperCollection(ContactPeer.STUDIES, StudyWrapper.class, sort);
        if (notCached) {
            for (StudyBaseWrapper e : studyCollection) {
                e.addToContactCollectionInternal(Arrays.asList(this));
            }
        }
        return studyCollection;
    }

    public void addToStudyCollection(List<? extends StudyBaseWrapper> studyCollection) {
        addToWrapperCollection(ContactPeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.addToContactCollectionInternal(Arrays.asList(this));
        }
    }

    void addToStudyCollectionInternal(List<? extends StudyBaseWrapper> studyCollection) {
        if (isInitialized(ContactPeer.STUDIES)) {
            addToWrapperCollection(ContactPeer.STUDIES, studyCollection);
        } else {
            getElementQueue().add(ContactPeer.STUDIES, studyCollection);
        }
    }

    public void removeFromStudyCollection(List<? extends StudyBaseWrapper> studyCollection) {
        removeFromWrapperCollection(ContactPeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.removeFromContactCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromStudyCollectionInternal(List<? extends StudyBaseWrapper> studyCollection) {
        if (isPropertyCached(ContactPeer.STUDIES)) {
            removeFromWrapperCollection(ContactPeer.STUDIES, studyCollection);
        } else {
            getElementQueue().remove(ContactPeer.STUDIES, studyCollection);
        }
    }

    public void removeFromStudyCollectionWithCheck(List<? extends StudyBaseWrapper> studyCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContactPeer.STUDIES, studyCollection);
        for (StudyBaseWrapper e : studyCollection) {
            e.removeFromContactCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromStudyCollectionWithCheckInternal(List<? extends StudyBaseWrapper> studyCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ContactPeer.STUDIES, studyCollection);
    }

}
