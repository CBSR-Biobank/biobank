package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenWrapper extends SpecimenBaseWrapper {

    private static final String DISPATCHS_CACHE_KEY = "dispatchs";
    private AbstractObjectWithPositionManagement<SpecimenPosition, SpecimenWrapper> objectWithPositionManagement;

    public SpecimenWrapper(WritableApplicationService appService,
        Specimen wrappedObject) {
        super(appService, wrappedObject);
        init();
    }

    public SpecimenWrapper(WritableApplicationService appService) {
        super(appService);
        init();
    }

    private void init() {
        if (isNew()) {
            setTopSpecimenInternal(this);
        }
        initManagement();
    }

    private void initManagement() {
        objectWithPositionManagement = new AbstractObjectWithPositionManagement<SpecimenPosition, SpecimenWrapper>(
            this) {

            @Override
            protected AbstractPositionWrapper<SpecimenPosition> getSpecificPositionWrapper(
                boolean initIfNoPosition) {
                if (nullPositionSet)
                    return null;

                SpecimenPosition pos = wrappedObject.getSpecimenPosition();
                if (pos != null) {
                    return new SpecimenPositionWrapper(appService, pos);
                } else if (initIfNoPosition) {
                    SpecimenPositionWrapper posWrapper = new SpecimenPositionWrapper(
                        appService);
                    posWrapper.setSpecimen(SpecimenWrapper.this);
                    wrappedObject.setSpecimenPosition(posWrapper
                        .getWrappedObject());
                    return posWrapper;
                }
                return null;
            }
        };
    }

    @Override
    public void persist() throws Exception {
        // check if position was deleted
        if (getPosition() == null) {
            // get original position
            SpecimenPosition rawPos = wrappedObject.getSpecimenPosition();
            if (rawPos != null) {
                AbstractPositionWrapper<SpecimenPosition> pos = new SpecimenPositionWrapper(
                    appService, rawPos);
                if (!pos.isNew()) {
                    pos.delete();
                }
            }
            wrappedObject.setSpecimenPosition(null);
        }
        objectWithPositionManagement.persist();
        super.persist();
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkInventoryIdUnique();
        checkParentAcceptSampleType();
        objectWithPositionManagement.persistChecks();
    }

    public void checkInventoryIdUnique() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(Specimen.class, SpecimenPeer.INVENTORY_ID.getName(),
            getInventoryId(), "A specimen with inventoryId");
    }

    public String getFormattedCreatedAt() {
        return DateFormatter.formatAsDateTime(getCreatedAt());
    }

    public ContainerWrapper getParentContainer() {
        return objectWithPositionManagement.getParentContainer();
    }

    public void setParent(ContainerWrapper container) {
        objectWithPositionManagement.setParent(container);
    }

    public boolean hasParent() {
        return objectWithPositionManagement.hasParentContainer();
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

    private void checkParentAcceptSampleType() throws BiobankCheckException {
        ContainerWrapper parent = getParentContainer();
        if (parent != null) {
            ContainerTypeWrapper parentType = getParentContainer()
                .getContainerType();
            try {
                parentType.reload();
            } catch (Exception e) {
                throw new BiobankCheckException(e);
            }
            List<SpecimenTypeWrapper> types = parentType
                .getSpecimenTypeCollection(false);
            if (types == null || !types.contains(getSpecimenType())) {
                throw new BiobankCheckException("Container "
                    + getParentContainer().getFullInfoLabel()
                    + " does not allow inserts of sample type "
                    + ((getSpecimenType() == null) ? "null" : getSpecimenType()
                        .getName()) + ".");
            }
        }
    }

    public String getCenterString() {
        CenterWrapper<?> center = getCurrentCenter();
        if (center != null) {
            return center.getNameShort();
        }
        // FIXME should never see that ? should never retrieve a Specimen which
        // site cannot be displayed ?
        return "CANNOT DISPLAY INFORMATION";
    }

    /**
     * Set the position in the given container using the positionString
     */
    public void setSpecimenPositionFromString(String positionString,
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

    private static final String POSITION_FREE_QRY = "from "
        + Specimen.class.getName()
        + " where "
        + Property.concatNames(SpecimenPeer.SPECIMEN_POSITION,
            SpecimenPositionPeer.ROW)
        + "=? and "
        + Property.concatNames(SpecimenPeer.SPECIMEN_POSITION,
            SpecimenPositionPeer.COL)
        + "=? and "
        + Property.concatNames(SpecimenPeer.SPECIMEN_POSITION,
            SpecimenPositionPeer.CONTAINER) + "=?";

    /**
     * Method used to check if the current position of this Specimen is
     * available on the container. Return true if the position is free, false
     * otherwise
     */
    public boolean isPositionFree(ContainerWrapper parentContainer)
        throws ApplicationException {
        RowColPos position = getPosition();
        if (position != null) {
            HQLCriteria criteria = new HQLCriteria(POSITION_FREE_QRY,
                Arrays.asList(new Object[] { position.row, position.col,
                    parentContainer.getWrappedObject() }));

            List<Specimen> samples = appService.query(criteria);
            if (samples.size() > 0) {
                return false;
            }
        }
        return true;
    }

    public String getPositionString(boolean fullString,
        boolean addTopParentShortName) {
        RowColPos position = getPosition();
        if (position == null) {
            return null;
        }

        if (!fullString) {
            return getPositionStringInParent(position, getParentContainer());
        }
        ContainerWrapper directParent = getParentContainer();
        ContainerPathWrapper path = directParent.getContainerPath();
        ContainerWrapper topContainer;

        if (path != null) {
            topContainer = path.getTopContainer();
        } else {
            topContainer = directParent;
            while (topContainer.hasParentContainer()) {
                topContainer = topContainer.getParentContainer();
            }
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
        if (getSpecimenType() == null)
            return;
        CollectionEventWrapper cevent = getCollectionEvent();
        StudyWrapper study = cevent.getPatient().getStudy();
        Collection<AliquotedSpecimenWrapper> aliquotedSpecimenCollection = study
            .getAliquotedSpecimenCollection(false);
        if (aliquotedSpecimenCollection != null) {
            for (AliquotedSpecimenWrapper ss : aliquotedSpecimenCollection) {
                if (getSpecimenType().equals(ss.getSpecimenType())) {
                    setQuantity(ss.getVolume());
                    return;
                }
            }
        }
    }

    @Override
    public void loadAttributes() throws Exception {
        super.loadAttributes();
        getPositionString();
        wrappedObject.getSpecimenType().getName();
    }

    private static final String Specimen_QRY = "from "
        + Specimen.class.getName() + " where "
        + SpecimenPeer.INVENTORY_ID.getName() + " = ?";

    /**
     * search in all Specimens list. No matter which site added it.
     */
    protected static SpecimenWrapper getSpecimen(
        WritableApplicationService appService, String inventoryId)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria(Specimen_QRY,
            Arrays.asList(new Object[] { inventoryId }));
        List<Specimen> specimens = appService.query(criteria);
        if (specimens == null || specimens.size() == 0)
            return null;
        if (specimens.size() == 1)
            return new SpecimenWrapper(appService, specimens.get(0));
        throw new BiobankCheckException("Error retrieving specimens: found "
            + specimens.size() + " results.");
    }

    /**
     * search in all Specimens list. No matter which site added it. If user is
     * not null, will return only Specimen that is linked to a visit which site
     * can be read by the user
     * 
     * @throws BiobankCheckException
     */
    public static SpecimenWrapper getSpecimen(
        WritableApplicationService appService, String inventoryId, User user)
        throws ApplicationException, BiobankCheckException {
        SpecimenWrapper specimen = getSpecimen(appService, inventoryId);
        if (specimen != null && user != null) {
            CenterWrapper<?> center = specimen.getCurrentCenter();
            // site might be null if can't access it !
            if (center == null) {
                throw new ApplicationException(
                    "Specimen "
                        + inventoryId
                        + " exists but you don't have access to it."
                        + " Its current site location should be a site you can access.");
            }
        }
        return specimen;
    }

    private static final String SPECIMENS_NON_ACTIVE_QRY = "from "
        + Specimen.class.getName()
        + " spec where spec."
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + " = ? and "
        + Property.concatNames(SpecimenPeer.ACTIVITY_STATUS,
            ActivityStatusPeer.NAME) + " != ?";

    public static List<SpecimenWrapper> getSpecimensNonActiveInCenter(
        WritableApplicationService appService, CenterWrapper<?> center)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(SPECIMENS_NON_ACTIVE_QRY,
            Arrays.asList(new Object[] { center.getId(),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING }));
        List<Specimen> Specimens = appService.query(criteria);
        List<SpecimenWrapper> list = new ArrayList<SpecimenWrapper>();

        for (Specimen Specimen : Specimens) {
            list.add(new SpecimenWrapper(appService, Specimen));
        }
        return list;
    }

    public static List<SpecimenWrapper> getSpecimensInSiteWithPositionLabel(
        WritableApplicationService appService, SiteWrapper site,
        String positionString) throws ApplicationException, BiobankException {
        List<ContainerWrapper> possibleContainers = ContainerWrapper
            .getPossibleParents(appService, positionString, site, null);
        List<SpecimenWrapper> Specimens = new ArrayList<SpecimenWrapper>();
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
                    SpecimenWrapper Specimen = container.getSpecimen(rcp.row,
                        rcp.col);
                    if (Specimen != null) {
                        Specimens.add(Specimen);
                    }
                }
            }
        }
        return Specimens;
    }

    @Override
    public int compareTo(ModelWrapper<Specimen> o) {
        if (o instanceof SpecimenWrapper) {
            return getInventoryId().compareTo(
                ((SpecimenWrapper) o).getInventoryId());
        }
        return 0;
    }

    @Override
    public String toString() {
        return getInventoryId();
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getDispatchs() {
        List<DispatchWrapper> dispatchs = (List<DispatchWrapper>) cache
            .get(DISPATCHS_CACHE_KEY);
        if (dispatchs == null) {
            List<DispatchSpecimenWrapper> dsaList = getDispatchSpecimenCollection();
            if (dsaList != null) {
                dispatchs = new ArrayList<DispatchWrapper>();
                for (DispatchSpecimenWrapper dsa : dsaList) {
                    dispatchs.add(dsa.getDispatch());
                }
                cache.put(DISPATCHS_CACHE_KEY, dispatchs);
            }
        }
        return dispatchs;
    }

    @Override
    protected Log getLogMessage(String action, String center, String details) {
        Log log = new Log();
        CollectionEventWrapper cevent = getCollectionEvent();
        log.setAction(action);
        if (center == null)
            center = getCurrentCenter().getNameShort();
        log.setCenter(center);
        log.setPatientNumber(cevent.getPatient().getPnumber());
        log.setInventoryId(getInventoryId());
        log.setLocationLabel(getPositionString(true, true));
        log.setDetails(details);
        log.setType("Specimen");
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

    public List<DispatchSpecimenWrapper> getDispatchSpecimenCollection() {
        return getWrapperCollection(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION,
            DispatchSpecimenWrapper.class, false);
    }

    public boolean isUsedInDispatch() {
        return isUsedInDispatch(null);
    }

    public boolean isUsedInDispatch(DispatchWrapper excludedShipment) {
        List<DispatchSpecimenWrapper> dsas = getDispatchSpecimenCollection();
        if (dsas != null)
            for (DispatchSpecimenWrapper dsa : dsas) {
                DispatchWrapper dispatch = dsa.getDispatch();
                if (!dispatch.equals(excludedShipment)
                    && (EnumSet.of(DispatchState.CREATION,
                        DispatchState.IN_TRANSIT, DispatchState.RECEIVED)
                        .contains(dispatch.getDispatchState()))) {
                    if (DispatchSpecimenState.MISSING.equals(dsa
                        .getDispatchSpecimenState())) {
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

    @Override
    protected void persistDependencies(Specimen originalSpecimen)
        throws Exception {

        boolean parentChanged = (originalSpecimen != null
            && originalSpecimen.getParentSpecimen() != null && originalSpecimen
            .getParentSpecimen().equals(getParentSpecimen()));

        if (isNew() || parentChanged) {
            updateChildren();
        }
    }

    private void updateChildren() throws Exception {
        for (SpecimenWrapper child : getChildSpecimenCollection(false)) {
            child.setTopSpecimenInternal(getTopSpecimen());

            // only persist if the child has already been persisted, otherwise
            // only update the reference.
            if (child.isNew()) {
                child.updateChildren();
            } else {
                child.persist();
            }
        }
    }

    @Override
    public void setParentSpecimen(SpecimenWrapper specimen) {
        super.setParentSpecimen(specimen);
        // topSpecimen should never be null
        setTopSpecimenInternal(specimen != null ? specimen.getTopSpecimen()
            : this);
    }

    /**
     * Call <code>setParentSpecimen(SpecimenWrapper)</code> instead of this
     * method to change the top parent. The 'topSpecimen' will be automatically
     * updated.
     */
    @Override
    @Deprecated
    public void setTopSpecimen(SpecimenWrapper specimen) {
        // this method should never be called outside of the wrapper.
        throw new UnsupportedOperationException(
            "this method should never be called");
    }

    protected void setTopSpecimenInternal(SpecimenWrapper specimen) {
        super.setTopSpecimen(specimen);
    }
}
