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
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.util.DispatchShipmentState;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.model.DispatchShipmentAliquot;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * @see DispatchShipmentState
 */
public class DispatchShipmentWrapper extends
    AbstractShipmentWrapper<DispatchShipment> {

    private static final String NON_PROCESSED_ALIQUOTS_KEY = "nonProcessedDispatchShipmentAliquotCollection";

    private static final String RECEIVED_ALIQUOTS_KEY = "receivedDispatchShipmentAliquots";

    private static final String MISSING_ALIQUOTS_KEY = "missingDispatchShipmentAliquots";

    private static final String EXTRA_ALIQUOTS_KEY = "extraDispatchShipmentAliquots";

    private Set<DispatchShipmentAliquotWrapper> deletedDispatchedShipmentAliquots = new HashSet<DispatchShipmentAliquotWrapper>();

    private boolean stateModified = false;

    private Set<AliquotWrapper> modifiedAliquots = new HashSet<AliquotWrapper>();

    public DispatchShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchShipmentWrapper(WritableApplicationService appService,
        DispatchShipment ship) {
        super(appService, ship);
    }

    @Override
    public Class<DispatchShipment> getWrappedClass() {
        return DispatchShipment.class;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        String[] properties = super.getPropertyChangeNames();
        List<String> list = new ArrayList<String>(Arrays.asList(properties));
        list.addAll(Arrays.asList("sender", "receiver", "aliquotCollection",
            "study"));
        return list.toArray(new String[] {});
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
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
            throw new BiobankCheckException("A dispatch shipment with waybill "
                + getWaybill() + " already exists for sending site "
                + getSender().getNameShort());
        }
        if (isInTransitState() && getDateShipped() == null) {
            throw new BiobankCheckException(
                "Date shipped should be set when this shipment is in transit.");
        }
        checkSenderCanSendToReceiver();
    }

    @Override
    protected void persistDependencies(DispatchShipment origObject)
        throws Exception {
        for (DispatchShipmentAliquotWrapper dsa : deletedDispatchedShipmentAliquots) {
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

    private void checkSenderCanSendToReceiver() throws BiobankCheckException,
        WrapperException {
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

    private boolean checkWaybillUniqueForSender() throws ApplicationException,
        BiobankCheckException {
        String isSameShipment = "";
        List<Object> params = new ArrayList<Object>();
        SiteWrapper sender = getSender();
        if (sender == null) {
            throw new BiobankCheckException("sender site cannot be null");
        }
        params.add(sender.getId());
        params.add(getWaybill());
        if (!isNew()) {
            isSameShipment = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from "
            + DispatchShipment.class.getName()
            + " where sender.id=? and waybill = ?" + isSameShipment, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    public SiteWrapper getSender() {
        SiteWrapper sender = (SiteWrapper) propertiesMap.get("sender");
        if (sender == null) {
            Site s = wrappedObject.getSender();
            if (s == null)
                return null;
            sender = new SiteWrapper(appService, s);
            propertiesMap.put("sender", sender);
        }
        return sender;
    }

    public void setSender(SiteWrapper sender) {
        propertiesMap.put("sender", sender);
        Site oldSender = wrappedObject.getSender();
        Site newSender = null;
        if (sender != null) {
            newSender = sender.getWrappedObject();
        }
        wrappedObject.setSender(newSender);
        propertyChangeSupport
            .firePropertyChange("sender", oldSender, newSender);
    }

    public SiteWrapper getReceiver() {
        SiteWrapper receiver = (SiteWrapper) propertiesMap.get("receiver");
        if (receiver == null) {
            Site r = wrappedObject.getReceiver();
            if (r == null)
                return null;
            receiver = new SiteWrapper(appService, r);
            propertiesMap.put("receiver", receiver);
        }
        return receiver;
    }

    public void setReceiver(SiteWrapper receiver) {
        propertiesMap.put("receiver", receiver);
        Site oldReceiver = wrappedObject.getReceiver();
        Site newReceiver = null;
        if (receiver != null) {
            newReceiver = receiver.getWrappedObject();
        }
        wrappedObject.setReceiver(newReceiver);
        propertyChangeSupport.firePropertyChange("receiver", oldReceiver,
            newReceiver);
    }

    public StudyWrapper getStudy() {
        StudyWrapper study = (StudyWrapper) propertiesMap.get("study");
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
            propertiesMap.put("study", study);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        propertiesMap.put("study", study);
        Study oldStudy = wrappedObject.getStudy();
        Study newStudy = null;
        if (study != null) {
            newStudy = study.getWrappedObject();
        }
        wrappedObject.setStudy(newStudy);
        propertyChangeSupport.firePropertyChange("study", oldStudy, newStudy);
    }

    @SuppressWarnings("unchecked")
    public List<DispatchShipmentAliquotWrapper> getDispatchShipmentAliquotCollection(
        boolean sort) {
        List<DispatchShipmentAliquotWrapper> aliquotCollection = (List<DispatchShipmentAliquotWrapper>) propertiesMap
            .get("dispatchShipmentAliquotCollection");
        if (aliquotCollection == null) {
            Collection<DispatchShipmentAliquot> children = wrappedObject
                .getDispatchShipmentAliquotCollection();
            if (children != null) {
                aliquotCollection = new ArrayList<DispatchShipmentAliquotWrapper>();
                for (DispatchShipmentAliquot dsa : children) {
                    aliquotCollection.add(new DispatchShipmentAliquotWrapper(
                        appService, dsa));
                }
                propertiesMap.put("dispatchShipmentAliquotCollection",
                    aliquotCollection);
            }
        }
        if ((aliquotCollection != null) && sort)
            Collections.sort(aliquotCollection);
        return aliquotCollection;
    }

    public List<DispatchShipmentAliquotWrapper> getDispatchShipmentAliquotCollection() {
        return getDispatchShipmentAliquotCollection(true);
    }

    @SuppressWarnings("unchecked")
    private List<DispatchShipmentAliquotWrapper> getDispatchShipmentAliquotCollectionWithState(
        String mapKey, boolean sort, DispatchAliquotState... states) {
        List<DispatchShipmentAliquotWrapper> dsaCollection = (List<DispatchShipmentAliquotWrapper>) propertiesMap
            .get(mapKey);
        if (dsaCollection == null) {
            Collection<DispatchShipmentAliquotWrapper> children = getDispatchShipmentAliquotCollection(sort);
            if (children != null) {
                dsaCollection = new ArrayList<DispatchShipmentAliquotWrapper>();
                for (DispatchShipmentAliquotWrapper dsa : children) {
                    boolean hasState = false;
                    for (DispatchAliquotState state : states) {
                        if (dsa.getState() == state.ordinal()) {
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

    public List<DispatchShipmentAliquotWrapper> getNonProcessedDispatchShipmentAliquotCollection() {
        return getDispatchShipmentAliquotCollectionWithState(
            NON_PROCESSED_ALIQUOTS_KEY, true, DispatchAliquotState.NONE_STATE);
    }

    public List<DispatchShipmentAliquotWrapper> getReceivedDispatchShipmentAliquots() {
        return getDispatchShipmentAliquotCollectionWithState(
            RECEIVED_ALIQUOTS_KEY, true, DispatchAliquotState.RECEIVED_STATE);
    }

    @SuppressWarnings("unchecked")
    public List<AliquotWrapper> getAliquotCollection(boolean sort) {
        List<AliquotWrapper> aliquotCollection = (List<AliquotWrapper>) propertiesMap
            .get("aliquotCollection");
        if (aliquotCollection == null) {
            Collection<DispatchShipmentAliquotWrapper> dsaList = getDispatchShipmentAliquotCollection(sort);
            if (dsaList != null) {
                aliquotCollection = new ArrayList<AliquotWrapper>();
                for (DispatchShipmentAliquotWrapper dsa : dsaList) {
                    aliquotCollection.add(dsa.getAliquot());
                }
                propertiesMap.put("aliquotCollection", aliquotCollection);
            }
        }
        return aliquotCollection;
    }

    public List<AliquotWrapper> getAliquotCollection() {
        return getAliquotCollection(true);
    }

    private void setDispathcShipmentAliquotCollection(
        Collection<DispatchShipmentAliquot> allDsaObjects,
        List<DispatchShipmentAliquotWrapper> allDsaWrappers) {
        Collection<DispatchShipmentAliquot> oldList = wrappedObject
            .getDispatchShipmentAliquotCollection();
        wrappedObject.setDispatchShipmentAliquotCollection(allDsaObjects);
        propertyChangeSupport.firePropertyChange(
            "dispatchShipmentAliquotCollection", oldList, allDsaObjects);
        propertiesMap.put("dispatchShipmentAliquotCollection", allDsaWrappers);
    }

    public void addNewAliquots(List<AliquotWrapper> newAliquots)
        throws BiobankCheckException {
        addAliquots(newAliquots, DispatchAliquotState.NONE_STATE);
    }

    public void addExtraAliquots(List<AliquotWrapper> newAliquots)
        throws BiobankCheckException {
        addAliquots(newAliquots, DispatchAliquotState.EXTRA);
    }

    private void addAliquots(List<AliquotWrapper> newAliquots,
        DispatchAliquotState stateForAliquot) throws BiobankCheckException {
        if ((newAliquots == null) || (newAliquots.size() == 0))
            return;

        Collection<DispatchShipmentAliquot> allDsaObjects = new HashSet<DispatchShipmentAliquot>();
        List<DispatchShipmentAliquotWrapper> allDsaWrappers = new ArrayList<DispatchShipmentAliquotWrapper>();
        // already added dsa
        List<DispatchShipmentAliquotWrapper> currentList = getDispatchShipmentAliquotCollection();
        List<AliquotWrapper> currentAliquots = new ArrayList<AliquotWrapper>();
        if (currentList != null) {
            for (DispatchShipmentAliquotWrapper dsa : currentList) {
                allDsaObjects.add(dsa.getWrappedObject());
                allDsaWrappers.add(dsa);
                currentAliquots.add(dsa.getAliquot());
            }
        }
        // new aliquots added
        for (AliquotWrapper aliquot : newAliquots) {
            CheckStatus check = checkCanAddAliquot(currentAliquots, aliquot);
            if (!check.ok)
                throw new BiobankCheckException(check.message);
            // FIXME if state is "Extra". Should be able to add it anyway ?
            DispatchShipmentAliquotWrapper dsa = new DispatchShipmentAliquotWrapper(
                appService);
            dsa.setAliquot(aliquot);
            dsa.setState(stateForAliquot.ordinal());
            if (stateForAliquot == DispatchAliquotState.EXTRA) {
                aliquot.setPosition(null);
                modifiedAliquots.add(aliquot);
            }
            dsa.setShipment(this);
            allDsaObjects.add(dsa.getWrappedObject());
            allDsaWrappers.add(dsa);
        }
        setDispathcShipmentAliquotCollection(allDsaObjects, allDsaWrappers);
        resetStateLists();
    }

    public class CheckStatus {
        public CheckStatus(boolean b, String string) {
            this.ok = b;
            this.message = string;
        }

        public boolean ok = true;
        public String message;

    }

    public CheckStatus checkCanAddAliquot(List<AliquotWrapper> currentAliquots,
        AliquotWrapper aliquot) {
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
        if (!aliquot.getParent().getSite().equals(getSender())) {
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
        if (currentAliquots != null && currentAliquots.contains(aliquot)) {
            return new CheckStatus(false, aliquot.getInventoryId()
                + " is already in this shipment.");
        }
        if (aliquot.isUsedInDispatchShipment()) {
            return new CheckStatus(false, aliquot.getInventoryId()
                + " is already in a shipment in transit or in creation.");
        }
        return new CheckStatus(true, "");
    }

    public void removeDispatchShipmentAliquots(
        List<DispatchShipmentAliquotWrapper> dsasToRemove) {
        if ((dsasToRemove == null) || (dsasToRemove.size() == 0))
            return;

        Collection<DispatchShipmentAliquot> allDsaObjects = new HashSet<DispatchShipmentAliquot>();
        List<DispatchShipmentAliquotWrapper> allDsaWrappers = new ArrayList<DispatchShipmentAliquotWrapper>();
        // already added dsa
        List<DispatchShipmentAliquotWrapper> currentList = getDispatchShipmentAliquotCollection(false);
        if (currentList != null) {
            for (DispatchShipmentAliquotWrapper dsa : currentList) {
                if (!dsasToRemove.contains(dsa)) {
                    allDsaObjects.add(dsa.getWrappedObject());
                    allDsaWrappers.add(dsa);
                } else {
                    deletedDispatchedShipmentAliquots.add(dsa);
                }
            }
        }
        setDispathcShipmentAliquotCollection(allDsaObjects, allDsaWrappers);
        resetStateLists();
    }

    public void removeAliquots(List<AliquotWrapper> aliquotsToRemove) {
        if ((aliquotsToRemove == null) || (aliquotsToRemove.size() == 0))
            return;

        Collection<DispatchShipmentAliquot> allDsaObjects = new HashSet<DispatchShipmentAliquot>();
        List<DispatchShipmentAliquotWrapper> allDsaWrappers = new ArrayList<DispatchShipmentAliquotWrapper>();
        // already added dsa
        List<DispatchShipmentAliquotWrapper> currentList = getDispatchShipmentAliquotCollection(false);
        if (currentList != null) {
            for (DispatchShipmentAliquotWrapper dsa : currentList) {
                if (!aliquotsToRemove.contains(dsa.getAliquot())) {
                    allDsaObjects.add(dsa.getWrappedObject());
                    allDsaWrappers.add(dsa);
                } else {
                    deletedDispatchedShipmentAliquots.add(dsa);
                }
            }
        }
        setDispathcShipmentAliquotCollection(allDsaObjects, allDsaWrappers);
    }

    @Override
    protected void deleteChecks() throws Exception {

    }

    public void receiveAliquots(List<AliquotWrapper> aliquotsToReceive) {
        List<DispatchShipmentAliquotWrapper> nonProcessedAliquots = getNonProcessedDispatchShipmentAliquotCollection();
        for (DispatchShipmentAliquotWrapper dsa : nonProcessedAliquots) {
            if (aliquotsToReceive.contains(dsa.getAliquot())) {
                dsa.setState(DispatchAliquotState.RECEIVED_STATE.ordinal());
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
            && wrappedObject.getState() == DispatchShipmentState.IN_TRANSIT
                .ordinal();
    }

    public boolean isInReceivedState() {
        return wrappedObject.getState() != null
            && wrappedObject.getState() == DispatchShipmentState.RECEIVED
                .ordinal();
    }

    public boolean hasBeenReceived() {
        return wrappedObject.getState() != null
            && (wrappedObject.getState() == DispatchShipmentState.RECEIVED
                .ordinal() || wrappedObject.getState() == DispatchShipmentState.CLOSED
                .ordinal());
    }

    public boolean isInClosedState() {
        return wrappedObject.getState() != null
            && wrappedObject.getState() == DispatchShipmentState.CLOSED
                .ordinal();
    }

    public boolean isInLostState() {
        return wrappedObject.getState() != null
            && wrappedObject.getState() == DispatchShipmentState.LOST.ordinal();
    }

    /**
     * Search for shipments with the given waybill. Site can be the sender or
     * the receiver.
     */
    public static List<DispatchShipmentWrapper> getShipmentsInSite(
        WritableApplicationService appService, String waybill, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + DispatchShipment.class.getName()
            + " where (sender.id = ? or receiver.id = ?) and waybill = ?",
            Arrays.asList(new Object[] { site.getId(), site.getId(), waybill }));
        List<DispatchShipment> shipments = appService.query(criteria);
        List<DispatchShipmentWrapper> wrappers = new ArrayList<DispatchShipmentWrapper>();
        for (DispatchShipment s : shipments) {
            wrappers.add(new DispatchShipmentWrapper(appService, s));
        }
        return wrappers;
    }

    /**
     * Search for shipments with the given date sent. Don't use hour and minute.
     * Site can be the sender or the receiver.
     */
    public static List<DispatchShipmentWrapper> getShipmentsInSiteByDateSent(
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
            "from "
                + DispatchShipment.class.getName()
                + " where (sender.id = ? or receiver.id = ?) and dateShipped >= ? and dateShipped <= ?",
            Arrays.asList(new Object[] { site.getId(), site.getId(), startDate,
                endDate }));
        List<DispatchShipment> shipments = appService.query(criteria);
        List<DispatchShipmentWrapper> wrappers = new ArrayList<DispatchShipmentWrapper>();
        for (DispatchShipment s : shipments) {
            wrappers.add(new DispatchShipmentWrapper(appService, s));
        }
        return wrappers;
    }

    /**
     * Search for shipments with the given date received. Don't use hour and
     * minute. Site can be the sender or the receiver.
     */
    public static List<DispatchShipmentWrapper> getShipmentsInSiteByDateReceived(
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
            "from "
                + DispatchShipment.class.getName()
                + " where (sender.id = ? or receiver.id = ?) and dateReceived >= ? and dateReceived <= ?",
            Arrays.asList(new Object[] { site.getId(), site.getId(), startDate,
                endDate }));
        List<DispatchShipment> shipments = appService.query(criteria);
        List<DispatchShipmentWrapper> wrappers = new ArrayList<DispatchShipmentWrapper>();
        for (DispatchShipment s : shipments) {
            wrappers.add(new DispatchShipmentWrapper(appService, s));
        }
        return wrappers;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getSender() == null ? "" : getSender().getNameShort() + "/");
        sb.append(getReceiver() == null ? "" : getReceiver().getNameShort()
            + "/");
        sb.append(getFormattedDateShipped() + "/");
        sb.append(getFormattedDateReceived());
        return sb.toString();
    }

    private void setState(DispatchShipmentState state) {
        Integer oldState = wrappedObject.getState();
        Integer newState = state.ordinal();
        wrappedObject.setState(newState);
        stateModified = oldState == null || state == null
            || !oldState.equals(newState);
    }

    public void setInTransitState() {
        setState(DispatchShipmentState.IN_TRANSIT);
    }

    public void setInLostState() {
        setState(DispatchShipmentState.LOST);
    }

    public void setInCloseState() {
        setState(DispatchShipmentState.CLOSED);
    }

    public void setInReceivedState() {
        setState(DispatchShipmentState.RECEIVED);
    }

    public boolean canBeSentBy(User user, SiteWrapper site) {
        return canUpdate(user) && getSender().equals(site)
            && isInCreationState() && hasAliquots();
    }

    public boolean hasAliquots() {
        return getAliquotCollection() != null
            && getAliquotCollection().size() > 0;
    }

    public boolean canBeReceivedBy(User user, SiteWrapper site) {
        return canUpdate(user) && getReceiver().equals(site)
            && isInTransitState();
    }

    public boolean canBeClosedBy(User user, SiteWrapper site) {
        return canUpdate(user) && getReceiver().equals(site)
            && isInReceivedState() && !hasPendingAliquots();
    }

    private boolean hasPendingAliquots() {
        List<DispatchShipmentAliquotWrapper> dsaList = getNonProcessedDispatchShipmentAliquotCollection();
        return dsaList == null ? false : dsaList.size() > 0;
    }

    public DispatchShipmentAliquotWrapper getDispatchShipmentAliquot(
        String inventoryId) {
        for (DispatchShipmentAliquotWrapper dsa : getDispatchShipmentAliquotCollection()) {
            if (dsa.getAliquot().getInventoryId().equals(inventoryId))
                return dsa;
        }
        return null;
    }

    @Override
    protected void resetInternalFields() {
        deletedDispatchedShipmentAliquots.clear();
        stateModified = false;
    }

    public List<DispatchShipmentAliquotWrapper> getExtraDispatchShipmentAliquots() {
        return getDispatchShipmentAliquotCollectionWithState(
            EXTRA_ALIQUOTS_KEY, true, DispatchAliquotState.EXTRA);
    }

    public List<DispatchShipmentAliquotWrapper> getMissingDispatchShipmentAliquots() {
        return getDispatchShipmentAliquotCollectionWithState(
            MISSING_ALIQUOTS_KEY, true, DispatchAliquotState.MISSING);
    }

    public void resetStateLists() {
        propertiesMap.put(MISSING_ALIQUOTS_KEY, null);
        propertiesMap.put(EXTRA_ALIQUOTS_KEY, null);
        propertiesMap.put(RECEIVED_ALIQUOTS_KEY, null);
        propertiesMap.put(NON_PROCESSED_ALIQUOTS_KEY, null);
    }

    public boolean hasErrors() {
        List<DispatchShipmentAliquotWrapper> extraList = getExtraDispatchShipmentAliquots();
        List<DispatchShipmentAliquotWrapper> missingList = getMissingDispatchShipmentAliquots();
        return (extraList != null && extraList.size() > 0)
            || (missingList != null && missingList.size() > 0);
    }
}
