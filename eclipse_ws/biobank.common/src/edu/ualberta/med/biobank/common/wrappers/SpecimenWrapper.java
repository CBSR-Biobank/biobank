package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudySpecimenAttrWrapper;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenWrapper extends SpecimenBaseWrapper {

    private static final String DISPATCHS_CACHE_KEY = "dispatchs";
    private AbstractObjectWithPositionManagement<SpecimenPosition, SpecimenWrapper> objectWithPositionManagement;

    private Map<String, StudySpecimenAttrWrapper> studySpecimenAttrMap;
    private Map<String, SpecimenAttrWrapper> specimenAttrMap;
    private SpecimenAttrWrapper specimenAttr;

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
    public static SpecimenWrapper getSpecimen(
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
            String s1 = getPositionString(true, true);
            String s2 = ((SpecimenWrapper) o).getPositionString(true, true);
            if (s1 == null || s2 == null)
                getInventoryId().compareTo(
                    ((SpecimenWrapper) o).getInventoryId());
            else
                return s1.compareTo(s2);
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
                        .getSpecimenState())) {
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
        if (isNew())
            setTopSpecimenInternal(this);
        specimenAttrMap = null;
        studySpecimenAttrMap = null;
    }

    @Override
    protected void persistDependencies(Specimen originalSpecimen)
        throws Exception {

        boolean parentChanged = (originalSpecimen != null
            && originalSpecimen.getParentSpecimen() != null && originalSpecimen
            .getParentSpecimen().getId().equals(getParentSpecimen().getId()));

        if (isNew() || parentChanged) {
            updateChildren();
        }
        if (specimenAttrMap != null) {
            setWrapperCollection(SpecimenPeer.SPECIMEN_ATTR_COLLECTION,
                specimenAttrMap.values());
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

    /**
     * return a string with collection date (different from created at if it is
     * an aliquoted specimen) + the collection center
     */
    public String getCollectionInfo() {
        return getTopSpecimen().getFormattedCreatedAt() + " in "
            + getTopSpecimen().getOriginInfo().getCenter().getNameShort()
            + " (visit #" + getCollectionEvent().getVisitNumber() + ")";
    }

    public boolean hasUnknownImportType() {
        return getSpecimenType() != null && getSpecimenType().isUnknownImport();
    }

    // DFE
    private Map<String, StudySpecimenAttrWrapper> getStudySpecimenAttrMap() {
        if (studySpecimenAttrMap != null)
            return studySpecimenAttrMap;

        CollectionEventWrapper collectionEvent = getCollectionEvent();
        PatientWrapper patient = collectionEvent.getPatient();

        studySpecimenAttrMap = new HashMap<String, StudySpecimenAttrWrapper>();
        if (patient != null && patient.getStudy() != null) {
            Collection<StudySpecimenAttrWrapper> studySpecimenAttrCollection = patient
                .getStudy().getStudySpecimenAttrCollection();
            if (studySpecimenAttrCollection != null) {
                for (StudySpecimenAttrWrapper studySpecimenAttr : studySpecimenAttrCollection) {
                    studySpecimenAttrMap.put(studySpecimenAttr.getLabel(),
                        studySpecimenAttr);
                }
            }
        }
        return studySpecimenAttrMap;
    }

    private Map<String, SpecimenAttrWrapper> getSpecimenAttrMap() {
        getStudySpecimenAttrMap();
        if (specimenAttrMap != null)
            return specimenAttrMap;

        specimenAttrMap = new HashMap<String, SpecimenAttrWrapper>();
        List<SpecimenAttrWrapper> pvAttrCollection = getSpecimenAttrCollection(false);

        if (pvAttrCollection != null) {
            for (SpecimenAttrWrapper pvAttr : pvAttrCollection) {
                specimenAttrMap.put(pvAttr.getStudySpecimenAttr().getLabel(),
                    pvAttr);
            }
        }
        return specimenAttrMap;
    }

    public String[] getSpecimenAttrLabels() {
        getSpecimenAttrMap();
        return specimenAttrMap.keySet().toArray(new String[] {});
    }

    public String getSpecimenAttrValue(String label) throws Exception {
        getSpecimenAttrMap();
        SpecimenAttrWrapper pvAttr = specimenAttrMap.get(label);
        if (pvAttr == null) {
            StudySpecimenAttrWrapper studySpecimenAttr = studySpecimenAttrMap
                .get(label);
            // make sure "label" is a valid study pv attr
            if (studySpecimenAttr == null) {
                throw new Exception("StudySpecimenAttr with label \"" + label
                    + "\" is invalid");
            }
            // not assigned yet so return null
            return null;
        }
        return pvAttr.getValue();
    }

    public String getSpecimenAttrTypeName(String label) throws Exception {
        getSpecimenAttrMap();
        SpecimenAttrWrapper pvAttr = specimenAttrMap.get(label);
        StudySpecimenAttrWrapper studySpecimenAttr = null;
        if (pvAttr != null) {
            studySpecimenAttr = pvAttr.getStudySpecimenAttr();
        } else {
            studySpecimenAttr = studySpecimenAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studySpecimenAttr == null) {
                throw new Exception("StudySpecimenAttr withr label \"" + label
                    + "\" does not exist");
            }
        }
        return studySpecimenAttr.getSpecimenAttrType().getName();
    }

    public String[] getSpecimenAttrPermissible(String label) throws Exception {
        getSpecimenAttrMap();
        SpecimenAttrWrapper pvAttr = specimenAttrMap.get(label);
        StudySpecimenAttrWrapper studySpecimenAttr = null;
        if (pvAttr != null) {
            studySpecimenAttr = pvAttr.getStudySpecimenAttr();
        } else {
            studySpecimenAttr = studySpecimenAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studySpecimenAttr == null) {
                throw new Exception("SpecimenAttr for label \"" + label
                    + "\" does not exist");
            }
        }
        String permissible = studySpecimenAttr.getPermissible();
        if (permissible == null) {
            return null;
        }
        return permissible.split(";");
    }

    protected SpecimenAttrWrapper getSpecimenAttr(String label)
        throws Exception {
        getStudySpecimenAttrMap();
        SpecimenAttrWrapper studySpecimenAttr = specimenAttrMap.get(label);
        if (specimenAttr == null) {
            throw new Exception("StudyEventAttr with label \"" + label
                + "\" is invalid");
        }
        return studySpecimenAttr;
    }

    public SpecimenAttrTypeEnum getSpecimenAttrType(String label)
        throws Exception {
        return SpecimenAttrTypeEnum.getSpecimenAttrType(getSpecimenAttrType(
            label).getName());
    }

    /**
     * Assigns a value to a patient visit attribute. The value is parsed for
     * correctness.
     * 
     * @param label The attribute's label.
     * @param value The value to assign.
     * @throws Exception when assigning a label of type "select_single" or
     *             "select_multiple" and the value is not one of the permissible
     *             ones.
     * @throws NumberFormatException when assigning a label of type "number" and
     *             the value is not a valid double number.
     * @throws ParseException when assigning a label of type "date_time" and the
     *             value is not a valid date and time.
     * @see edu.ualberta.med.biobank
     *      .common.formatters.DateFormatter.DATE_TIME_FORMAT
     */
    public void setSpecimenAttrValue(String label, String value)
        throws Exception {
        getSpecimenAttrMap();
        SpecimenAttrWrapper pvAttr = specimenAttrMap.get(label);
        StudySpecimenAttrWrapper studySpecimenAttr = null;

        if (pvAttr != null) {
            studySpecimenAttr = pvAttr.getStudySpecimenAttr();
        } else {
            studySpecimenAttr = studySpecimenAttrMap.get(label);
            if (studySpecimenAttr == null) {
                throw new Exception("no StudySpecimenAttr found for label \""
                    + label + "\"");
            }
        }

        if (!studySpecimenAttr.getActivityStatus().isActive()) {
            throw new Exception("attribute for label \"" + label
                + "\" is locked, changes not premitted");
        }

        if (value != null) {
            // validate the value
            value = value.trim();
            if (value.length() > 0) {
                String type = studySpecimenAttr.getSpecimenAttrType().getName();
                List<String> permissibleSplit = null;

                if (SpecimenAttrTypeEnum.SELECT_SINGLE.isSameType(type)
                    || SpecimenAttrTypeEnum.SELECT_MULTIPLE.isSameType(type)) {
                    String permissible = studySpecimenAttr.getPermissible();
                    if (permissible != null) {
                        permissibleSplit = Arrays
                            .asList(permissible.split(";"));
                    }
                }

                if (SpecimenAttrTypeEnum.SELECT_SINGLE.isSameType(type)) {
                    if (!permissibleSplit.contains(value)) {
                        throw new Exception("value " + value
                            + "is invalid for label \"" + label + "\"");
                    }
                } else if (SpecimenAttrTypeEnum.SELECT_MULTIPLE
                    .isSameType(type)) {
                    for (String singleVal : value.split(";")) {
                        if (!permissibleSplit.contains(singleVal)) {
                            throw new Exception("value " + singleVal + " ("
                                + value + ") is invalid for label \"" + label
                                + "\"");
                        }
                    }
                } else if (SpecimenAttrTypeEnum.NUMBER.isSameType(type)) {
                    Double.parseDouble(value);
                } else if (SpecimenAttrTypeEnum.DATE_TIME.isSameType(type)) {
                    DateFormatter.dateFormatter.parse(value);
                } else if (SpecimenAttrTypeEnum.TEXT.isSameType(type)) {
                    // do nothing
                } else {
                    throw new Exception("type \"" + type + "\" not tested");
                }
            }
        }

        if (pvAttr == null) {
            pvAttr = new SpecimenAttrWrapper(appService);
            pvAttr.setSpecimen(this);
            pvAttr.setStudySpecimenAttr(studySpecimenAttr);
            specimenAttrMap.put(label, pvAttr);
        }
        pvAttr.setValue(value);
    }

}
