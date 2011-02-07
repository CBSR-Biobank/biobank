package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.SourcePeer;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Source;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class CenterWrapper<E extends Center> extends ModelWrapper<E> {

    public CenterWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CenterWrapper(WritableApplicationService appService, E c) {
        super(appService, c);
    }

    public String getName() {
        return getProperty(CenterPeer.NAME);
    }

    public void setName(String name) {
        setProperty(CenterPeer.NAME, name);
    }

    public String getNameShort() {
        return getProperty(CenterPeer.NAME_SHORT);
    }

    public void setNameShort(String name) {
        setProperty(CenterPeer.NAME_SHORT, name);
    }

    public AddressWrapper getAddress() {
        return getWrappedProperty(CenterPeer.ADDRESS, AddressWrapper.class);
    }

    public void setAddress(AddressWrapper address) {
        setWrappedProperty(CenterPeer.ADDRESS, address);
    }

    public ActivityStatusWrapper getActivityStatus() {
        return getWrappedProperty(CenterPeer.ACTIVITY_STATUS,
            ActivityStatusWrapper.class);
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        setWrappedProperty(CenterPeer.ACTIVITY_STATUS, activityStatus);
    }

    public String getComment() {
        return getProperty(CenterPeer.COMMENT);
    }

    public void setComment(String comment) {
        setProperty(CenterPeer.COMMENT, comment);
    }

    public Collection<DispatchWrapper> getSrcDispatchCollection(boolean sort) {
        return getWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION,
            DispatchWrapper.class, sort);
    }

    public void setSrcDispatchCollection(Collection<DispatchWrapper> collection) {
        setWrapperCollection(CenterPeer.SRC_DISPATCH_COLLECTION, collection);
    }

    public Collection<DispatchWrapper> getDstDispatchCollection(boolean sort) {
        return getWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION,
            DispatchWrapper.class, sort);
    }

    public void setDstDispatchCollection(Collection<DispatchWrapper> collection) {
        setWrapperCollection(CenterPeer.DST_DISPATCH_COLLECTION, collection);
    }

    public Collection<RequestWrapper> getRequestCollection(boolean sort) {
        return getWrapperCollection(CenterPeer.REQUEST_COLLECTION,
            RequestWrapper.class, sort);
    }

    public void setRequestCollection(Collection<RequestWrapper> collection) {
        setWrapperCollection(CenterPeer.REQUEST_COLLECTION, collection);
    }

    public static final String SOURCE_COUNT_QRY = "select count(source) from "
        + Source.class.getName() + " as source where "
        + Property.concatNames(SourcePeer.SOURCE_CENTER, ClinicPeer.ID)
        + " = ?";

    public long getSourceCount() throws ApplicationException, BiobankException {
        return getSourceCount(false);
    }

    /**
     * fast = true will execute a hql query. fast = false will call the
     * getShipmentCollection().size method
     */
    public long getSourceCount(boolean fast) throws ApplicationException,
        BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(SOURCE_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            List<Long> results = appService.query(criteria);
            if (results.size() != 1) {
                throw new BiobankQueryResultSizeException();
            }
            return results.get(0);
        }
        List<SourceWrapper> list = getSourceCollection();
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public List<SourceWrapper> getSourceCollection(boolean sort) {
        return getWrapperCollection(CenterPeer.SOURCE_COLLECTION,
            SourceWrapper.class, sort);
    }

    public List<SourceWrapper> getSourceCollection() {
        return getSourceCollection(true);
    }

    public void addSources(List<SourceWrapper> newSources) {
        addToWrapperCollection(CenterPeer.SOURCE_COLLECTION, newSources);
    }

    public void removeSources(List<SourceWrapper> removedSources) {
        removeFromWrapperCollection(CenterPeer.SOURCE_COLLECTION,
            removedSources);
    }

    /**
     * Search for a source in the center with the given date received
     */
    public SourceWrapper getSource(Date dateReceived) {
        List<SourceWrapper> sources = getSourceCollection();
        if (sources != null) {
            for (SourceWrapper ship : sources) {
                if (DateCompare.compare(ship.getDateReceived(), dateReceived) == 0)
                    return ship;
            }
        }
        return null;
    }

    /**
     * Search for a source in the center with the given date received and
     * patient number.
     */
    public SourceWrapper getSource(Date dateReceived, String patientNumber) {
        List<SourceWrapper> sources = getSourceCollection();
        if (sources != null)
            for (SourceWrapper source : sources)
                if (DateCompare.compare(source.getDateReceived(), dateReceived) == 0) {
                    List<PatientWrapper> patients = source
                        .getPatientCollection();
                    for (PatientWrapper p : patients)
                        if (p.getPnumber().equals(patientNumber))
                            return source;
                }
        return null;
    }

}
