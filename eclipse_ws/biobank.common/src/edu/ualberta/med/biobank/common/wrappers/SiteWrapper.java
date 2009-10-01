package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> implements
    Comparable<SiteWrapper> {

    private AddressWrapper addressWrapper;

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
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

    @Override
    protected void firePropertyChanges(Site oldWrappedObject,
        Site newWrappedObject) {
        String[] members = new String[] { "name", "activityStatus", "comment",
            "site" };

        try {
            firePropertyChanges(members, oldWrappedObject, newWrappedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        if (checkSiteNameUnique()) {
            return DatabaseResult.OK;
        }
        return new DatabaseResult("A site with name \"" + getName()
            + "\" already exists.");
    }

    private boolean checkSiteNameUnique() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from " + Site.class.getName()
            + " where name = ?", Arrays.asList(new Object[] { getName() }));

        List<Object> results = appService.query(c);
        return (results.size() == 0);
    }

    @Override
    protected Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected DatabaseResult deleteChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public int compareTo(SiteWrapper wrapper) {
        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();
        return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
            .equals(wrapperName) ? 0 : -1));
    }

    public Collection<StudyWrapper> getStudyWrapperCollection() {
        Collection<StudyWrapper> collection = new HashSet<StudyWrapper>();
        for (Study study : wrappedObject.getStudyCollection()) {
            collection.add(new StudyWrapper(appService, study));
        }
        return collection;
    }

    public Collection<ClinicWrapper> getClinicWrapperCollection() {
        Collection<ClinicWrapper> collection = new HashSet<ClinicWrapper>();
        for (Clinic clinic : wrappedObject.getClinicCollection()) {
            collection.add(new ClinicWrapper(appService, clinic));
        }
        return collection;
    }

    public Collection<ContainerTypeWrapper> getContainerTypeWrapperCollection() {
        Collection<ContainerTypeWrapper> collection = new HashSet<ContainerTypeWrapper>();
        for (ContainerType ct : wrappedObject.getContainerTypeCollection()) {
            collection.add(new ContainerTypeWrapper(appService, ct));
        }
        return collection;
    }

    public Collection<ContainerWrapper> getContainerWrapperCollection() {
        Collection<ContainerWrapper> collection = new HashSet<ContainerWrapper>();
        for (Container c : wrappedObject.getContainerCollection()) {
            collection.add(new ContainerWrapper(appService, c));
        }
        return collection;
    }

    public Collection<ContainerWrapper> getTopContainerWrapperCollection()
        throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id = ? and position is null", Arrays
            .asList(new Object[] { wrappedObject.getId() }));
        List<Container> containers = appService.query(criteria);

        Collection<ContainerWrapper> wrappers = new HashSet<ContainerWrapper>();
        for (Container c : containers) {
            wrappers.add(new ContainerWrapper(appService, c));
        }
        return wrappers;
    }

    public List<ContainerWrapper> getTopContainerWrapperCollectionSorted()
        throws Exception {
        List<ContainerWrapper> result = new ArrayList<ContainerWrapper>(
            getTopContainerWrapperCollection());
        Collections.sort(result);
        return result;
    }

}
