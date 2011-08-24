package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.DispatchSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction.Is;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchSpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.NotNullPreCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.UniqueCheck;
import edu.ualberta.med.biobank.common.wrappers.loggers.DispatchLogProvider;
import edu.ualberta.med.biobank.common.wrappers.tasks.NoActionWrapperQueryTask;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchWrapper extends DispatchBaseWrapper {
    private static final DispatchLogProvider LOG_PROVIDER = new DispatchLogProvider();
    private static final Property<String, Dispatch> WAYBILL_PROPERTY = DispatchPeer.SHIPMENT_INFO
        .to(ShipmentInfoPeer.WAYBILL);
    private static final Collection<Property<?, ? super Dispatch>> UNIQUE_WAYBILL_PER_SENDER_PROPERTIES = new ArrayList<Property<?, ? super Dispatch>>();

    static {
        UNIQUE_WAYBILL_PER_SENDER_PROPERTIES.add(WAYBILL_PROPERTY);
        UNIQUE_WAYBILL_PER_SENDER_PROPERTIES.add(DispatchPeer.SENDER_CENTER);
    }

    private final Map<DispatchSpecimenState, List<DispatchSpecimenWrapper>> dispatchSpecimenMap = new HashMap<DispatchSpecimenState, List<DispatchSpecimenWrapper>>();

    private boolean hasNewSpecimens = false;

    private boolean hasSpecimenStatesChanged = false;

    // TODO: Not sure if it's a good idea to maintain a list like this
    // internally. It can result in unwanted changes being persisted.
    private List<DispatchSpecimenWrapper> dispatchSpecimensToPersist = new ArrayList<DispatchSpecimenWrapper>();

    public DispatchWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchWrapper(WritableApplicationService appService,
        Dispatch dispatch) {
        super(appService, dispatch);
    }

    @Override
    public Dispatch getNewObject() throws Exception {
        Dispatch newObject = super.getNewObject();
        newObject.setState(DispatchState.CREATION.getId());
        return newObject;
    }

    public String getStateDescription() {
        DispatchState state = DispatchState
            .getState(getProperty(DispatchPeer.STATE));
        if (state == null)
            return "";
        return state.getLabel();
    }

    public DispatchState getDispatchState() {
        return DispatchState.getState(getState());
    }

    public String getFormattedPackedAt() {
        if (getShipmentInfo() != null)
            return DateFormatter.formatAsDateTime(getShipmentInfo()
                .getPackedAt());
        return null;
    }

    public String getFormattedReceivedAt() {
        if (getShipmentInfo() != null)
            return DateFormatter.formatAsDateTime(getShipmentInfo()
                .getReceivedAt());
        return null;
    }

    public boolean hasErrors() {
        return !getDispatchSpecimenCollectionWithState(
            DispatchSpecimenState.MISSING, DispatchSpecimenState.EXTRA)
            .isEmpty();
    }

    public Map<DispatchSpecimenState, List<DispatchSpecimenWrapper>> getMap() {
        return dispatchSpecimenMap;
    }

    private List<DispatchSpecimenWrapper> getDispatchSpecimenCollectionWithState(
        DispatchSpecimenState... states) {
        return getDispatchSpecimenCollectionWithState(dispatchSpecimenMap,
            getFastDispatchSpecimenCollection(), states);
    }

    private List<DispatchSpecimenWrapper> getDispatchSpecimenCollectionWithState(
        Map<DispatchSpecimenState, List<DispatchSpecimenWrapper>> map,
        List<DispatchSpecimenWrapper> list, DispatchSpecimenState... states) {

        if (map.isEmpty()) {
            for (DispatchSpecimenState state : DispatchSpecimenState.values()) {
                map.put(state, new ArrayList<DispatchSpecimenWrapper>());
            }
            for (DispatchSpecimenWrapper wrapper : list) {
                map.get(wrapper.getSpecimenState()).add(wrapper);
            }
        }

        if (states.length == 1) {
            return map.get(states[0]);
        } else {
            List<DispatchSpecimenWrapper> tmp = new ArrayList<DispatchSpecimenWrapper>();
            for (DispatchSpecimenState state : states) {
                tmp.addAll(map.get(state));
            }
            return tmp;
        }
    }

    public List<SpecimenWrapper> getSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> list = new ArrayList<SpecimenWrapper>();
        for (DispatchSpecimenWrapper da : getDispatchSpecimenCollection(false)) {
            list.add(da.getSpecimen());
        }
        if (sort) {
            Collections.sort(list);
        }
        return list;
    }

    public void addSpecimens(List<SpecimenWrapper> newSpecimens,
        DispatchSpecimenState state) throws BiobankCheckException {
        if (newSpecimens == null)
            return;

        // already added dsa
        List<DispatchSpecimenWrapper> currentDaList = getDispatchSpecimenCollection(false);
        List<DispatchSpecimenWrapper> newDispatchSpecimens = new ArrayList<DispatchSpecimenWrapper>();
        List<SpecimenWrapper> currentSpecimenList = new ArrayList<SpecimenWrapper>();

        for (DispatchSpecimenWrapper dsa : currentDaList) {
            currentSpecimenList.add(dsa.getSpecimen());
        }

        // new specimens added
        for (SpecimenWrapper specimen : newSpecimens) {
            if (specimen.getCurrentCenter().equals(getSenderCenter())
                || isInReceivedState()) {
                // in received state, let any specimen to be added just in case
                // it received something wrong
                if (!currentSpecimenList.contains(specimen)) {
                    DispatchSpecimenWrapper dsa = new DispatchSpecimenWrapper(
                        appService);
                    dsa.setSpecimen(specimen);
                    dsa.setDispatch(this);
                    dsa.setDispatchSpecimenState(state);
                    if (state == DispatchSpecimenState.EXTRA) {
                        specimen.setCurrentCenter(getReceiverCenter());
                        // remove position in case it has one in the previous
                        // center.
                        specimen.setParent(null, null);
                        dispatchSpecimensToPersist.add(dsa);
                    }
                    newDispatchSpecimens.add(dsa);
                    hasNewSpecimens = true;
                }
            } else
                throw new BiobankCheckException("Specimen "
                    + specimen.getInventoryId()
                    + " does not belong to this sender.");
        }
        addToDispatchSpecimenCollection(newDispatchSpecimens);
        resetMap();
    }

    @Override
    public void removeFromDispatchSpecimenCollection(
        List<? extends DispatchSpecimenBaseWrapper> dasToRemove) {
        super.removeFromDispatchSpecimenCollection(dasToRemove);
        resetMap();
    }

    public void removeSpecimens(List<SpecimenWrapper> spcs) {
        if (spcs == null) {
            throw new NullPointerException();
        }

        if (spcs.isEmpty())
            return;

        List<DispatchSpecimenWrapper> removeDispatchSpecimens = new ArrayList<DispatchSpecimenWrapper>();

        for (DispatchSpecimenWrapper dsa : getDispatchSpecimenCollection(false)) {
            if (spcs.contains(dsa.getSpecimen())) {
                removeDispatchSpecimens.add(dsa);
            }
        }
        removeFromDispatchSpecimenCollection(removeDispatchSpecimens);
    }

    public void removeDispatchSpecimens(List<DispatchSpecimenWrapper> dsaList) {
        if (dsaList == null) {
            throw new NullPointerException();
        }

        if (dsaList.isEmpty())
            return;

        List<DispatchSpecimenWrapper> currentDaList = getDispatchSpecimenCollection(false);
        List<DispatchSpecimenWrapper> removeDispatchSpecimens = new ArrayList<DispatchSpecimenWrapper>();

        for (DispatchSpecimenWrapper dsa : currentDaList) {
            if (dsaList.contains(dsa)) {
                removeDispatchSpecimens.add(dsa);
            }
        }
        removeFromDispatchSpecimenCollection(removeDispatchSpecimens);
    }

    // TODO: IMHO methods like this shouldn't even exist in the wrapper. It
    // should be a static method in some DispatchUtilFunctions class that keeps
    // track of what was added, then persists them immediately. Having all this
    // tracking done in the wrapper just creates potential problems if several
    // methods that do tracking are called without persisting after - JMF
    public void receiveSpecimens(List<SpecimenWrapper> specimensToReceive) {
        List<DispatchSpecimenWrapper> nonProcessedSpecimens = getDispatchSpecimenCollectionWithState(DispatchSpecimenState.NONE);
        for (DispatchSpecimenWrapper ds : nonProcessedSpecimens) {
            if (specimensToReceive.contains(ds.getSpecimen())) {
                hasSpecimenStatesChanged = true;
                ds.setDispatchSpecimenState(DispatchSpecimenState.RECEIVED);
                ds.getSpecimen().setCurrentCenter(getReceiverCenter());
                dispatchSpecimensToPersist.add(ds);
            }
        }
        resetMap();
    }

    public boolean isInCreationState() {
        return getDispatchState() == null
            || DispatchState.CREATION.equals(getDispatchState());
    }

    public boolean isInTransitState() {
        return DispatchState.IN_TRANSIT.equals(getDispatchState());
    }

    public boolean isInReceivedState() {
        return DispatchState.RECEIVED.equals(getDispatchState());
    }

    public boolean hasBeenReceived() {
        return EnumSet.of(DispatchState.RECEIVED, DispatchState.CLOSED)
            .contains(getDispatchState());
    }

    public boolean isInClosedState() {
        return DispatchState.CLOSED.equals(getDispatchState());
    }

    public boolean isInLostState() {
        return DispatchState.LOST.equals(getDispatchState());
    }

    public void setState(DispatchState ds) {
        setState(ds.getId());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getSenderCenter() == null ? "" : getSenderCenter()
            .getNameShort() + "/");
        sb.append(getReceiverCenter() == null ? "" : getReceiverCenter()
            .getNameShort() + "/");
        sb.append(getShipmentInfo().getFormattedDateReceived());
        return sb.toString();
    }

    public boolean canBeSentBy(User user) {
        return canUpdate(user)
            && getSenderCenter().equals(user.getCurrentWorkingCenter())
            && isInCreationState() && hasDispatchSpecimens();
    }

    public boolean hasDispatchSpecimens() {
        return getSpecimenCollection(false) != null
            && !getSpecimenCollection(false).isEmpty();
    }

    public boolean canBeReceivedBy(User user) {
        return canUpdate(user)
            && getReceiverCenter().equals(user.getCurrentWorkingCenter())
            && isInTransitState();
    }

    public DispatchSpecimenWrapper getDispatchSpecimen(String inventoryId) {
        for (DispatchSpecimenWrapper dsa : getDispatchSpecimenCollection(false)) {
            if (dsa.getSpecimen().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    public List<DispatchSpecimenWrapper> getNonProcessedDispatchSpecimenCollection() {
        return getDispatchSpecimenCollectionWithState(DispatchSpecimenState.NONE);
    }

    public List<DispatchSpecimenWrapper> getExtraDispatchSpecimens() {
        return getDispatchSpecimenCollectionWithState(DispatchSpecimenState.EXTRA);
    }

    public List<DispatchSpecimenWrapper> getMissingDispatchSpecimens() {
        return getDispatchSpecimenCollectionWithState(DispatchSpecimenState.MISSING);
    }

    public List<DispatchSpecimenWrapper> getReceivedDispatchSpecimens() {
        return getDispatchSpecimenCollectionWithState(DispatchSpecimenState.RECEIVED);
    }

    private static final String FAST_DISPATCH_SPECIMEN_QRY = "select ra from "
        + DispatchSpecimen.class.getName() + " ra inner join fetch ra."
        + DispatchSpecimenPeer.SPECIMEN.getName()
        + " as spec inner join fetch spec."
        + SpecimenPeer.SPECIMEN_TYPE.getName() + " inner join fetch spec."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevent inner join fetch cevent."
        + CollectionEventPeer.PATIENT.getName() + " inner join fetch spec."
        + SpecimenPeer.ACTIVITY_STATUS.getName() + " where ra."
        + Property.concatNames(DispatchSpecimenPeer.DISPATCH, DispatchPeer.ID)
        + " = ?";

    // fast... from db. should only call this once then use the cached value
    public List<DispatchSpecimenWrapper> getFastDispatchSpecimenCollection() {
        if (!isPropertyCached(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION)) {
            List<DispatchSpecimen> results;
            // test hql
            HQLCriteria query = new HQLCriteria(FAST_DISPATCH_SPECIMEN_QRY,
                Arrays.asList(new Object[] { getId() }));
            try {
                results = appService.query(query);
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
            wrappedObject.setDispatchSpecimenCollection(results);
        }
        return getDispatchSpecimenCollection(false);
    }

    public boolean canBeClosedBy(User user) {
        return isInReceivedState() && canUpdate(user);
    }

    @Override
    protected void resetInternalFields() {
        resetMap();
        hasNewSpecimens = false;
        hasSpecimenStatesChanged = false;
        dispatchSpecimensToPersist.clear();
    }

    public void resetMap() {
        dispatchSpecimenMap.clear();
    }

    @Override
    public DispatchLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    private static final String DISPATCH_HQL_STRING = "from "
        + Dispatch.class.getName() + " as d inner join fetch d."
        + DispatchPeer.SHIPMENT_INFO.getName() + " as s ";

    /**
     * Search for shipments in the site with the given waybill
     */
    public static List<DispatchWrapper> getDispatchesByWaybill(
        WritableApplicationService appService, String waybill)
        throws ApplicationException {
        StringBuilder qry = new StringBuilder(DISPATCH_HQL_STRING + " where s."
            + ShipmentInfoPeer.WAYBILL.getName() + " = ?");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { waybill }));

        List<Dispatch> origins = appService.query(criteria);
        List<DispatchWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, DispatchWrapper.class);

        return shipments;
    }

    private static final String DISPATCHES_BY_DATE_RECEIVED_QRY = DISPATCH_HQL_STRING
        + " where s."
        + ShipmentInfoPeer.RECEIVED_AT.getName()
        + " >=? and s."
        + ShipmentInfoPeer.RECEIVED_AT.getName()
        + " <=? and (d."
        + Property.concatNames(DispatchPeer.RECEIVER_CENTER, CenterPeer.ID)
        + "= ? or d."
        + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
        + " = ?)";

    // Date should input with no hour/minute/seconds
    public static Date endOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * Search for shipments in the site with the given date received. Don't use
     * hour and minute.
     */
    public static List<DispatchWrapper> getDispatchesByDateReceived(
        WritableApplicationService appService, Date dateReceived,
        CenterWrapper<?> center) throws ApplicationException {

        Integer centerId = center.getId();
        HQLCriteria criteria = new HQLCriteria(
            DISPATCHES_BY_DATE_RECEIVED_QRY.toString(),
            Arrays.asList(new Object[] { dateReceived, endOfDay(dateReceived),
                centerId, centerId }));

        List<Dispatch> origins = appService.query(criteria);
        List<DispatchWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, DispatchWrapper.class);

        return shipments;
    }

    private static final String DISPATCHED_BY_DATE_SENT_QRY = DISPATCH_HQL_STRING
        + " where s."
        + ShipmentInfoPeer.PACKED_AT.getName()
        + " >= ? and s."
        + ShipmentInfoPeer.PACKED_AT.getName()
        + " <= ? and (d."
        + Property.concatNames(DispatchPeer.RECEIVER_CENTER, CenterPeer.ID)
        + "= ? or d."
        + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
        + " = ?)";

    public static List<DispatchWrapper> getDispatchesByDateSent(
        WritableApplicationService appService, Date dateSent,
        CenterWrapper<?> center) throws ApplicationException {
        Integer centerId = center.getId();
        HQLCriteria criteria = new HQLCriteria(DISPATCHED_BY_DATE_SENT_QRY,
            Arrays.asList(new Object[] { dateSent, endOfDay(dateSent),
                centerId, centerId }));

        List<Dispatch> origins = appService.query(criteria);
        List<DispatchWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, DispatchWrapper.class);

        return shipments;
    }

    @Override
    public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
        List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>();
        if (getSenderCenter() != null)
            centers.add(getSenderCenter());
        if (getReceiverCenter() != null)
            centers.add(getReceiverCenter());
        return centers;
    }

    public boolean hasNewSpecimens() {
        return hasNewSpecimens;
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(check().notNull(DispatchPeer.SENDER_CENTER));
        tasks.add(check().notNull(DispatchPeer.RECEIVER_CENTER));

        tasks.add(new NotNullPreCheck<Dispatch>(this,
            DispatchPeer.SENDER_CENTER));

        tasks.deleteRemoved(this, DispatchPeer.DISPATCH_SPECIMEN_COLLECTION);

        removeSpecimensFromParents(tasks);
        persistSpecimens(tasks);

        super.addPersistTasks(tasks);

        tasks.persistAdded(this, DispatchPeer.DISPATCH_SPECIMEN_COLLECTION);

        BiobankSessionAction checkWaybill = new UniqueCheck<Dispatch>(this,
            UNIQUE_WAYBILL_PER_SENDER_PROPERTIES);

        tasks.add(new IfAction<Dispatch>(this, WAYBILL_PROPERTY, Is.NOT_NULL,
            checkWaybill));

        tasks.add(new ResetInternalStateQueryTask(this));
    }

    private void persistSpecimens(TaskList tasks) {
        for (DispatchSpecimenWrapper dispatchSpecimen : dispatchSpecimensToPersist) {
            SpecimenWrapper specimen = dispatchSpecimen.getSpecimen();
            specimen.addPersistTasks(tasks);
        }
    }

    private void removeSpecimensFromParents(TaskList tasks) {
        if (DispatchState.IN_TRANSIT.equals(getDispatchState())) {
            Collection<DispatchSpecimenWrapper> dispatchSpecimens = getDispatchSpecimenCollection(false);
            for (DispatchSpecimenWrapper dispatchSpecimen : dispatchSpecimens) {
                SpecimenWrapper specimen = dispatchSpecimen.getSpecimen();
                specimen.setParent(null, null);
                specimen.addPersistTasks(tasks);
            }
        }
    }

    public boolean hasSpecimenStatesChanged() {
        return hasSpecimenStatesChanged;
    }

    private static class ResetInternalStateQueryTask extends
        NoActionWrapperQueryTask<DispatchWrapper> {
        public ResetInternalStateQueryTask(DispatchWrapper dispatch) {
            super(dispatch);
        }

        @Override
        public void afterExecute(SDKQueryResult result) {
            getWrapper().hasNewSpecimens = false;
            getWrapper().hasSpecimenStatesChanged = false;
            getWrapper().dispatchSpecimensToPersist.clear();
        }
    }
}
