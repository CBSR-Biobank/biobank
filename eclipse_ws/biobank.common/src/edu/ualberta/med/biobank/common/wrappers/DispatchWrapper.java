package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * @see DispatchState
 */
public class DispatchWrapper extends AbstractShipmentWrapper<Dispatch> {

    private static final String NON_PROCESSED_ALIQUOTS_KEY = "nonProcessedDispatchAliquotCollection";

    private static final String RECEIVED_ALIQUOTS_KEY = "receivedDispatchAliquots";

    private static final String MISSING_ALIQUOTS_KEY = "missingDispatchAliquots";

    private static final String EXTRA_ALIQUOTS_KEY = "extraDispatchAliquots";

    private static final String ALL_ALIQUOTS_KEY = "aliquotCollection";

    private Set<DispatchAliquotWrapper> deletedDispatchedAliquots = new HashSet<DispatchAliquotWrapper>();

    private boolean stateModified = false;

    private Set<AliquotWrapper> modifiedAliquots = new HashSet<AliquotWrapper>();

    public DispatchWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchWrapper(WritableApplicationService appService, Dispatch ship) {
        super(appService, ship);
    }

    @Override
    public Class<Dispatch> getWrappedClass() {
        return Dispatch.class;
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return DispatchPeer.PROP_NAMES;
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
        if (getStudy() == null) {
            throw new BiobankCheckException("Study should be set");
        }
        if (!checkWaybillUniqueForSender()) {
            throw new BiobankCheckException("A dispatch with waybill "
                + getWaybill() + " already exists for sending site "
                + getSender().getNameShort());
        }
        if (isInTransitState() && getDeparted() == null) {
            throw new BiobankCheckException(
                "Departed should be set when this shipment is in transit.");
        }
        checkSenderCanSendToReceiver();
    }

    @Override
    protected void persistDependencies(Dispatch origObject) throws Exception {
        for (DispatchAliquotWrapper dsa : deletedDispatchedAliquots) {
            if (!dsa.isNew()) {
                dsa.delete();
            }
        }
        if (stateModified) {
            if (isInTransitState()) {
                // when is sent, need to set aliquots positions to null and to
                // remove containers holding them
                for (AliquotWrapper aliquot : getAliquotCollection()) {
                    if (aliquot.getPosition() != null) {
                        ContainerWrapper parent = aliquot.getParent();
                        aliquot.setPosition(null);
                        aliquot.persist();
                        parent.reload();
                        if (!parent.hasAliquots()) {
                            parent.delete();
                        }
                    }
                }
            } else if (isInLostState()) {
                // if lost, set all aliquot in close status
                for (AliquotWrapper aliquot : getAliquotCollection()) {
                    aliquot.setActivityStatus(ActivityStatusWrapper
                        .getActivityStatus(appService,
                            ActivityStatusWrapper.CLOSED_STATUS_STRING));
                    aliquot.persist();
                }
            }
        } else {
            for (AliquotWrapper aliquot : modifiedAliquots) {
                aliquot.persist();
            }
        }
    }

    private void checkSenderCanSendToReceiver() throws BiobankException {
        if (getSender() != null && getReceiver() != null && getStudy() != null) {
            List<SiteWrapper> possibleReceivers = getSender()
                .getStudyDispachSites(getStudy());
            if (possibleReceivers == null
                || !possibleReceivers.contains(getReceiver())) {
                throw new BiobankCheckException("site "
                    + getSender().getNameShort()
                    + " cannot dispatch aliquots to site "
                    + getReceiver().getNameShort() + " for study "
                    + getStudy().getNameShort());
            }
        }
    }

    private static final String WAYBILL_UNIQUE_FOR_SENDER_BASE_QRY = "from "
        + Dispatch.class.getName() + " where "
        + Property.concatNames(DispatchPeer.SENDER, SitePeer.ID) + "=? and "
        + DispatchPeer.WAYBILL.getName() + "=?";

    private boolean checkWaybillUniqueForSender() throws ApplicationException,
        BiobankCheckException {
        List<Object> params = new ArrayList<Object>();
        SiteWrapper sender = getSender();
        if (sender == null) {
            throw new BiobankCheckException("sender site cannot be null");
        }
        params.add(sender.getId());
        params.add(getWaybill());

        StringBuilder qry = new StringBuilder(
            WAYBILL_UNIQUE_FOR_SENDER_BASE_QRY);
        if (!isNew()) {
            qry.append(" and id <> ?");
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(qry.toString(), params);
        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    public SiteWrapper getSender() {
        return getWrappedProperty(DispatchPeer.SENDER, SiteWrapper.class);
    }

    public void setSender(SiteWrapper sender) {
        setWrappedProperty(DispatchPeer.SENDER, sender);
    }

    public SiteWrapper getReceiver() {
        return getWrappedProperty(DispatchPeer.RECEIVER, SiteWrapper.class);
    }

    public void setReceiver(SiteWrapper receiver) {
        setWrappedProperty(DispatchPeer.RECEIVER, receiver);
    }

    protected Integer getDispatchState() {
        return getProperty(DispatchPeer.STATE);
    }

    public String getStateDescription() {
        DispatchState state = DispatchState.getState(getDispatchState());
        if (state == null)
            return "";
        return state.getLabel();
    }

    public StudyWrapper getStudy() {
        return getWrappedProperty(DispatchPeer.STUDY, StudyWrapper.class);
    }

    public void setStudy(StudyWrapper study) {
        setWrappedProperty(DispatchPeer.STUDY, study);
    }

    public List<DispatchAliquotWrapper> getDispatchAliquotCollection(
        boolean sort) {
        return getWrapperCollection(DispatchPeer.DISPATCH_ALIQUOT_COLLECTION,
            DispatchAliquotWrapper.class, sort);
    }

    public List<DispatchAliquotWrapper> getDispatchAliquotCollection() {
        return getDispatchAliquotCollection(true);
    }

    @SuppressWarnings("unchecked")
    private List<DispatchAliquotWrapper> getDispatchAliquotCollectionWithState(
        String mapKey, boolean sort, DispatchAliquotState... states) {
        List<DispatchAliquotWrapper> dsaCollection = (List<DispatchAliquotWrapper>) propertiesMap
            .get(mapKey);
        if (dsaCollection == null) {
            Collection<DispatchAliquotWrapper> children = getDispatchAliquotCollection(sort);
            if (children != null) {
                dsaCollection = new ArrayList<DispatchAliquotWrapper>();
                for (DispatchAliquotWrapper dsa : children) {
                    boolean hasState = false;
                    for (DispatchAliquotState state : states) {
                        if (state.isEquals(dsa.getState())) {
                            hasState = true;
                            break;
                        }
                    }
                    if (hasState)
                        dsaCollection.add(dsa);
                }
                propertiesMap.put(mapKey, dsaCollection);
            }
        }
        if ((dsaCollection != null) && sort)
            Collections.sort(dsaCollection);
        return dsaCollection;
    }

    public List<DispatchAliquotWrapper> getNonProcessedDispatchAliquotCollection() {
        return getDispatchAliquotCollectionWithState(
            NON_PROCESSED_ALIQUOTS_KEY, true, DispatchAliquotState.NONE_STATE);
    }

    public List<DispatchAliquotWrapper> getReceivedDispatchAliquots() {
        return getDispatchAliquotCollectionWithState(RECEIVED_ALIQUOTS_KEY,
            true, DispatchAliquotState.RECEIVED_STATE);
    }

    @SuppressWarnings("unchecked")
    public List<AliquotWrapper> getAliquotCollection(boolean sort) {
        List<AliquotWrapper> aliquotCollection = (List<AliquotWrapper>) propertiesMap
            .get(ALL_ALIQUOTS_KEY);
        if (aliquotCollection == null) {
            Collection<DispatchAliquotWrapper> dsaList = getDispatchAliquotCollection(sort);
            if (dsaList != null) {
                aliquotCollection = new ArrayList<AliquotWrapper>();
                for (DispatchAliquotWrapper dsa : dsaList) {
                    aliquotCollection.add(dsa.getAliquot());
                }
                propertiesMap.put(ALL_ALIQUOTS_KEY, aliquotCollection);
            }
        }
        return aliquotCollection;
    }

    public List<AliquotWrapper> getAliquotCollection() {
        return getAliquotCollection(true);
    }

    public void addNewAliquots(List<AliquotWrapper> newAliquots,
        boolean checkAlreadyAdded) throws BiobankCheckException {
        addAliquots(newAliquots, DispatchAliquotState.NONE_STATE,
            checkAlreadyAdded);
    }

    public void addExtraAliquots(List<AliquotWrapper> newAliquots,
        boolean checkAlreadyAdded) throws BiobankCheckException {
        addAliquots(newAliquots, DispatchAliquotState.EXTRA, checkAlreadyAdded);
    }

    private void addAliquots(List<AliquotWrapper> newAliquots,
        DispatchAliquotState stateForAliquot, boolean checkAlreadyAdded)
        throws BiobankCheckException {
        if (newAliquots == null)
            return;

        // already added dsa
        List<DispatchAliquotWrapper> currentDaList = getDispatchAliquotCollection();
        List<DispatchAliquotWrapper> newDispatchAliquots = new ArrayList<DispatchAliquotWrapper>();
        List<AliquotWrapper> currentAliquotList = new ArrayList<AliquotWrapper>();

        for (DispatchAliquotWrapper dsa : currentDaList) {
            currentAliquotList.add(dsa.getAliquot());
        }

        // new aliquots added
        for (AliquotWrapper aliquot : newAliquots) {
            if (stateForAliquot != DispatchAliquotState.EXTRA) {
                CheckStatus check = checkCanAddAliquot(currentAliquotList,
                    aliquot, checkAlreadyAdded);
                if (!check.ok)
                    throw new BiobankCheckException(check.message);
            }
            if (!currentAliquotList.contains(aliquot)) {
                DispatchAliquotWrapper dsa = new DispatchAliquotWrapper(
                    appService);
                dsa.setAliquot(aliquot);
                dsa.setState(stateForAliquot.getId());
                if (stateForAliquot == DispatchAliquotState.EXTRA) {
                    aliquot.setPosition(null);
                    modifiedAliquots.add(aliquot);
                }
                dsa.setDispatch(this);
                newDispatchAliquots.add(dsa);
            }
        }

        addToWrapperCollection(DispatchPeer.DISPATCH_ALIQUOT_COLLECTION,
            newDispatchAliquots);
        resetStateLists();

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedDispatchedAliquots.removeAll(newDispatchAliquots);
    }

    public void removeDispatchAliquots(List<DispatchAliquotWrapper> dasToRemove) {
        if ((dasToRemove == null) || (dasToRemove.size() == 0))
            return;

        deletedDispatchedAliquots.addAll(dasToRemove);
        removeFromWrapperCollection(DispatchPeer.DISPATCH_ALIQUOT_COLLECTION,
            dasToRemove);
        resetStateLists();
    }

    public void removeAliquots(List<AliquotWrapper> aliquotsToRemove) {
        if ((aliquotsToRemove == null) || (aliquotsToRemove.size() == 0))
            return;

        List<DispatchAliquotWrapper> currentDaList = getDispatchAliquotCollection(false);
        List<DispatchAliquotWrapper> removeDispatchAliquots = new ArrayList<DispatchAliquotWrapper>();

        for (DispatchAliquotWrapper dsa : currentDaList) {
            if (aliquotsToRemove.contains(dsa.getAliquot())) {
                removeDispatchAliquots.add(dsa);
                deletedDispatchedAliquots.add(dsa);
            }
        }
        removeFromWrapperCollection(DispatchPeer.DISPATCH_ALIQUOT_COLLECTION,
            removeDispatchAliquots);
    }

    public static class CheckStatus {
        public CheckStatus(boolean b, String string) {
            this.ok = b;
            this.message = string;
        }

        public boolean ok = true;
        public String message;

    }

    public CheckStatus checkCanAddAliquot(AliquotWrapper aliquot,
        boolean checkAlreadyAdded) {
        return checkCanAddAliquot(getAliquotCollection(), aliquot,
            checkAlreadyAdded);
    }

    protected CheckStatus checkCanAddAliquot(
        List<AliquotWrapper> currentAliquots, AliquotWrapper aliquot,
        boolean checkAlreadyAdded) {
        if (aliquot.isNew()) {
            return new CheckStatus(false, "Cannot add aliquot "
                + aliquot.getInventoryId() + ": it has not already been saved");
        }
        if (!aliquot.isActive()) {
            return new CheckStatus(false, "Activity status of "
                + aliquot.getInventoryId() + " is not 'Active'."
                + " Check comments on this aliquot for more information.");
        }
        if (aliquot.getPosition() == null) {
            return new CheckStatus(false, "Cannot add aliquot "
                + aliquot.getInventoryId()
                + ": it has no position. A position should be first assigned.");
        }
        if (aliquot.getParent() != null
            && !aliquot.getParent().getSite().equals(getSender())) {
            return new CheckStatus(false, "Aliquot " + aliquot.getInventoryId()
                + " is currently assigned to site "
                + aliquot.getParent().getSite().getNameShort()
                + ". It should be first assigned to "
                + getSender().getNameShort() + " site.");
        }
        StudyWrapper aliquotStudy = aliquot.getPatientVisit().getPatient()
            .getStudy();
        if (!aliquotStudy.equals(getStudy())) {
            return new CheckStatus(false, "Aliquot " + aliquot.getInventoryId()
                + " is linked to study " + aliquotStudy.getNameShort()
                + ". The study of this shipment is "
                + ((getStudy() == null) ? "none" : getStudy().getNameShort())
                + ".");
        }
        if (checkAlreadyAdded && currentAliquots != null
            && currentAliquots.contains(aliquot)) {
            return new CheckStatus(false, aliquot.getInventoryId()
                + " is already in this shipment.");
        }
        if (aliquot.isUsedInDispatch()) {
            return new CheckStatus(false, aliquot.getInventoryId()
                + " is already in another shipment in transit or in creation.");
        }
        return new CheckStatus(true, "");
    }

    public void receiveAliquots(List<AliquotWrapper> aliquotsToReceive) {
        List<DispatchAliquotWrapper> nonProcessedAliquots = getNonProcessedDispatchAliquotCollection();
        for (DispatchAliquotWrapper dsa : nonProcessedAliquots) {
            if (aliquotsToReceive.contains(dsa.getAliquot())) {
                dsa.setState(DispatchAliquotState.RECEIVED_STATE.getId());
            }
        }
        propertiesMap.put(NON_PROCESSED_ALIQUOTS_KEY, null);
        propertiesMap.put(RECEIVED_ALIQUOTS_KEY, null);
    }

    public boolean isInCreationState() {
        return wrappedObject.getState() == null
            || wrappedObject.getState() == 0;
    }

    public boolean isInTransitState() {
        return wrappedObject.getState() != null
            && DispatchState.IN_TRANSIT.isEquals(wrappedObject.getState());
    }

    public boolean isInReceivedState() {
        return wrappedObject.getState() != null
            && DispatchState.RECEIVED.isEquals(wrappedObject.getState());
    }

    public boolean hasBeenReceived() {
        return wrappedObject.getState() != null
            && (DispatchState.RECEIVED.isEquals(wrappedObject.getState()) || DispatchState.CLOSED
                .isEquals(wrappedObject.getState()));
    }

    public boolean isInClosedState() {
        return wrappedObject.getState() != null
            && DispatchState.CLOSED.isEquals(wrappedObject.getState());
    }

    public boolean isInLostState() {
        return wrappedObject.getState() != null
            && DispatchState.LOST.isEquals(wrappedObject.getState());
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
    public static List<DispatchWrapper> getDispatchesInSite(
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
        + DispatchPeer.DEPARTED.getName() + " >=? and "
        + DispatchPeer.DEPARTED.getName() + " <= ?";

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
    public static List<DispatchWrapper> getDispatchesInSiteByDateReceived(
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
        sb.append(getFormattedDeparted() + "/");
        sb.append(getFormattedDateReceived());
        return sb.toString();
    }

    private void setState(DispatchState state) {
        setProperty(DispatchPeer.STATE, state.getId());
    }

    public void setInCreationState() {
        setState(DispatchState.CREATION);
    }

    public void setInTransitState() {
        setState(DispatchState.IN_TRANSIT);
    }

    public void setInLostState() {
        setState(DispatchState.LOST);
    }

    public void setInCloseState() {
        setState(DispatchState.CLOSED);
    }

    public void setInReceivedState() {
        setState(DispatchState.RECEIVED);
    }

    public boolean canBeSentBy(User user) throws Exception {
        return getSender() != null && canUpdate(user)
            && SiteWrapper.getSites(appService).contains(getSender())
            && isInCreationState() && hasAliquots();
    }

    public boolean hasAliquots() {
        return getAliquotCollection() != null
            && getAliquotCollection().size() > 0;
    }

    public boolean canBeReceivedBy(User user) throws Exception {
        return getReceiver() != null && canUpdate(user)
            && SiteWrapper.getSites(appService).contains(getReceiver())
            && isInTransitState() && user.canUpdateSite(getReceiver());
    }

    public boolean canBeClosedBy(User user) throws Exception {
        return getReceiver() != null && canUpdate(user)
            && SiteWrapper.getSites(appService).contains(getReceiver())
            && isInReceivedState() && !hasPendingAliquots()
            && user.canUpdateSite(getReceiver());
    }

    private boolean hasPendingAliquots() {
        List<DispatchAliquotWrapper> dsaList = getNonProcessedDispatchAliquotCollection();
        return dsaList == null ? false : dsaList.size() > 0;
    }

    public DispatchAliquotWrapper getDispatchAliquot(String inventoryId) {
        for (DispatchAliquotWrapper dsa : getDispatchAliquotCollection()) {
            if (dsa.getAliquot().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    @Override
    protected void resetInternalFields() {
        deletedDispatchedAliquots.clear();
        stateModified = false;
    }

    public List<DispatchAliquotWrapper> getExtraDispatchAliquots() {
        return getDispatchAliquotCollectionWithState(EXTRA_ALIQUOTS_KEY, true,
            DispatchAliquotState.EXTRA);
    }

    public List<DispatchAliquotWrapper> getMissingDispatchAliquots() {
        return getDispatchAliquotCollectionWithState(MISSING_ALIQUOTS_KEY,
            true, DispatchAliquotState.MISSING);
    }

    public void resetStateLists() {
        propertiesMap.put(MISSING_ALIQUOTS_KEY, null);
        propertiesMap.put(EXTRA_ALIQUOTS_KEY, null);
        propertiesMap.put(RECEIVED_ALIQUOTS_KEY, null);
        propertiesMap.put(NON_PROCESSED_ALIQUOTS_KEY, null);
        propertiesMap.put(ALL_ALIQUOTS_KEY, null);
    }

    public boolean hasErrors() {
        List<DispatchAliquotWrapper> extraList = getExtraDispatchAliquots();
        List<DispatchAliquotWrapper> missingList = getMissingDispatchAliquots();
        return (extraList != null && extraList.size() > 0)
            || (missingList != null && missingList.size() > 0);
    }
}
