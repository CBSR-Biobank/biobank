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
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchItemState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchBaseWrapper;
import edu.ualberta.med.biobank.model.Dispatch;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchWrapper extends DispatchBaseWrapper {

    private final Map<DispatchItemState, List<DispatchSpecimenWrapper>> dispatchSpecimenMap = new HashMap<DispatchItemState, List<DispatchSpecimenWrapper>>();

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

    public String getFormattedDeparted() {
        return DateFormatter.formatAsDateTime(getDepartedAt());
    }

    public boolean hasErrors() {
        return !getDispatchSpecimenCollectionWithState(
            DispatchItemState.MISSING, DispatchItemState.EXTRA).isEmpty();
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        if (getSender() == null) {
            throw new BiobankCheckException("Sender should be set");
        }
        if (getReceiver() == null) {
            throw new BiobankCheckException("Receiver should be set");
        }

        if (!checkWaybillUniqueForSender()) {
            throw new BiobankCheckException("A dispatch with waybill "
                + getShipmentInfo().getWaybill()
                + " already exists for sending site "
                + getSender().getNameShort());
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
        + Dispatch.class.getName() + " where "
        + Property.concatNames(DispatchPeer.SENDER, CenterPeer.ID) + "=? and "
        + ShipmentInfoPeer.WAYBILL.getName() + "=?";

    private boolean checkWaybillUniqueForSender() throws ApplicationException,
        BiobankCheckException {
        List<Object> params = new ArrayList<Object>();
        CenterWrapper<?> sender = getSender();
        if (sender == null) {
            throw new BiobankCheckException("sender site cannot be null");
        }
        params.add(sender.getId());
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
        DispatchItemState... states) {
        return getDispatchSpecimenCollectionWithState(dispatchSpecimenMap,
            getDispatchSpecimenCollection(false), states);
    }

    private List<DispatchSpecimenWrapper> getDispatchSpecimenCollectionWithState(
        Map<DispatchItemState, List<DispatchSpecimenWrapper>> map,
        List<DispatchSpecimenWrapper> list, DispatchItemState... states) {

        if (map.isEmpty()) {
            for (DispatchItemState state : DispatchItemState.values()) {
                map.put(state, new ArrayList<DispatchSpecimenWrapper>());
            }
            for (DispatchSpecimenWrapper wrapper : list) {
                map.get(wrapper.getState()).add(wrapper);
            }
        }

        if (states.length == 1) {
            return map.get(states[0]);
        } else {
            List<DispatchSpecimenWrapper> tmp = new ArrayList<DispatchSpecimenWrapper>();
            for (DispatchItemState state : states) {
                tmp.addAll(map.get(state));
            }
            return tmp;
        }
    }

    public List<SpecimenWrapper> getSpecimenCollection(boolean sort) {
        // TODO: cache?
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
        List<DispatchSpecimenWrapper> newDispatchAliquots = new ArrayList<DispatchSpecimenWrapper>();
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
                newDispatchAliquots.add(dsa);
            }
        }

        addToDispatchSpecimenCollection(newDispatchAliquots);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedDispatchedSpecimens.removeAll(newDispatchAliquots);
    }

    public void checkCanAddAliquot(List<SpecimenWrapper> currentAliquots,
        SpecimenWrapper aliquot) throws BiobankCheckException {
        if (aliquot.isNew()) {
            throw new BiobankCheckException("Cannot add aliquot "
                + aliquot.getInventoryId() + ": it has not already been saved");
        }
        if (!aliquot.isActive()) {
            throw new BiobankCheckException("Activity status of "
                + aliquot.getInventoryId() + " is not 'Active'."
                + " Check comments on this aliquot for more information.");
        }
        if (aliquot.getPosition() == null) {
            throw new BiobankCheckException("Cannot add aliquot "
                + aliquot.getInventoryId()
                + ": it has no position. A position should be first assigned.");
        }
        if (!aliquot.getParent().getSite().equals(getSender())) {
            throw new BiobankCheckException("Aliquot "
                + aliquot.getInventoryId() + " is currently assigned to site "
                + aliquot.getParent().getSite().getNameShort()
                + ". It should be first assigned to "
                + getSender().getNameShort() + " site.");
        }
        if (currentAliquots != null && currentAliquots.contains(aliquot)) {
            throw new BiobankCheckException(aliquot.getInventoryId()
                + " is already in this Dispatch.");
        }
        if (aliquot.isUsedInDispatch()) {
            throw new BiobankCheckException(aliquot.getInventoryId()
                + " is already in a Dispatch in-transit or in creation.");
        }
    }

    private List<DispatchSpecimenWrapper> getDispatchSpecimens(
        Collection<DispatchSpecimenWrapper> allDispatchItems,
        Collection<SpecimenWrapper> items) {
        List<DispatchSpecimenWrapper> dispatchItems = new ArrayList<DispatchSpecimenWrapper>();
        for (DispatchSpecimenWrapper dispatchItem : allDispatchItems) {
            if (items.contains(dispatchItem.getSpecimen())) {
                dispatchItems.add(dispatchItem);
            }
        }
        return dispatchItems;
    }

    @Override
    public void removeFromDispatchSpecimenCollection(
        List<DispatchSpecimenWrapper> dasToRemove) {
        super.removeFromDispatchSpecimenCollection(dasToRemove);
        dispatchSpecimenMap.clear();
    }

    public void removeAliquots(List<SpecimenWrapper> aliquotsToRemove) {
        if (aliquotsToRemove == null) {
            throw new NullPointerException();
        }

        if (aliquotsToRemove.isEmpty())
            return;

        List<DispatchSpecimenWrapper> currentDaList = getDispatchSpecimenCollection(false);
        List<DispatchSpecimenWrapper> removeDispatchAliquots = new ArrayList<DispatchSpecimenWrapper>();

        for (DispatchSpecimenWrapper dsa : currentDaList) {
            if (aliquotsToRemove.contains(dsa.getSpecimen())) {
                removeDispatchAliquots.add(dsa);
                deletedDispatchedSpecimens.add(dsa);
            }
        }
        removeFromDispatchSpecimenCollection(removeDispatchAliquots);
    }

    public void receiveSpecimens(List<SpecimenWrapper> specimensToReceive) {
        List<DispatchSpecimenWrapper> nonProcessedAliquots = getDispatchSpecimenCollectionWithState(DispatchItemState.NONE);
        for (DispatchSpecimenWrapper da : nonProcessedAliquots) {
            if (specimensToReceive.contains(da.getSpecimen())) {
                da.setState(DispatchItemState.RECEIVED.getId());
            }
        }

        dispatchSpecimenMap.clear();
    }

    public boolean isInCreationState() {
        return getState() == null || DispatchState.CREATION.equals(getState());
    }

    public boolean isInTransitState() {
        return DispatchState.CREATION.equals(getState());
    }

    public boolean isInReceivedState() {
        return DispatchState.RECEIVED.equals(getState());
    }

    public boolean hasBeenReceived() {
        return EnumSet.of(DispatchState.RECEIVED, DispatchState.CLOSED)
            .contains(getState());
    }

    public boolean isInClosedState() {
        return DispatchState.CLOSED.equals(getState());
    }

    private static final String DISPATCHES_IN_SITE_QRY = "from "
        + Dispatch.class.getName() + " where ("
        + Property.concatNames(DispatchPeer.SENDER, SitePeer.ID) + "=? or "
        + Property.concatNames(DispatchPeer.RECEIVER, SitePeer.ID) + "=?) and "
        + ShipmentInfoPeer.WAYBILL.getName() + "=?";

    /**
     * Search for shipments with the given waybill. Site can be the sender or
     * the receiver.
     */
    public static List<DispatchWrapper> getShipmentsInSite(
        WritableApplicationService appService, String waybill, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(DISPATCHES_IN_SITE_QRY,
            Arrays.asList(new Object[] { site.getId(), site.getId(), waybill }));
        List<Dispatch> shipments = appService.query(criteria);
        List<DispatchWrapper> wrappers = new ArrayList<DispatchWrapper>();
        for (Dispatch s : shipments) {
            wrappers.add(new DispatchWrapper(appService, s));
        }
        return wrappers;
    }

    private static final String DISPATCHES_IN_SITE_BY_DATE_SENT_QRY = "from "
        + Dispatch.class.getName() + " where ("
        + Property.concatNames(DispatchPeer.SENDER, SitePeer.ID) + "=? or "
        + Property.concatNames(DispatchPeer.RECEIVER, SitePeer.ID) + "=?) and "
        + ShipmentInfoPeer.RECEIVED_AT.getName() + ">=? and "
        + ShipmentInfoPeer.RECEIVED_AT.getName() + "<=?";

    /**
     * Search for shipments with the given date sent. Don't use hour and minute.
     * Site can be the sender or the receiver.
     */
    public static List<DispatchWrapper> getDispatchesInSiteByDateSent(
        WritableApplicationService appService, Date dateReceived,
        SiteWrapper site) throws ApplicationException {
        Calendar cal = Calendar.getInstance();
        // date at 0:0am
        cal.setTime(dateReceived);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        // date at 0:0pm
        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime();
        HQLCriteria criteria = new HQLCriteria(
            DISPATCHES_IN_SITE_BY_DATE_SENT_QRY, Arrays.asList(new Object[] {
                site.getId(), site.getId(), startDate, endDate }));
        List<Dispatch> shipments = appService.query(criteria);
        List<DispatchWrapper> wrappers = new ArrayList<DispatchWrapper>();
        for (Dispatch s : shipments) {
            wrappers.add(new DispatchWrapper(appService, s));
        }
        return wrappers;
    }

    private static final String DISPATCHES_IN_SITE_BY_DATE_RECEIVED_QRY = "from "
        + Dispatch.class.getName()
        + " where ("
        + Property.concatNames(DispatchPeer.SENDER, SitePeer.ID)
        + "=? or "
        + Property.concatNames(DispatchPeer.RECEIVER, SitePeer.ID)
        + "=?) and "
        + ShipmentInfoPeer.RECEIVED_AT.getName()
        + " >=? and "
        + ShipmentInfoPeer.RECEIVED_AT.getName() + " <= ?";

    /**
     * Search for shipments with the given date received. Don't use hour and
     * minute. Site can be the sender or the receiver.
     */
    public static List<DispatchWrapper> getShipmentsInSiteByDateReceived(
        WritableApplicationService appService, Date dateReceived,
        SiteWrapper site) throws ApplicationException {
        Calendar cal = Calendar.getInstance();
        // date at 0:0am
        cal.setTime(dateReceived);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        // date at 0:0pm
        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime();
        HQLCriteria criteria = new HQLCriteria(
            DISPATCHES_IN_SITE_BY_DATE_RECEIVED_QRY,
            Arrays.asList(new Object[] { site.getId(), site.getId(), startDate,
                endDate }));
        List<Dispatch> shipments = appService.query(criteria);
        List<DispatchWrapper> wrappers = new ArrayList<DispatchWrapper>();
        for (Dispatch s : shipments) {
            wrappers.add(new DispatchWrapper(appService, s));
        }
        return wrappers;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getSender() == null ? "" : getSender().getNameShort() + "/");
        sb.append(getReceiver() == null ? "" : getReceiver().getNameShort()
            + "/");
        sb.append(getShipmentInfo().getFormattedDateReceived());
        return sb.toString();
    }

    public boolean canBeSentBy(User user, SiteWrapper site) {
        return canUpdate(user) && getSender().equals(site)
            && isInCreationState() && hasDispatchSpecimens();
    }

    public boolean hasDispatchSpecimens() {
        return getSpecimenCollection() != null
            && !getSpecimenCollection().isEmpty();
    }

    public boolean canBeReceivedBy(User user, SiteWrapper site) {
        return canUpdate(user) && getReceiver().equals(site)
            && isInTransitState();
    }

    public DispatchSpecimenWrapper getDispatchSpecimen(String inventoryId) {
        for (DispatchSpecimenWrapper dsa : getDispatchSpecimenCollection(false)) {
            if (dsa.getSpecimen().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    public void setState(DispatchState ds) {
        setState(ds.getId());
    }
}
