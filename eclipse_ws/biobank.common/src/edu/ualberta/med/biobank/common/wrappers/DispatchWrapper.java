package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchBaseWrapper;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchWrapper extends DispatchBaseWrapper {

    private final Map<DispatchSpecimenState, List<DispatchSpecimenWrapper>> dispatchSpecimenMap = new HashMap<DispatchSpecimenState, List<DispatchSpecimenWrapper>>();

    private List<DispatchSpecimenWrapper> deletedDispatchedSpecimens = new ArrayList<DispatchSpecimenWrapper>();

    public DispatchWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchWrapper(WritableApplicationService appService,
        Dispatch dispatch) {
        super(appService, dispatch);
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

    public boolean hasErrors() {
        return !getDispatchSpecimenCollectionWithState(
            DispatchSpecimenState.MISSING, DispatchSpecimenState.EXTRA)
            .isEmpty();
    }

    public Map<DispatchSpecimenState, List<DispatchSpecimenWrapper>> getMap() {
        return dispatchSpecimenMap;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        if (getSenderCenter() == null) {
            throw new BiobankCheckException("Sender should be set");
        }
        if (getReceiverCenter() == null) {
            throw new BiobankCheckException("Receiver should be set");
        }

        if (!checkWaybillUniqueForSender()) {
            throw new BiobankCheckException("A dispatch with waybill "
                + getShipmentInfo().getWaybill()
                + " already exists for sending site "
                + getSenderCenter().getNameShort());
        }
    }

    @Override
    protected void persistDependencies(Dispatch origObject) throws Exception {
        for (DispatchSpecimenWrapper dsa : deletedDispatchedSpecimens) {
            if (!dsa.isNew()) {
                dsa.delete();
            }
        }
    }

    private static final String WAYBILL_UNIQUE_FOR_SENDER_QRY = "from "
        + Dispatch.class.getName()
        + " where "
        + Property.concatNames(DispatchPeer.SENDER_CENTER, CenterPeer.ID)
        + "=? and "
        + Property.concatNames(DispatchPeer.SHIPMENT_INFO,
            ShipmentInfoPeer.WAYBILL) + "=?";

    private boolean checkWaybillUniqueForSender() throws ApplicationException,
        BiobankCheckException {
        List<Object> params = new ArrayList<Object>();
        CenterWrapper<?> sender = getSenderCenter();
        if (sender == null) {
            throw new BiobankCheckException("sender site cannot be null");
        }
        params.add(sender.getId());
        if (getShipmentInfo() == null)
            params.add("");
        else
            params.add(getShipmentInfo().getWaybill());

        StringBuilder qry = new StringBuilder(WAYBILL_UNIQUE_FOR_SENDER_QRY);
        if (!isNew()) {
            qry.append(" and id <> ?");
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(qry.toString(), params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
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
                map.get(wrapper.getDispatchSpecimenState()).add(wrapper);
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

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(true);
    }

    public void addSpecimens(List<SpecimenWrapper> newSpecimens) {
        if (newSpecimens == null)
            return;

        // already added dsa
        List<DispatchSpecimenWrapper> currentDaList = getDispatchSpecimenCollection(false);
        List<DispatchSpecimenWrapper> newDispatchSpecimens = new ArrayList<DispatchSpecimenWrapper>();
        List<SpecimenWrapper> currentAliquotList = new ArrayList<SpecimenWrapper>();

        for (DispatchSpecimenWrapper dsa : currentDaList) {
            currentAliquotList.add(dsa.getSpecimen());
        }

        // new aliquots added
        for (SpecimenWrapper aliquot : newSpecimens) {
            if (!currentAliquotList.contains(aliquot)) {
                DispatchSpecimenWrapper dsa = new DispatchSpecimenWrapper(
                    appService);
                dsa.setSpecimen(aliquot);
                dsa.setDispatch(this);
                dsa.setDispatchSpecimenState(DispatchSpecimenState.NONE);
                dispatchSpecimenMap.put(DispatchSpecimenState.NONE,
                    new ArrayList<DispatchSpecimenWrapper>());
                newDispatchSpecimens.add(dsa);
                dispatchSpecimenMap.get(DispatchSpecimenState.NONE).add(dsa);
            }
        }

        addToDispatchSpecimenCollection(newDispatchSpecimens);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedDispatchedSpecimens.removeAll(newDispatchSpecimens);
    }

    public static class CheckStatus {
        public CheckStatus(boolean b, String string) {
            this.ok = b;
            this.message = string;
        }

        public boolean ok = true;
        public String message;

    }

    public CheckStatus checkCanAddSpecimen(SpecimenWrapper spc,
        boolean checkAlreadyAdded) {
        return checkCanAddSpecimen(getSpecimenCollection(), spc,
            checkAlreadyAdded);
    }

    public CheckStatus checkCanAddSpecimen(
        List<SpecimenWrapper> currentAliquots, SpecimenWrapper aliquot,
        boolean checkAlreadyAdded) {
        if (aliquot.isNew()) {
            return new CheckStatus(false, "Cannot add aliquot "
                + aliquot.getInventoryId() + ": it has not been saved");
        }
        if (!aliquot.isActive()) {
            return new CheckStatus(false, "Activity status of "
                + aliquot.getInventoryId() + " is not 'Active'."
                + " Check comments on this aliquot for more information.");
        }
        if (!aliquot.getCurrentCenter().equals(getSenderCenter())) {
            return new CheckStatus(false, "Specimen "
                + aliquot.getInventoryId() + " is currently assigned to site "
                + aliquot.getCurrentCenter().getNameShort()
                + ". It should be first assigned to "
                + getSenderCenter().getNameShort() + " site.");
        }
        if (checkAlreadyAdded && currentAliquots != null
            && currentAliquots.contains(aliquot)) {
            return new CheckStatus(false, "Specimen "
                + aliquot.getInventoryId() + " is already in this Dispatch.");
        }
        if (aliquot.isUsedInDispatch()) {
            return new CheckStatus(false, "Specimen "
                + aliquot.getInventoryId()
                + " is already in an active dispatch.");
        }
        return new CheckStatus(true, "");
    }

    @Override
    public void removeFromDispatchSpecimenCollection(
        List<DispatchSpecimenWrapper> dasToRemove) {
        super.removeFromDispatchSpecimenCollection(dasToRemove);
        resetMap();
    }

    public void removeAliquots(List<DispatchSpecimenWrapper> dsaList) {
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
                deletedDispatchedSpecimens.add(dsa);
            }
        }
        removeFromDispatchSpecimenCollection(removeDispatchSpecimens);
    }

    public void receiveSpecimens(List<SpecimenWrapper> specimensToReceive) {
        List<DispatchSpecimenWrapper> nonProcessedAliquots = getDispatchSpecimenCollectionWithState(DispatchSpecimenState.NONE);
        for (DispatchSpecimenWrapper da : nonProcessedAliquots) {
            if (specimensToReceive.contains(da.getSpecimen())) {
                da.setDispatchSpecimenState(DispatchSpecimenState.RECEIVED);
                da.getSpecimen().setCurrentCenter(getReceiverCenter());
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
        return getSpecimenCollection() != null
            && !getSpecimenCollection().isEmpty();
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

    public void addExtraAliquots(List<SpecimenWrapper> extraAliquots) {
        List<DispatchSpecimenWrapper> daws = new ArrayList<DispatchSpecimenWrapper>();
        for (SpecimenWrapper a : extraAliquots) {
            DispatchSpecimenWrapper da = new DispatchSpecimenWrapper(appService);
            da.setSpecimen(a);
            da.setDispatch(this);
            da.setDispatchSpecimenState(DispatchSpecimenState.EXTRA);
            daws.add(da);
        }
        addToDispatchSpecimenCollection(daws);
        resetMap();
    }

    // fast... from db. should only call this once then use the cached value
    public List<DispatchSpecimenWrapper> getFastDispatchSpecimenCollection() {
        if (!isPropertyCached(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION)) {
            List<DispatchSpecimen> results = new ArrayList<DispatchSpecimen>();
            // test hql
            HQLCriteria query = new HQLCriteria(
                "select ra from "
                    + DispatchSpecimen.class.getName()
                    + " ra inner join fetch ra.specimen inner join fetch ra.specimen.specimenType inner join fetch ra.specimen.collectionEvent inner join fetch ra.specimen.collectionEvent.patient inner join fetch ra.specimen.activityStatus "
                    + " where ra.dispatch.id = ?",
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

    public void addAliquots(List<SpecimenWrapper> aliquots) {
        List<DispatchSpecimenWrapper> daws = new ArrayList<DispatchSpecimenWrapper>();
        for (SpecimenWrapper a : aliquots) {
            DispatchSpecimenWrapper da = new DispatchSpecimenWrapper(appService);
            da.setSpecimen(a);
            da.setDispatch(this);
            da.setDispatchSpecimenState(DispatchSpecimenState.NONE);
            daws.add(da);
        }
        addToDispatchSpecimenCollection(daws);
        resetMap();
    }

    public boolean canBeClosedBy(User user) {
        return isInReceivedState() && canUpdate(user);
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        resetMap();
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        resetMap();
    }

    public void resetMap() {
        dispatchSpecimenMap.clear();
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);

        DispatchState state = getDispatchState();

        if (site != null) {
            log.setCenter(site);
        } else {
            if (state.equals(DispatchState.CREATION)
                || state.equals(DispatchState.IN_TRANSIT)) {
                log.setCenter(getSenderCenter().getNameShort());
            } else {
                log.setCenter(getReceiverCenter().getNameShort());
            }
        }

        List<String> detailsList = new ArrayList<String>();
        if (details.length() > 0) {
            detailsList.add(details);
        }

        detailsList.add(new StringBuilder("state: ").append(
            getStateDescription()).toString());

        if (state.equals(DispatchState.CREATION)
            || state.equals(DispatchState.IN_TRANSIT)
            || state.equals(DispatchState.LOST)) {
            String packedAt = getFormattedPackedAt();
            if ((packedAt != null) && (packedAt.length() > 0)) {
                detailsList.add(new StringBuilder("packed at: ").append(
                    packedAt).toString());
            }
        }

        ShipmentInfoWrapper shipInfo = getShipmentInfo();
        if (shipInfo != null) {
            String receivedAt = shipInfo.getFormattedDateReceived();
            if ((receivedAt != null) && (receivedAt.length() > 0)) {
                detailsList.add(new StringBuilder("received at: ").append(
                    receivedAt).toString());
            }

            String waybill = shipInfo.getWaybill();
            if (waybill != null) {
                detailsList.add(new StringBuilder(", waybill: ")
                    .append(waybill).toString());
            }
        }
        log.setDetails(StringUtils.join(detailsList, ", "));
        log.setType("Dispatch");
        return log;
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

    /**
     * Search for shipments in the site with the given date received. Don't use
     * hour and minute.
     */
    public static List<DispatchWrapper> getDispatchesByDateReceived(
        WritableApplicationService appService, Date dateReceived)
        throws ApplicationException {

        StringBuilder qry = new StringBuilder(DISPATCH_HQL_STRING
            + " where DATE(s." + ShipmentInfoPeer.RECEIVED_AT.getName()
            + ") = DATE(?)");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { dateReceived }));

        List<Dispatch> origins = appService.query(criteria);
        List<DispatchWrapper> shipments = ModelWrapper.wrapModelCollection(
            appService, origins, DispatchWrapper.class);

        return shipments;
    }

    public static List<DispatchWrapper> getDispatchesByDateSent(
        WritableApplicationService appService, Date dateSent)
        throws ApplicationException {

        StringBuilder qry = new StringBuilder(DISPATCH_HQL_STRING
            + " where DATE(s." + ShipmentInfoPeer.PACKED_AT.getName()
            + ") = DATE(?)");
        HQLCriteria criteria = new HQLCriteria(qry.toString(),
            Arrays.asList(new Object[] { dateSent }));

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
}
