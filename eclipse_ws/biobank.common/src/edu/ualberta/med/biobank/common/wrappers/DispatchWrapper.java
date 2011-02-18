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
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
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

    private final Map<DispatchItemState, List<DispatchAliquotWrapper>> dispatchAliquotsMap = new HashMap<DispatchItemState, List<DispatchAliquotWrapper>>();

    private final Map<DispatchItemState, List<DispatchSourceVesselWrapper>> dispatchSourceVesselsMap = new HashMap<DispatchItemState, List<DispatchSourceVesselWrapper>>();

    private List<DispatchAliquotWrapper> deletedDispatchedAliquots = new ArrayList<DispatchAliquotWrapper>();

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

    public boolean hasErrors() {
        boolean faultyAliquots = !getDispatchAliquotCollectionWithState(
            DispatchItemState.MISSING, DispatchItemState.EXTRA).isEmpty();
        boolean faultySourceVessels = !getDispatchSourceVesselCollectionWithState(
            DispatchItemState.MISSING, DispatchItemState.EXTRA).isEmpty();

        return faultyAliquots || faultySourceVessels;
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
                + getWaybill() + " already exists for sending site "
                + getSender().getNameShort());
        }
    }

    @Override
    protected void persistDependencies(Dispatch origObject) throws Exception {
        for (DispatchAliquotWrapper dsa : deletedDispatchedAliquots) {
            if (!dsa.isNew()) {
                dsa.delete();
            }
        }
    }

    private static final String WAYBILL_UNIQUE_FOR_SENDER_QRY = "from "
        + Dispatch.class.getName() + " where "
        + Property.concatNames(DispatchPeer.SENDER, CenterPeer.ID) + "=? and "
        + DispatchPeer.WAYBILL.getName() + "=?";

    private boolean checkWaybillUniqueForSender() throws ApplicationException,
        BiobankCheckException {
        List<Object> params = new ArrayList<Object>();
        CenterWrapper<?> sender = getSender();
        if (sender == null) {
            throw new BiobankCheckException("sender site cannot be null");
        }
        params.add(sender.getId());
        params.add(getWaybill());

        StringBuilder qry = new StringBuilder(WAYBILL_UNIQUE_FOR_SENDER_QRY);
        if (!isNew()) {
            qry.append(" and id <> ?");
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(qry.toString(), params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    private List<DispatchAliquotWrapper> getDispatchAliquotCollectionWithState(
        DispatchItemState... states) {
        return getDispatchItemCollectionWithState(dispatchAliquotsMap,
            getDispatchAliquotCollection(false), states);
    }

    private List<DispatchSourceVesselWrapper> getDispatchSourceVesselCollectionWithState(
        DispatchItemState... states) {
        return getDispatchItemCollectionWithState(dispatchSourceVesselsMap,
            getDispatchSourceVesselCollection(false), states);
    }

    private <T extends DispatchItemWrapper<?>> List<T> getDispatchItemCollectionWithState(
        Map<DispatchItemState, List<T>> map, List<T> list,
        DispatchItemState... states) {

        if (map.isEmpty()) {
            for (DispatchItemState state : DispatchItemState.values()) {
                map.put(state, new ArrayList<T>());
            }
            for (T wrapper : list) {
                map.get(wrapper.getState()).add(wrapper);
            }
        }

        if (states.length == 1) {
            return map.get(states[0]);
        } else {
            List<T> tmp = new ArrayList<T>();
            for (DispatchItemState state : states) {
                tmp.addAll(map.get(state));
            }
            return tmp;
        }
    }

    public List<AliquotWrapper> getAliquotCollection(boolean sort) {
        // TODO: cache?
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();
        for (DispatchAliquotWrapper da : getDispatchAliquotCollection(false)) {
            list.add(da.getAliquot());
        }
        if (sort) {
            Collections.sort(list);
        }
        return list;
    }

    public List<AliquotWrapper> getAliquotCollection() {
        return getAliquotCollection(true);
    }

    public List<SourceVesselWrapper> getSourceVesselCollection(boolean sort) {
        // TODO: cache?
        List<SourceVesselWrapper> list = new ArrayList<SourceVesselWrapper>();
        for (DispatchSourceVesselWrapper da : getDispatchSourceVesselCollection(false)) {
            list.add(da.getSourceVessel());
        }
        if (sort) {
            Collections.sort(list);
        }
        return list;
    }

    public List<SourceVesselWrapper> getSourceVesselCollection() {
        return getSourceVesselCollection(true);
    }

    public void addAliquots(List<AliquotWrapper> newAliquots) {
        if (newAliquots == null)
            return;

        // already added dsa
        List<DispatchAliquotWrapper> currentDaList = getDispatchAliquotCollection(false);
        List<DispatchAliquotWrapper> newDispatchAliquots = new ArrayList<DispatchAliquotWrapper>();
        List<AliquotWrapper> currentAliquotList = new ArrayList<AliquotWrapper>();

        for (DispatchAliquotWrapper dsa : currentDaList) {
            currentAliquotList.add(dsa.getAliquot());
        }

        // new aliquots added
        for (AliquotWrapper aliquot : newAliquots) {
            if (!currentAliquotList.contains(aliquot)) {
                DispatchAliquotWrapper dsa = new DispatchAliquotWrapper(
                    appService);
                dsa.setAliquot(aliquot);
                dsa.setDispatch(this);
                newDispatchAliquots.add(dsa);
            }
        }

        addToDispatchAliquotCollection(newDispatchAliquots);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedDispatchedAliquots.removeAll(newDispatchAliquots);
    }

    public void checkCanAddAliquot(List<AliquotWrapper> currentAliquots,
        AliquotWrapper aliquot) throws BiobankCheckException {
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

    private <T extends DispatchItemWrapper<?>> List<T> getDispatchItems(
        Collection<T> allDispatchItems, Collection<?> items) {
        List<T> dispatchItems = new ArrayList<T>();
        for (T dispatchItem : allDispatchItems) {
            if (items.contains(dispatchItem.getItem())) {
                dispatchItems.add(dispatchItem);
            }
        }
        return dispatchItems;
    }

    @Override
    public void removeFromDispatchAliquotCollection(
        List<DispatchAliquotWrapper> dasToRemove) {
        super.removeFromDispatchAliquotCollection(dasToRemove);
        dispatchAliquotsMap.clear();
    }

    public void removeAliquots(List<AliquotWrapper> aliquotsToRemove) {
        if (aliquotsToRemove == null) {
            throw new NullPointerException();
        }

        if (aliquotsToRemove.isEmpty())
            return;

        List<DispatchAliquotWrapper> currentDaList = getDispatchAliquotCollection(false);
        List<DispatchAliquotWrapper> removeDispatchAliquots = new ArrayList<DispatchAliquotWrapper>();

        for (DispatchAliquotWrapper dsa : currentDaList) {
            if (aliquotsToRemove.contains(dsa.getAliquot())) {
                removeDispatchAliquots.add(dsa);
                deletedDispatchedAliquots.add(dsa);
            }
        }
        removeFromDispatchAliquotCollection(removeDispatchAliquots);
    }

    public void receiveAliquots(List<AliquotWrapper> aliquotsToReceive) {
        List<DispatchAliquotWrapper> nonProcessedAliquots = getDispatchAliquotCollectionWithState(DispatchItemState.NONE);
        for (DispatchAliquotWrapper da : nonProcessedAliquots) {
            if (aliquotsToReceive.contains(da.getAliquot())) {
                da.setState(DispatchItemState.RECEIVED.getId());
            }
        }

        dispatchAliquotsMap.clear();
    }

    public void addToSourceVesselCollection(
        List<SourceVesselWrapper> newSourceVessels, DispatchItemState state) {
        List<DispatchSourceVesselWrapper> newDispatchSourceVessels = new ArrayList<DispatchSourceVesselWrapper>();

        for (SourceVesselWrapper sourceVessel : newSourceVessels) {
            // TODO: check
            // checkCanAddSourceVessel(currentSourceVessels, aliquot);

            DispatchSourceVesselWrapper da = new DispatchSourceVesselWrapper(
                appService);
            da.setSourceVessel(sourceVessel);
            da.setState(state.getId());
            da.setDispatch(this);

            newDispatchSourceVessels.add(da);
        }

        dispatchSourceVesselsMap.clear();
        addToDispatchSourceVesselCollection(newDispatchSourceVessels);
    }

    @Override
    public void removeFromDispatchSourceVesselCollection(
        List<DispatchSourceVesselWrapper> dasToRemove) {
        super.removeFromDispatchSourceVesselCollection(dasToRemove);

        dispatchSourceVesselsMap.clear();
    }

    public void removeSourceVessels(
        List<SourceVesselWrapper> sourceVesselsToRemove) {
        List<DispatchSourceVesselWrapper> allSvs = getDispatchSourceVesselCollection(false);
        List<DispatchSourceVesselWrapper> dsvsToRemove = getDispatchItems(
            allSvs, sourceVesselsToRemove);

        removeFromWrapperCollection(
            DispatchPeer.DISPATCH_SOURCE_VESSEL_COLLECTION, dsvsToRemove);
    }

    public void receiveSourceVessels(
        List<SourceVesselWrapper> sourceVesselsToReceive) {
        List<DispatchSourceVesselWrapper> nonProcessedSourceVessels = getDispatchSourceVesselCollectionWithState(DispatchItemState.NONE);
        for (DispatchSourceVesselWrapper dsv : nonProcessedSourceVessels) {
            if (sourceVesselsToReceive.contains(dsv.getSourceVessel())) {
                dsv.setState(DispatchItemState.RECEIVED.getId());
            }
        }

        dispatchSourceVesselsMap.clear();
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
        + DispatchPeer.WAYBILL.getName() + "=?";

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
        + DispatchPeer.DATE_RECEIVED.getName() + ">=? and "
        + DispatchPeer.DATE_RECEIVED.getName() + "<=?";

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
        + DispatchPeer.DATE_RECEIVED.getName()
        + " >=? and "
        + DispatchPeer.DATE_RECEIVED.getName() + " <= ?";

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
        sb.append(getFormattedDateReceived());
        return sb.toString();
    }

    public boolean canBeSentBy(User user, SiteWrapper site) {
        return canUpdate(user) && getSender().equals(site)
            && isInCreationState() && hasDispatchItems();
    }

    public boolean hasDispatchItems() {
        boolean hasAliquots = getAliquotCollection() != null
            && !getAliquotCollection().isEmpty();
        boolean hasSourceVessels = getSourceVesselCollection() != null
            && !getSourceVesselCollection().isEmpty();

        return hasAliquots || hasSourceVessels;
    }

    public boolean canBeReceivedBy(User user, SiteWrapper site) {
        return canUpdate(user) && getReceiver().equals(site)
            && isInTransitState();
    }

    public DispatchAliquotWrapper getDispatchAliquot(String inventoryId) {
        for (DispatchAliquotWrapper dsa : getDispatchAliquotCollection(false)) {
            if (dsa.getAliquot().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    public void setState(DispatchState ds) {
        setState(ds.getId());
    }
}
