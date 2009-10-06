package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyWrapper extends ModelWrapper<Study> implements
    Comparable<StudyWrapper> {

    public StudyWrapper(WritableApplicationService appService,
        Study wrappedObject) {
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

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setNameShort(String nameShort) {
        String oldNameShort = getNameShort();
        wrappedObject.setNameShort(nameShort);
        propertyChangeSupport.firePropertyChange("name", oldNameShort,
            nameShort);
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

    public Site getSite() {
        return wrappedObject.getSite();
    }

    public void setSiteWrapper(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = siteWrapper.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name", "nameShort", "activityStatus", "comment",
            "site" };
    }

    @Override
    protected Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        checkStudyNameUnique();
    }

    private void checkStudyNameUnique() throws BiobankCheckException, Exception {
        HQLCriteria c;

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Study.class.getName()
                + " where site = ? and name = ?", Arrays.asList(new Object[] {
                getSite(), getName() }));
        } else {
            c = new HQLCriteria("from " + Study.class.getName()
                + " as study where site = ? and name = ?  and study <> ?",
                Arrays.asList(new Object[] { getSite(), getName(),
                    getWrappedObject() }));
        }

        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A study with name \"" + getName()
                + "\" already exists.");
        }

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Study.class.getName()
                + " site = ? and study.nameShort = ?", Arrays
                .asList(new Object[] { getSite(), getNameShort() }));
        } else {
            c = new HQLCriteria("from " + Study.class.getName() + " as study"
                + " where site = ? and study.nameShort = ? and study <> ?",
                Arrays.asList(new Object[] { getSite(), getNameShort(),
                    getWrappedObject() }));
        }

        results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException("A study with short name \""
                + getNameShort() + "\" already exists.");
        }
    }

    @Override
    public int compareTo(StudyWrapper wrapper) {
        String myNameShort = wrappedObject.getNameShort();
        String wrapperNameShort = wrapper.wrappedObject.getNameShort();

        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();

        return ((myNameShort.compareTo(wrapperNameShort) > 0) ? 1
            : (myNameShort.equals(wrapperNameShort) ? (myName
                .compareTo(wrapperName) > 0) ? 1
                : (myName.equals(wrapperName) ? 0 : -1) : -1));
    }

    public Collection<ContactWrapper> getContactCollection() {
        Collection<ContactWrapper> wrapperCollection = new HashSet<ContactWrapper>();
        Collection<Contact> collection = wrappedObject.getContactCollection();
        if (collection != null)
            for (Contact contact : collection) {
                wrapperCollection.add(new ContactWrapper(appService, contact));
            }
        return wrapperCollection;
    }

    public List<ContactWrapper> getClinicCollectionSorted() {
        List<ContactWrapper> list = new ArrayList<ContactWrapper>(
            getContactCollection());
        if (list.size() > 1) {
            Collections.sort(list);
        }
        return list;
    }

    public Collection<SampleStorageWrapper> getSampleStorageCollection() {
        Collection<SampleStorageWrapper> wrapperCollection = new HashSet<SampleStorageWrapper>();
        Collection<SampleStorage> collection = wrappedObject
            .getSampleStorageCollection();
        if (collection != null)
            for (SampleStorage ss : collection) {
                wrapperCollection.add(new SampleStorageWrapper(appService, ss));
            }
        return wrapperCollection;
    }

    public List<SampleStorageWrapper> getSampleStorageCollectionSorted() {
        List<SampleStorageWrapper> list = new ArrayList<SampleStorageWrapper>(
            getSampleStorageCollection());
        if (list.size() > 1) {
            Collections.sort(list);
        }
        return list;
    }

    public Collection<SampleSource> getSampleSourceCollection() {
        return wrappedObject.getSampleSourceCollection();
    }

    public void setSampleSourceCollection(
        Collection<SampleSource> sampleSourceCollection) {
        wrappedObject.setSampleSourceCollection(sampleSourceCollection);

    }

    public void setSampleStorageCollection(Collection<SampleStorage> collection) {
        wrappedObject.setSampleStorageCollection(collection);
    }

    public Collection<PvInfo> getPvInfoCollection() {
        return wrappedObject.getPvInfoCollection();
    }

    public void setPvInfoCollection(Collection<PvInfo> pvInfoCollection) {
        wrappedObject.setPvInfoCollection(pvInfoCollection);
    }

}
