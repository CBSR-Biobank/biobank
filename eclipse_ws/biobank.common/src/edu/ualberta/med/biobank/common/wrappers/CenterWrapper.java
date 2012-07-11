package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.AddressPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.util.WrapperUtil;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class CenterWrapper<E extends Center> extends
    CenterBaseWrapper<E> {
    private static final String RECEIVING_WITH_ERRORS_DISPATCH_COLLECTION_CACHE_KEY =
        "receivingWithErrorsDispatchCollection"; //$NON-NLS-1$
    private static final String RECEIVING_DISPATCH_COLLECTION_CACHE_KEY =
        "receivingDispatchCollection"; //$NON-NLS-1$
    private static final String IN_TRANSIT_RECEIVE_DISPATCH_COLLECTION_CACHE_KEY =
        "inTransitReceiveDispatchCollection"; //$NON-NLS-1$
    private static final String IN_CREATION_DISPATCH_COLLECTION_CACHE_KEY =
        "inCreationDispatchCollection"; //$NON-NLS-1$
    private static final String IN_TRANSIT_SENT_DISPATCH_COLLECTION_CACHE_KEY =
        "inTransitSentDispatchCollection"; //$NON-NLS-1$

    private static final String ALL_CENTERS_HQL_STRING = "from " //$NON-NLS-1$
        + Center.class.getName();

    public CenterWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CenterWrapper(WritableApplicationService appService, E c) {
        super(appService, c);
    }

    @Override
    public int compareTo(ModelWrapper<E> wrapper) {
        if (wrapper instanceof CenterWrapper) {
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();
            return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
                : -1));
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    protected List<Property<?, ? super E>> getProperties() {
        List<Property<?, ? super E>> aList =
            new ArrayList<Property<?, ? super E>>();

        aList.addAll(super.getProperties());

        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.CITY));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.POSTAL_CODE));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.PROVINCE));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.STREET1));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.STREET2));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.PHONE_NUMBER));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.FAX_NUMBER));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.EMAIL_ADDRESS));
        aList.add(CenterPeer.ADDRESS.wrap(AddressPeer.COUNTRY));

        return aList;
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
        initAddress().setProperty(AddressPeer.STREET1, street1);
    }

    public String getStreet2() {
        return getProperty(getAddress(), AddressPeer.STREET2);
    }

    public void setStreet2(String street2) {
        initAddress().setProperty(AddressPeer.STREET2, street2);
    }

    public String getCity() {
        return getProperty(getAddress(), AddressPeer.CITY);
    }

    public void setCity(String city) {
        initAddress().setProperty(AddressPeer.CITY, city);
    }

    public String getProvince() {
        return getProperty(getAddress(), AddressPeer.PROVINCE);
    }

    public void setProvince(String province) {
        initAddress().setProperty(AddressPeer.PROVINCE, province);
    }

    public String getPostalCode() {
        return getProperty(getAddress(), AddressPeer.POSTAL_CODE);
    }

    public void setPostalCode(String postalCode) {
        initAddress().setProperty(AddressPeer.POSTAL_CODE, postalCode);
    }

    public String getPhoneNumber() {
        return getProperty(getAddress(), AddressPeer.PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        initAddress().setProperty(AddressPeer.PHONE_NUMBER, phoneNumber);
    }

    public String getFaxNumber() {
        return getProperty(getAddress(), AddressPeer.FAX_NUMBER);
    }

    public void setFaxNumber(String faxNumber) {
        initAddress().setProperty(AddressPeer.FAX_NUMBER, faxNumber);
    }

    public String getEmailAddress() {
        return getProperty(getAddress(), AddressPeer.EMAIL_ADDRESS);
    }

    public void setEmailAddress(String emailAddress) {
        initAddress().setProperty(AddressPeer.EMAIL_ADDRESS, emailAddress);
    }

    public String getCountry() {
        return getProperty(getAddress(), AddressPeer.COUNTRY);
    }

    public void setCountry(String country) {
        initAddress().setProperty(AddressPeer.COUNTRY, country);
    }

    public long getProcessingEventCount() throws ApplicationException,
        BiobankException {
        return getProcessingEventCount(false);
    }

    public long getProcessingEventCount(boolean fast)
        throws ApplicationException, BiobankException {
        return getPropertyCount(CenterPeer.PROCESSING_EVENTS, fast);
    }

    public static List<CenterWrapper<?>> getCenters(
        WritableApplicationService appService) throws ApplicationException {
        StringBuilder qry = new StringBuilder(ALL_CENTERS_HQL_STRING);
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            new ArrayList<Object>());

        List<Center> centers = appService.query(criteria);
        List<CenterWrapper<?>> centerWrappers = ModelWrapper
            .wrapModelCollection(appService, centers, null);

        return centerWrappers;
    }

    public static List<CenterWrapper<?>> getOtherCenters(
        WritableApplicationService appService, CenterWrapper<?> center)
        throws ApplicationException {
        List<CenterWrapper<?>> centers = getCenters(appService);
        centers.remove(center);
        return centers;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitSentDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) cache
            .get(IN_TRANSIT_SENT_DISPATCH_COLLECTION_CACHE_KEY);
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSrcDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.IN_TRANSIT.equals(dispatch
                        .getDispatchState())) {
                        shipCollection.add(dispatch);
                    }
                }
                cache.put(IN_TRANSIT_SENT_DISPATCH_COLLECTION_CACHE_KEY,
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInTransitReceiveDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) cache
            .get(IN_TRANSIT_RECEIVE_DISPATCH_COLLECTION_CACHE_KEY);
        if (shipCollection == null) {
            List<DispatchWrapper> children = getDstDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.IN_TRANSIT.equals(dispatch
                        .getDispatchState())) {
                        shipCollection.add(dispatch);
                    }
                }
                cache.put(IN_TRANSIT_RECEIVE_DISPATCH_COLLECTION_CACHE_KEY,
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivingNoErrorsDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) cache
            .get(RECEIVING_DISPATCH_COLLECTION_CACHE_KEY);
        if (shipCollection == null) {
            List<DispatchWrapper> children = getDstDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.RECEIVED.equals(dispatch
                        .getDispatchState()) && !dispatch.hasErrors()) {
                        shipCollection.add(dispatch);
                    }
                }
                cache.put(RECEIVING_DISPATCH_COLLECTION_CACHE_KEY,
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getReceivingWithErrorsDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) cache
            .get(RECEIVING_WITH_ERRORS_DISPATCH_COLLECTION_CACHE_KEY);
        if (shipCollection == null) {
            List<DispatchWrapper> children = getDstDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.RECEIVED.equals(dispatch
                        .getDispatchState()) && dispatch.hasErrors()) {
                        shipCollection.add(dispatch);
                    }
                }
                cache.put(RECEIVING_WITH_ERRORS_DISPATCH_COLLECTION_CACHE_KEY,
                    shipCollection);
            }
        }
        return shipCollection;
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getInCreationDispatchCollection() {
        List<DispatchWrapper> shipCollection = (List<DispatchWrapper>) cache
            .get(IN_CREATION_DISPATCH_COLLECTION_CACHE_KEY);
        if (shipCollection == null) {
            List<DispatchWrapper> children = getSrcDispatchCollection(false);
            if (children != null) {
                shipCollection = new ArrayList<DispatchWrapper>();
                for (DispatchWrapper dispatch : children) {
                    if (DispatchState.CREATION.equals(dispatch
                        .getDispatchState())) {
                        shipCollection.add(dispatch);
                    }
                }
                cache.put(IN_CREATION_DISPATCH_COLLECTION_CACHE_KEY,
                    shipCollection);
            }
        }
        return shipCollection;
    }

    public static List<? extends ModelWrapper<?>> getInTransitReceiveDispatchCollection(
        CenterWrapper<?> center) {
        return center.getInTransitReceiveDispatchCollection();
    }

    public static List<? extends ModelWrapper<?>> getReceivingNoErrorsDispatchCollection(
        CenterWrapper<?> center) {
        return center.getReceivingNoErrorsDispatchCollection();
    }

    public static List<? extends ModelWrapper<?>> getInCreationDispatchCollection(
        CenterWrapper<?> center) {
        return center.getInCreationDispatchCollection();
    }

    public static List<? extends ModelWrapper<?>> getReceivingWithErrorsDispatchCollection(
        CenterWrapper<?> center) {
        return center.getReceivingWithErrorsDispatchCollection();
    }

    public static List<? extends ModelWrapper<?>> getInTransitSentDispatchCollection(
        CenterWrapper<?> center) {
        return center.getInTransitSentDispatchCollection();
    }

    @SuppressWarnings("nls")
    private static final String CHILD_SPECIMENS_COUNT_QRY =
        "select count(childSpcs) from "
            + Specimen.class.getName()
            + " sp join sp."
            + SpecimenPeer.CHILD_SPECIMENS.getName()
            + " as childSpcs where childSpcs."
            + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
            + "=?";

    public Long getAliquotedSpecimenCount() throws Exception {
        HQLCriteria criteria = new HQLCriteria(CHILD_SPECIMENS_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    @SuppressWarnings("nls")
    public static final String COLLECTION_EVENT_COUNT_QRY =
        "select count(distinct cevent) from "
            + Specimen.class.getName()
            + " as spc join spc."
            + SpecimenPeer.COLLECTION_EVENT.getName()
            + " as cevent where spc."
            + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
            + "=?";

    /**
     * Count events for specimen that are currently at this site
     */
    public long getCollectionEventCount() throws ApplicationException,
        BiobankException {
        HQLCriteria criteria = new HQLCriteria(COLLECTION_EVENT_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    @Override
    public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
        return Arrays.asList(this);
    }

    @SuppressWarnings("nls")
    public static final String CENTER_FROM_ID_QRY = "from "
        + Center.class.getName() + " where " + CenterPeer.ID.getName() + " = ?";

    public static CenterWrapper<?> getCenterFromId(
        WritableApplicationService appService, Integer centerId)
        throws Exception {
        HQLCriteria criteria = new HQLCriteria(CENTER_FROM_ID_QRY,
            Arrays.asList(new Object[] { centerId }));
        List<Center> centers = appService.query(criteria);
        if (centers.size() == 0)
            return null;
        return WrapperUtil.wrapModel(appService, centers.get(0), null);

    }

    // TODO: remove if allowing bi-direcitonal links.
    // public List<DispatchWrapper> getSrcDispatchCollection(boolean sort) {
    // return HQLAccessor.getCachedCollection(this,
    // DispatchPeer.SENDER_CENTER, Dispatch.class, DispatchWrapper.class,
    // sort);
    // }
    //
    // public List<DispatchWrapper> getDstDispatchCollection(boolean sort) {
    // return HQLAccessor.getCachedCollection(this,
    // DispatchPeer.RECEIVER_CENTER, Dispatch.class,
    // DispatchWrapper.class, sort);
    // }
    //
    // public List<SpecimenWrapper> getSpecimenCollection(boolean sort) {
    // return HQLAccessor.getCachedCollection(this,
    // SpecimenPeer.CURRENT_CENTER, Specimen.class, SpecimenWrapper.class,
    // sort);
    // }
    //
    // public List<OriginInfoWrapper> getOriginInfoCollection(boolean sort) {
    // return HQLAccessor.getCachedCollection(this, OriginInfoPeer.CENTER,
    // OriginInfo.class, OriginInfoWrapper.class, sort);
    // }
    //
    // public List<ProcessingEventWrapper> getProcessingEventCollection(
    // boolean sort) {
    // return HQLAccessor.getCachedCollection(this,
    // ProcessingEventPeer.CENTER, ProcessingEvent.class,
    // ProcessingEventWrapper.class, sort);
    // }

    @SuppressWarnings("nls")
    private static final String PENDING_REQUEST_STRING = "select distinct(ra."
        + RequestSpecimenPeer.REQUEST.getName()
        + ") from "
        + RequestSpecimen.class.getName()
        + " ra where ra."
        + Property.concatNames(RequestSpecimenPeer.SPECIMEN,
            SpecimenPeer.CURRENT_CENTER) + " = ? and ra.state = "
        + RequestSpecimenState.AVAILABLE_STATE.getId() + " or ra.state = "
        + RequestSpecimenState.PULLED_STATE.getId();

    public static List<? extends ModelWrapper<?>> getRequestCollection(
        WritableApplicationService appService, CenterWrapper<?> center)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(PENDING_REQUEST_STRING,
            Arrays.asList(new Object[] { center.getWrappedObject() }));
        List<Request> requests = appService.query(criteria);
        if (requests.size() == 0)
            return new ArrayList<RequestWrapper>();
        return wrapModelCollection(appService, requests,
            RequestWrapper.class);
    }

    public List<StudyWrapper> getStudyCollection() {
        return Collections.emptyList();
    }

}
