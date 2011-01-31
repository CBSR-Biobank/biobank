package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.AliquotPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AliquotPositionWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.DispatchAliquot;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotWrapper extends ModelWrapper<Aliquot> {

    private AbstractObjectWithPositionManagement<AliquotPosition, AliquotWrapper> objectWithPositionManagement;

    public AliquotWrapper(WritableApplicationService appService,
        Aliquot wrappedObject) {
        super(appService, wrappedObject);
        initManagement();
    }

    public AliquotWrapper(WritableApplicationService appService) {
        super(appService);
        initManagement();
    }

    private void initManagement() {
        objectWithPositionManagement = new AbstractObjectWithPositionManagement<AliquotPosition, AliquotWrapper>(
            this) {

            @Override
            protected AbstractPositionWrapper<AliquotPosition> getSpecificPositionWrapper(
                boolean initIfNoPosition) {
                if (nullPositionSet)
                    return null;

                AliquotPosition pos = wrappedObject.getAliquotPosition();
                if (pos != null) {
                    return new AliquotPositionWrapper(appService, pos);
                } else if (initIfNoPosition) {
                    AliquotPositionWrapper posWrapper = new AliquotPositionWrapper(
                        appService);
                    posWrapper.setAliquot(AliquotWrapper.this);
                    wrappedObject.setAliquotPosition(posWrapper
                        .getWrappedObject());
                    return posWrapper;
                }
                return null;
            }
        };
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return AliquotPeer.PROP_NAMES;
    }

    @Override
    public Class<Aliquot> getWrappedClass() {
        return Aliquot.class;
    }

    @Override
    public void persist() throws Exception {
        // check if position was deleted
        if (getPosition() == null) {
            // get original position
            AliquotPosition rawPos = wrappedObject.getAliquotPosition();
            if (rawPos != null) {
                AbstractPositionWrapper<AliquotPosition> pos = new AliquotPositionWrapper(
                    appService, rawPos);
                if (!pos.isNew()) {
                    pos.delete();
                }
            }
            wrappedObject.setAliquotPosition(null);
        }
        objectWithPositionManagement.persist();
        super.persist();
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        checkParentAcceptSampleType();
        objectWithPositionManagement.persistChecks();
    }

    public String getInventoryId() {
        return wrappedObject.getInventoryId();
    }

    public void setInventoryId(String inventoryId) {
        String oldInventoryId = inventoryId;
        wrappedObject.setInventoryId(inventoryId);
        propertyChangeSupport.firePropertyChange("inventoryId", oldInventoryId,
            inventoryId);
    }

    public void checkInventoryIdUnique() throws BiobankCheckException,
        ApplicationException {
        checkNoDuplicates(Aliquot.class, AliquotPeer.INVENTORY_ID.getName(),
            getInventoryId(), AliquotPeer.INVENTORY_ID.getName(), true);
    }

    private void checkParentAcceptSampleType() throws BiobankCheckException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            ContainerTypeWrapper parentType = getParent().getContainerType();
            try {
                parentType.reload();
            } catch (Exception e) {
                throw new BiobankCheckException(e);
            }
            List<SampleTypeWrapper> types = parentType
                .getSampleTypeCollection();
            if (types == null || !types.contains(getSampleType())) {
                throw new BiobankCheckException("Container "
                    + getParent().getFullInfoLabel()
                    + " does not allow inserts of sample type "
                    + ((getSampleType() == null) ? "null" : getSampleType()
                        .getName()) + ".");
            }
        }
    }

    public String getSiteString() {
        SiteWrapper site = getLocation();
        if (site != null) {
            return site.getNameShort();
        }
        // FIXME should never see that ? should never retrieve an aliquot which
        // site cannot be displayed ?
        return "CANNOT DISPLAY INFORMATION";
    }

    private SiteWrapper getLocation() {
        List<DispatchAliquotWrapper> dsac = this.getDispatchAliquotCollection();
        // if in a container, use the container's site
        if (getParent() != null) {
            return getParent().getSite();
        } else {
            // dispatched aliquot?
            for (DispatchAliquotWrapper da : dsac) {
                DispatchAliquotState state = DispatchAliquotState.getState(da
                    .getState());
                if (da.getDispatch().isInTransitState()
                    && DispatchAliquotState.NONE_STATE == state) {
                    // aliquot is in transit
                    // FIXME what if can't read sender or receiver
                    SiteWrapper fakeSite = new SiteWrapper(appService);
                    fakeSite.setNameShort("In Transit ("
                        + da.getDispatch().getSender().getNameShort() + " to "
                        + da.getDispatch().getReceiver().getNameShort() + ")");
                    return fakeSite;
                } else if (da.getDispatch().isInReceivedState()) {
                    switch (state) {
                    case EXTRA:
                        // aliquot has been accidentally dispatched
                        return da.getDispatch().getReceiver();
                    case MISSING:
                        // aliquot is missing
                        return da.getDispatch().getSender();
                    case RECEIVED_STATE:
                    case NONE_STATE:
                        // aliquot has been intentionally dispatched and
                        // received
                        return da.getDispatch().getReceiver();
                    }
                }
            }
            // if not in a container or a dispatch, use the originating shipment
            return getPatientVisit().getShipment().getSite();
        }
    }

    public void setPatientVisit(PatientVisitWrapper patientVisit) {
        propertiesMap.put("patientVisit", patientVisit);
        PatientVisit oldPvRaw = wrappedObject.getPatientVisit();
        PatientVisit newPvRaw = null;
        if (patientVisit != null) {
            newPvRaw = patientVisit.getWrappedObject();
        }
        wrappedObject.setPatientVisit(newPvRaw);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPvRaw,
            newPvRaw);
    }

    public PatientVisitWrapper getPatientVisit() {
        PatientVisitWrapper patientVisit = (PatientVisitWrapper) propertiesMap
            .get("patientVisit");
        if (patientVisit == null) {
            PatientVisit pv = wrappedObject.getPatientVisit();
            if (pv == null)
                return null;
            patientVisit = new PatientVisitWrapper(appService, pv);
            propertiesMap.put("patientVisit", patientVisit);
        }
        return patientVisit;
    }

    /**
     * Set the position in the given container using the positionString
     */
    public void setAliquotPositionFromString(String positionString,
        ContainerWrapper parentContainer) throws Exception {
        RowColPos rcp = parentContainer.getContainerType()
            .getRowColFromPositionString(
                positionString.replaceFirst(parentContainer.getLabel(), ""));
        if ((rcp.row > -1) && (rcp.col > -1)) {
            setPosition(rcp);
        } else {
            throw new Exception("Position " + positionString + " not valid");
        }
    }

    /**
     * Method used to check if the current position of this aliquot is available
     * on the container. Return true if the position is free, false otherwise
     */
    public boolean isPositionFree(ContainerWrapper parentContainer)
        throws ApplicationException {
        RowColPos position = getPosition();
        if (position != null) {
            HQLCriteria criteria = new HQLCriteria("from "
                + Aliquot.class.getName()
                + " where aliquotPosition.row=? and aliquotPosition.col=?"
                + " and aliquotPosition.container=?",
                Arrays.asList(new Object[] { position.row, position.col,
                    parentContainer.getWrappedObject() }));

            List<Aliquot> samples = appService.query(criteria);
            if (samples.size() > 0) {
                return false;
            }
        }
        return true;
    }

    public void setSampleType(SampleTypeWrapper type) {
        propertiesMap.put("sampleType", type);
        SampleType oldTypeRaw = wrappedObject.getSampleType();
        SampleType newTypeRaw = null;
        if (type != null) {
            newTypeRaw = type.getWrappedObject();
        }
        wrappedObject.setSampleType(newTypeRaw);
        propertyChangeSupport.firePropertyChange("sampleType", oldTypeRaw,
            newTypeRaw);
    }

    public SampleTypeWrapper getSampleType() {
        SampleTypeWrapper sampleType = (SampleTypeWrapper) propertiesMap
            .get("sampleType");
        if (sampleType == null) {
            SampleType s = wrappedObject.getSampleType();
            if (s == null)
                return null;
            sampleType = new SampleTypeWrapper(appService, s);
            propertiesMap.put("sampleType", sampleType);
        }
        return sampleType;
    }

    public void setLinkDate(Date date) {
        Date oldDate = getLinkDate();
        wrappedObject.setLinkDate(date);
        propertyChangeSupport.firePropertyChange("linkDate", oldDate, date);
    }

    public Date getLinkDate() {
        return wrappedObject.getLinkDate();
    }

    public String getFormattedLinkDate() {
        return DateFormatter.formatAsDateTime(wrappedObject.getLinkDate());
    }

    public void setQuantity(Double quantity) {
        Double oldQuantity = wrappedObject.getQuantity();
        wrappedObject.setQuantity(quantity);
        propertyChangeSupport.firePropertyChange("quantity", oldQuantity,
            quantity);
    }

    public Double getQuantity() {
        return wrappedObject.getQuantity();
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatusWrapper activity = (ActivityStatusWrapper) propertiesMap
            .get("activityStatus");
        if (activity == null) {
            ActivityStatus a = wrappedObject.getActivityStatus();
            if (a == null)
                return null;
            activity = new ActivityStatusWrapper(appService, a);
            propertiesMap.put("activityStatus", activity);
        }
        return activity;
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        propertiesMap.put("activityStatus", activityStatus);
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        ActivityStatus rawObject = null;
        if (activityStatus != null) {
            rawObject = activityStatus.getWrappedObject();
        }
        wrappedObject.setActivityStatus(rawObject);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
    }

    public void setComment(String comment) {
        String oldComment = wrappedObject.getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public ContainerWrapper getParent() {
        return objectWithPositionManagement.getParent();
    }

    public void setParent(ContainerWrapper container) {
        objectWithPositionManagement.setParent(container);
    }

    public boolean hasParent() {
        return objectWithPositionManagement.hasParent();
    }

    public RowColPos getPosition() {
        return objectWithPositionManagement.getPosition();
    }

    public void setPosition(RowColPos rcp) {
        if (rcp == null) {
            setParent(null);
        }
        objectWithPositionManagement.setPosition(rcp);
    }

    public String getPositionString() {
        return getPositionString(true, true);
    }

    public String getPositionString(boolean fullString,
        boolean addTopParentShortName) {
        RowColPos position = getPosition();
        if (position == null) {
            return null;
        }

        if (!fullString) {
            return getPositionStringInParent(position, getParent());
        }
        ContainerWrapper directParent = getParent();
        ContainerWrapper topContainer = directParent;
        while (topContainer.hasParent()) {
            topContainer = topContainer.getParent();
        }
        String nameShort = topContainer.getContainerType().getNameShort();
        if (addTopParentShortName && nameShort != null)
            return directParent.getLabel()
                + getPositionStringInParent(position, directParent) + " ("
                + nameShort + ")";
        return directParent.getLabel()
            + getPositionStringInParent(position, directParent);
    }

    private String getPositionStringInParent(RowColPos position,
        ContainerWrapper parent) {
        if (parent != null) {
            return parent.getContainerType().getPositionString(position);
        }
        return null;
    }

    public void setQuantityFromType() {
        if (getSampleType() != null) {
            PatientVisitWrapper patientVisit = (PatientVisitWrapper) propertiesMap
                .get("patientVisit");
            StudyWrapper study = patientVisit.getPatient().getStudy();
            Double volume = null;
            Collection<SampleStorageWrapper> sampleStorageCollection = study
                .getSampleStorageCollection();
            if (sampleStorageCollection != null) {
                for (SampleStorageWrapper ss : sampleStorageCollection) {
                    if (getSampleType().equals(getSampleType())) {
                        volume = ss.getVolume();
                    }
                }
            }
            setQuantity(volume);
        }
    }

    @Override
    public void loadAttributes() throws Exception {
        super.loadAttributes();
        getPositionString();
        wrappedObject.getSampleType().getName();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    /**
     * search in all aliquots list. No matter which site added it.
     */
    protected static AliquotWrapper getAliquot(
        WritableApplicationService appService, String inventoryId)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Aliquot.class.getName() + " where inventoryId = ?",
            Arrays.asList(new Object[] { inventoryId }));
        List<Aliquot> aliquots = appService.query(criteria);
        if (aliquots == null || aliquots.size() == 0)
            return null;
        if (aliquots.size() == 1)
            return new AliquotWrapper(appService, aliquots.get(0));
        throw new BiobankCheckException("Error retrieving aliquots: found "
            + aliquots.size() + " results.");
    }

    /**
     * search in all aliquots list. No matter which site added it. If user is
     * not null, will return only aliquot that is linked to a visit which site
     * can be read by the user
     * 
     * @throws BiobankCheckException
     */
    public static AliquotWrapper getAliquot(
        WritableApplicationService appService, String inventoryId, User user)
        throws ApplicationException, BiobankCheckException {
        AliquotWrapper aliquot = getAliquot(appService, inventoryId);
        if (aliquot != null && user != null) {
            SiteWrapper site = aliquot.getLocation();
            // site might be null if can't access it !
            if (site == null) {
                throw new ApplicationException(
                    "Aliquot "
                        + inventoryId
                        + " exists but you don't have access to it."
                        + " Its current site location should be a site you can access.");
            }
        }
        return aliquot;
    }

    // FIXME : do we want this search to be specific to a site ?
    public static List<AliquotWrapper> getAliquotsNonActive(
        WritableApplicationService appService, SiteWrapper site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "from "
                + Aliquot.class.getName()
                + " a where a.patientVisit.shipmentPatient.shipment.site.id = ? and activityStatus.name != ?",
            Arrays.asList(new Object[] { site.getId(),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING }));
        List<Aliquot> aliquots = appService.query(criteria);
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();

        for (Aliquot aliquot : aliquots) {
            list.add(new AliquotWrapper(appService, aliquot));
        }
        return list;
    }

    public static List<AliquotWrapper> getAliquotsInSiteWithPositionLabel(
        WritableApplicationService appService, SiteWrapper site,
        String positionString) throws ApplicationException,
        BiobankCheckException {
        List<ContainerWrapper> possibleContainers = ContainerWrapper
            .getPossibleParents(appService, positionString, site, null);
        List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
        for (ContainerWrapper container : possibleContainers) {
            RowColPos rcp = null;
            try {
                rcp = container.getContainerType().getRowColFromPositionString(
                    positionString.replaceFirst(container.getLabel(), ""));
            } catch (Exception e) {
                // Should never happen: it has been already tested in
                // getPossibleParentsMethod
                assert false;
            }
            if (rcp != null) {
                if ((rcp.row > -1) && (rcp.col > -1)) {
                    AliquotWrapper aliquot = container.getAliquot(rcp.row,
                        rcp.col);
                    if (aliquot != null) {
                        aliquots.add(aliquot);
                    }
                }
            }
        }
        return aliquots;
    }

    @Override
    public int compareTo(ModelWrapper<Aliquot> o) {
        if (o instanceof AliquotWrapper) {
            return getInventoryId().compareTo(
                ((AliquotWrapper) o).getInventoryId());
        }
        return 0;
    }

    @Override
    public String toString() {
        return getInventoryId();
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getDispatchs() {
        List<DispatchWrapper> dispatchs = (List<DispatchWrapper>) propertiesMap
            .get("dispatchs");
        if (dispatchs == null) {
            List<DispatchAliquotWrapper> dsaList = getDispatchAliquotCollection();
            if (dsaList != null) {
                dispatchs = new ArrayList<DispatchWrapper>();
                for (DispatchAliquotWrapper dsa : dsaList) {
                    dispatchs.add(dsa.getDispatch());
                }
                propertiesMap.put("dispatchs", dispatchs);
            }
        }
        return dispatchs;
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        PatientVisitWrapper visit = getPatientVisit();
        log.setAction(action);
        if (site == null) {
            log.setSite(visit.getShipment().getSite().getNameShort());
        } else {
            log.setSite(site);
        }
        log.setPatientNumber(visit.getPatient().getPnumber());
        log.setInventoryId(getInventoryId());
        log.setLocationLabel(getPositionString(true, true));
        log.setDetails(details);
        log.setType("Aliquot");
        return log;
    }

    public boolean isActive() {
        ActivityStatusWrapper status = getActivityStatus();
        return status != null && status.isActive();
    }

    public boolean isFlagged() {
        ActivityStatusWrapper status = getActivityStatus();
        return status != null && status.isFlagged();
    }

    public ContainerWrapper getTop() {
        return objectWithPositionManagement.getTop();
    }

    @SuppressWarnings("unchecked")
    public List<DispatchAliquotWrapper> getDispatchAliquotCollection() {
        List<DispatchAliquotWrapper> dsaCollection = (List<DispatchAliquotWrapper>) propertiesMap
            .get("dispatchAliquotCollection");
        if (dsaCollection == null) {
            Collection<DispatchAliquot> children = wrappedObject
                .getDispatchAliquotCollection();
            if (children != null) {
                dsaCollection = new ArrayList<DispatchAliquotWrapper>();
                for (DispatchAliquot dsa : children) {
                    dsaCollection.add(new DispatchAliquotWrapper(appService,
                        dsa));
                }
                propertiesMap.put("dispatchAliquotCollection", dsaCollection);
            }
        }
        return dsaCollection;
    }

    public boolean isUsedInDispatch() {
        return isUsedInDispatch(null);
    }

    public boolean isUsedInDispatch(DispatchWrapper excludedShipment) {
        List<DispatchAliquotWrapper> dsas = getDispatchAliquotCollection();
        if (dsas != null)
            for (DispatchAliquotWrapper dsa : dsas) {
                DispatchWrapper ship = dsa.getDispatch();
                if (!ship.equals(excludedShipment)
                    && (ship.isInTransitState() || ship.isInCreationState())) {
                    if (DispatchAliquotState.MISSING.isEquals(dsa.getState())) {
                        return false;
                    }
                    return true;
                }
            }
        return false;
    }

    @Override
    protected void resetInternalFields() {
        objectWithPositionManagement.resetInternalFields();
    }
}
