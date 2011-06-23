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
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfPropertyThenAction.Is;
import edu.ualberta.med.biobank.common.wrappers.actions.UpdateChildrensTopSpecimenAction;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.tasks.NoActionWrapperQueryTask;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage.LazyArg;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenWrapper extends SpecimenBaseWrapper {
    private static final String BAD_SAMPLE_TYPE_MSG = "Container {0} does not allow inserts of sample type {1}.";
    private static final String DISPATCHS_CACHE_KEY = "dispatchs";

    private boolean topSpecimenChanged = false;

    public SpecimenWrapper(WritableApplicationService appService,
        Specimen wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected Specimen getNewObject() throws Exception {
        Specimen newObject = super.getNewObject();
        // by default, any newly created Specimen will have a null parent, so
        // its top is itself.
        newObject.setTopSpecimen(wrappedObject);
        return newObject;
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
        SpecimenPositionWrapper pos = getSpecimenPosition();
        return pos == null ? null : pos.getParent();
    }

    public void setParent(ContainerWrapper container) {
        if (container == null) {
            setSpecimenPosition(null);
        } else {
            initSpecimenPosition().setParent(container);
        }
    }

    public ContainerWrapper getTop() {
        ContainerWrapper top = getParentContainer();
        if (top != null) {
            top = top.getTopContainer();
        }

        return top;
    }

    public boolean hasParent() {
        return getParentContainer() != null;
    }

    public RowColPos getPosition() {
        SpecimenPositionWrapper pos = getSpecimenPosition();
        return pos == null ? null : pos.getPosition();
    }

    public void setPosition(RowColPos rcp) {
        if (rcp == null) {
            setSpecimenPosition(null);
        } else {
            initSpecimenPosition().setPosition(rcp);
        }
    }

    private SpecimenPositionWrapper initSpecimenPosition() {
        SpecimenPositionWrapper specimenPosition = getSpecimenPosition();
        if (specimenPosition == null) {
            specimenPosition = new SpecimenPositionWrapper(appService);
            setSpecimenPosition(specimenPosition);
        }
        return specimenPosition;
    }

    public String getPositionString() {
        return getPositionString(true, true);
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
        // ContainerPathWrapper path = directParent.getContainerPath();
        String nameShort = directParent.getTopContainer().getContainerType()
            .getNameShort();
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
     * @throws Exception
     */
    public static SpecimenWrapper getSpecimen(
        WritableApplicationService appService, String inventoryId, User user)
        throws Exception {
        SpecimenWrapper specimen = getSpecimen(appService, inventoryId);
        if (specimen != null && user != null) {
            CenterWrapper<?> center = specimen.getCurrentCenter();
            if (center != null
                && !user.getWorkingCenters(appService).contains(center)) {
                String name = "none";
                if (center != null)
                    name = center.getNameShort();
                throw new ApplicationException("Specimen " + inventoryId
                    + " exists but you don't have access to it."
                    + " Its current center location (" + name
                    + ") should be a center you can access.");
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
    public List<DispatchWrapper> getDispatches() {
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
        log.setAction(action);
        if (center == null) {
            CenterWrapper<?> c = getCurrentCenter();
            if (c != null) {
                center = c.getNameShort();
            }
        }
        log.setCenter(center);

        CollectionEventWrapper cevent = getCollectionEvent();
        if (cevent != null) {
            PatientWrapper patient = cevent.getPatient();
            if (patient != null) {
                log.setPatientNumber(patient.getPnumber());
            }
        }

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
                        .getSpecimenState())) {
                        return false;
                    }
                    return true;
                }
            }
        return false;
    }

    @Override
    public void setParentSpecimen(SpecimenBaseWrapper specimen) {
        super.setParentSpecimen(specimen);

        // keep the top Specimen up-to-date whenever the parent is set; however,
        // only update this top Specimen, not the children's top Specimen. This
        // is so that the children/ descendants will only be updated if the
        // Specimen whose parent Specimen was changed is persisted.
        SpecimenWrapper topSpecimen = specimen == null ? this : specimen
            .getTopSpecimen();
        setTopSpecimenInternal(topSpecimen, true);
    }

    /**
     * Call {@code setParentSpecimen(SpecimenWrapper parent)} instead of this
     * method to change the top {@code Specimen}. The top {@code Specimen} will
     * be automatically updated.
     */
    @Override
    @Deprecated
    public void setTopSpecimen(SpecimenBaseWrapper specimen) {
        throw new UnsupportedOperationException(
            "Not allowed to directly set the top Specimen. Set the parent Specimen instead.");
    }

    protected void setTopSpecimenInternal(SpecimenWrapper specimen,
        boolean checkDatabase) {
        super.setTopSpecimen(specimen);

        // this is overly cautious, assuming that whenever the top Specimen is
        // set that it is changed. Could be improved to check if the value has
        // actually changed, but would probably require lazy loading.

        if (!isNew() && checkDatabase) {
            // TODO: actually check the database. Get the current
            // topSpecimen through an HQL query and compare it against the
            // one set.
            topSpecimenChanged = true;
            // TODO: may want to set to false if set back to the original?
        } else {
            topSpecimenChanged = true;
        }

    }

    /**
     * Return the top {@code Specimen} of the top loaded {@code Specimen}. This
     * will give the correct "in memory" answer of who the top {@code Specimen}
     * is (whereas super.getTopSpecimen() will give the value from the
     * underlying model).
     */
    @Override
    public SpecimenWrapper getTopSpecimen() {
        // if parent is cached, return their top specimen, otherwise get and
        // return mine (from super).
        if (isPropertyCached(SpecimenPeer.PARENT_SPECIMEN)
            && getParentSpecimen() != null) {
            return getParentSpecimen().getTopSpecimen();
        } else {
            return super.getTopSpecimen();
        }
    }

    private TaskList postCheckLegalSampleType() {
        LazyArg containerLabel = LazyMessage.newArg(this,
            SpecimenPeer.SPECIMEN_POSITION.to(SpecimenPositionPeer.CONTAINER
                .to(ContainerPeer.LABEL)));

        LazyArg specimenType = LazyMessage.newArg(this,
            SpecimenPeer.SPECIMEN_TYPE.to(SpecimenTypePeer.NAME_SHORT));

        LazyMessage badSampleTypeMsg = new LazyMessage(BAD_SAMPLE_TYPE_MSG,
            containerLabel, specimenType);

        Property<Collection<SpecimenType>, Specimen> pathToLegalSpecimenTypeOptions = SpecimenPeer.SPECIMEN_POSITION
            .to(SpecimenPositionPeer.CONTAINER.to(ContainerPeer.CONTAINER_TYPE
                .to(ContainerTypePeer.SPECIMEN_TYPE_COLLECTION)));

        TaskList tasks = new TaskList();

        BiobankSessionAction checkLegalSampleType = check().legalOption(
            pathToLegalSpecimenTypeOptions, SpecimenPeer.SPECIMEN_TYPE,
            badSampleTypeMsg);

        tasks.add(check().ifProperty(
            SpecimenPeer.SPECIMEN_POSITION.to(SpecimenPositionPeer.ID),
            Is.NOT_NULL, checkLegalSampleType));

        return tasks;
    }

    private TaskList updateChildrensTopSpecimen() {
        TaskList tasks = new TaskList();

        if (topSpecimenChanged) {
            SpecimenWrapper topSpecimen = getTopSpecimen();
            if (isPropertyCached(SpecimenPeer.CHILD_SPECIMEN_COLLECTION)) {
                // if the children have already been loaded, then update their
                // top specimen so that they update their children, etc. so that
                // the entire subtree is consistent.
                List<SpecimenWrapper> children = getChildSpecimenCollection(false);
                for (SpecimenWrapper child : children) {
                    child.setTopSpecimenInternal(topSpecimen, false);

                    // Save children whether their are new or not, because the
                    // children's children could be already persistent and need
                    // to be updated (but would then need their parent to be
                    // persisted first).
                    tasks.add(child.getPersistTasks());
                }
            } else {
                // Use HQL to update all descendants of this Specimen because
                // they are not loaded and loading them would be unnecessary.
                tasks.add(new UpdateChildrensTopSpecimenAction(this));
            }

            tasks.add(new ResetTopSpecimenChangedQueryTask(this));
        }

        return tasks;
    }

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().uniqueAndNotNull(SpecimenPeer.INVENTORY_ID));
        tasks.add(check().notNull(SpecimenPeer.SPECIMEN_TYPE));

        tasks.add(cascade().deleteRemovedUnchecked(
            SpecimenPeer.SPECIMEN_POSITION));

        tasks.add(super.getPersistTasks());

        tasks.add(cascade().persist(SpecimenPeer.SPECIMEN_POSITION));

        tasks.add(postCheckLegalSampleType());

        tasks.add(updateChildrensTopSpecimen());

        return tasks;
    }

    @Override
    protected TaskList getDeleteTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().empty(SpecimenPeer.CHILD_SPECIMEN_COLLECTION));

        tasks.add(cascade().delete(SpecimenPeer.SPECIMEN_POSITION));

        tasks.add(super.getDeleteTasks());

        return tasks;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }

    private static class ResetTopSpecimenChangedQueryTask extends
        NoActionWrapperQueryTask<SpecimenWrapper> {
        public ResetTopSpecimenChangedQueryTask(SpecimenWrapper specimen) {
            super(specimen);
        }

        @Override
        public void afterExecute(SDKQueryResult result) {
            getWrapper().topSpecimenChanged = false;
        }
    }
}
