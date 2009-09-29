package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
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

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        wrappedObject.setName(name);
    }

    public Site getSite() {
        return wrappedObject.getSite();
    }

    public void setStudy(Site site) {
        wrappedObject.setSite(site);
    }

    public AddressWrapper getAddressWrapper() {
        return new AddressWrapper(appService, wrappedObject.getAddress());
    }

    public boolean checkClinicNameUnique() throws ApplicationException {
        if (isNew()) {
            HQLCriteria c = new HQLCriteria("from " + Clinic.class.getName()
                + " where site = ? and name = ?", Arrays.asList(new Object[] {
                getSite(), getName() }));

            List<Clinic> results = appService.query(c);
            return results.size() == 0;
        }
        return true;
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

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public Collection<Contact> getContactCollection() {
        return wrappedObject.getContactCollection();
    }

    public List<Study> getStudyCollection() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct studies from "
            + Contact.class.getName() + " as contacts"
            + " inner join contacts.studyCollection as studies"
            + " where contacts.clinic = ?", Arrays
            .asList(new Object[] { wrappedObject }));

        return appService.query(c);
    }

}
