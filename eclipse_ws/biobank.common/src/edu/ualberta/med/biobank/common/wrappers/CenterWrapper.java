package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.AddressPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Center;
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
