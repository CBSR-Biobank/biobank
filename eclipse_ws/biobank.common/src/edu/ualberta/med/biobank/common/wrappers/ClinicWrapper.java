package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicWrapper extends ModelWrapper<Clinic> {

    private AddressWrapper addressWrapper;

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
        addressWrapper = new AddressWrapper(appService, wrappedObject
            .getAddress());
    }

    public AddressWrapper getAddressWrapper() {
        return addressWrapper;
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(String activityStatus) {
        String oldStatus = getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus", oldStatus,
            activityStatus);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = getComment();
        wrappedObject.setName(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public Site getSite() {
        return wrappedObject.getSite();
    }

    public void setSiteWrapper(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = siteWrapper.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("patient", oldSite, newSite);
    }

    public void setStudy(Site site) {
        wrappedObject.setSite(site);
    }

    public boolean checkClinicNameUnique() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from " + Clinic.class.getName()
            + " where site = ? and name = ?", Arrays.asList(new Object[] {
            getSite(), getName() }));

        List<Clinic> results = appService.query(c);
        return (results.size() == 0);
    }

    @Override
    protected void firePropertyChanges(Clinic oldValue, Clinic wrappedObject2) {
        propertyChangeSupport.firePropertyChange("name", oldValue.getName(),
            wrappedObject.getName());

    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        if (checkClinicNameUnique()) {
            return DatabaseResult.OK;
        }
        return new DatabaseResult("A clinic with name \"" + getName()
            + "\" already exists.");
    }

    @Override
    protected Class<Clinic> getWrappedClass() {
        return Clinic.class;
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    public Collection<ContactWrapper> getContactCollection() {
        Collection<ContactWrapper> collection = new HashSet<ContactWrapper>();
        for (Contact contact : wrappedObject.getContactCollection()) {
            collection.add(new ContactWrapper(appService, contact));
        }
        return collection;
    }

    public List<Study> getStudyCollection() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct studies from "
            + Contact.class.getName() + " as contacts"
            + " inner join contacts.studyCollection as studies"
            + " where contacts.clinic = ?", Arrays
            .asList(new Object[] { wrappedObject }));

        return appService.query(c);
    }

    @Override
    protected DatabaseResult deleteChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

}
