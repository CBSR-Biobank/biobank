package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Address;
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
        Address address = wrappedObject.getAddress();
        if (address == null) {
            address = new Address();
            wrappedObject.setAddress(address);
        }
        addressWrapper = new AddressWrapper(appService, address);
    }

    public AddressWrapper getAddressWrapper() {
        return addressWrapper;
    }

    public void setAddressWrapper(AddressWrapper addressWrapper) {
        this.addressWrapper = addressWrapper;
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
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "activityStatus", "comment" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        if (!checkSiteNameUnique()) {
            throw new BiobankCheckException("A site with name \"" + getName()
                + "\" already exists.");
        }
    }

    private boolean checkSiteNameUnique() throws ApplicationException {
        HQLCriteria c;

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Site.class.getName()
                + " where name = ?", Arrays.asList(new Object[] { getName() }));
        } else {
            c = new HQLCriteria("from " + Site.class.getName()
                + " as site where site <> ? and name = ?", Arrays
                .asList(new Object[] { getWrappedObject(), getName() }));
        }

        List<Object> results = appService.query(c);
        return (results.size() == 0);
    }

    @Override
    protected Class<Site> getWrappedClass() {
        return Site.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    public int compareTo(SiteWrapper wrapper) {
        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();
        return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
            .equals(wrapperName) ? 0 : -1));
    }

    public Collection<StudyWrapper> getStudyWrapperCollection() {
        Collection<StudyWrapper> wrapperCollection = new HashSet<StudyWrapper>();
        Collection<Study> collection = wrappedObject.getStudyCollection();
        if (collection != null)
            for (Study study : collection) {
                wrapperCollection.add(new StudyWrapper(appService, study));
            }
        return wrapperCollection;
    }

    public List<StudyWrapper> getStudyWrapperCollectionSorted() {
        List<StudyWrapper> list = new ArrayList<StudyWrapper>(
            getStudyWrapperCollection());
        if (list.size() > 1) {
            Collections.sort(list);
        }
        return list;
    }

    public Collection<ClinicWrapper> getClinicWrapperCollection() {
        Collection<ClinicWrapper> wrapperCollection = new HashSet<ClinicWrapper>();
        Collection<Clinic> collection = wrappedObject.getClinicCollection();
        if (collection != null)
            for (Clinic clinic : collection) {
                wrapperCollection.add(new ClinicWrapper(appService, clinic));
            }
        return wrapperCollection;
    }

    public List<ClinicWrapper> getClinicWrapperCollectionSorted() {
        List<ClinicWrapper> list = new ArrayList<ClinicWrapper>(
            getClinicWrapperCollection());
        if (list.size() > 1) {
            Collections.sort(list);
        }
        return list;
    }

    public Collection<ContainerTypeWrapper> getContainerTypeWrapperCollection() {
        Collection<ContainerTypeWrapper> wrapperCollection = new HashSet<ContainerTypeWrapper>();
        Collection<ContainerType> collection = wrappedObject
            .getContainerTypeCollection();
        if (collection != null)
            for (ContainerType ct : collection) {
                wrapperCollection.add(new ContainerTypeWrapper(appService, ct));
            }
        return wrapperCollection;
    }

    public Collection<ContainerWrapper> getContainerWrapperCollection() {
        Collection<ContainerWrapper> wrapperCollection = new HashSet<ContainerWrapper>();
        Collection<Container> collection = wrappedObject
            .getContainerCollection();
        if (collection != null)
            for (Container c : collection) {
                wrapperCollection.add(new ContainerWrapper(appService, c));
            }
        return wrapperCollection;
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
