package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.AddressPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class CenterWrapper<E extends Center> extends
    CenterBaseWrapper<E> {

    private Set<CollectionEventWrapper> deletedCollectionEvents = new HashSet<CollectionEventWrapper>();

    public CenterWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CenterWrapper(WritableApplicationService appService, E c) {
        super(appService, c);
    }

    private AddressWrapper initAddress() {
        AddressWrapper address = getAddress();
        if (address == null) {
            address = new AddressWrapper(appService);
            setAddress(address);
        }
        return address;
    }

    public String getStreet1() {
        return getProperty(getAddress(), AddressPeer.STREET1);
    }

    public void setStreet1(String street1) {
        setProperty(initAddress(), AddressPeer.STREET1, street1);
    }

    public String getStreet2() {
        return getProperty(getAddress(), AddressPeer.STREET2);
    }

    public void setStreet2(String street2) {
        setProperty(initAddress(), AddressPeer.STREET2, street2);
    }

    public String getCity() {
        return getProperty(getAddress(), AddressPeer.CITY);
    }

    public void setCity(String city) {
        setProperty(initAddress(), AddressPeer.CITY, city);
    }

    public String getProvince() {
        return getProperty(getAddress(), AddressPeer.PROVINCE);
    }

    public void setProvince(String province) {
        setProperty(initAddress(), AddressPeer.PROVINCE, province);
    }

    public String getPostalCode() {
        return getProperty(getAddress(), AddressPeer.POSTAL_CODE);
    }

    public void setPostalCode(String postalCode) {
        setProperty(initAddress(), AddressPeer.POSTAL_CODE, postalCode);
    }

    @Override
    public void addToCollectionEventCollection(
        List<CollectionEventWrapper> collectionEventCollection) {
        super.addToCollectionEventCollection(collectionEventCollection);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedCollectionEvents.removeAll(collectionEventCollection);
    }

    @Override
    public void removeFromCollectionEventCollection(
        List<CollectionEventWrapper> collectionEventCollection) {
        deletedCollectionEvents.addAll(collectionEventCollection);
        super.removeFromCollectionEventCollection(collectionEventCollection);
    }

    public static final String PROCESSING_EVENT_COUNT_QRY = "select count(proc) from "
        + ProcessingEvent.class.getName()
        + " as proc where "
        + Property.concatNames(ProcessingEventPeer.CENTER, CenterPeer.ID)
        + " = ?";

    public long getProcessingEventCount() throws ApplicationException,
        BiobankException {
        return getProcessingEventCount(false);
    }

    /**
     * fast = true will execute a hql query. fast = false will call the
     * getShipmentCollection().size method
     */
    public long getProcessingEventCount(boolean fast)
        throws ApplicationException, BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(PROCESSING_EVENT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<ProcessingEventWrapper> list = getProcessingEventCollection(false);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static final String COLLECTION_EVENT_COUNT_QRY = "select count(source) from "
        + CollectionEvent.class.getName()
        + " as source where "
        + Property
            .concatNames(CollectionEventPeer.SOURCE_CENTER, CenterPeer.ID)
        + " = ?";

    public long getCollectionEventCount() throws ApplicationException,
        BiobankException {
        return getCollectionEventCount(false);
    }

    /**
     * fast = true will execute a hql query. fast = false will call the
     * getShipmentCollection().size method
     */
    public long getCollectionEventCount(boolean fast)
        throws ApplicationException, BiobankException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(COLLECTION_EVENT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<CollectionEventWrapper> list = getCollectionEventCollection(false);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    /**
     * Search for a source in the center with the given date received
     */
    public CollectionEventWrapper getCollectionEvent(Date dateReceived) {
        List<CollectionEventWrapper> sources = getCollectionEventCollection(false);
        if (sources != null) {
            for (CollectionEventWrapper ship : sources) {
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
    public CollectionEventWrapper getCollectionEvent(Date dateReceived,
        String patientNumber) {
        List<CollectionEventWrapper> sources = getCollectionEventCollection(false);
        if (sources != null)
            for (CollectionEventWrapper source : sources)
                if (DateCompare.compare(source.getDateReceived(), dateReceived) == 0) {
                    List<PatientWrapper> patients = source
                        .getPatientCollection();
                    for (PatientWrapper p : patients)
                        if (p.getPnumber().equals(patientNumber))
                            return source;
                }
        return null;
    }

    @Override
    protected void persistDependencies(Center origObject) throws Exception {
        deleteCollectionEvents();
    }

    private void deleteCollectionEvents() throws Exception {
        for (CollectionEventWrapper ce : deletedCollectionEvents) {
            if (!ce.isNew()) {
                ce.delete();
            }
        }
    }

}
