package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.scanprocess.SpecimenHierarchy;
import edu.ualberta.med.biobank.common.scanprocess.data.LinkProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.linkassign.LinkFormPatientManagement.CEventComboCallback;
import edu.ualberta.med.biobank.forms.linkassign.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

// FIXME the custom selection is not done in this version. 
public class SpecimenLinkEntryForm extends AbstractLinkAssignEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenLinkEntryForm"; //$NON-NLS-1$

    private static final String INVENTORY_ID_BINDING = "inventoryId-binding"; //$NON-NLS-1$
    private static final String NEW_SINGLE_POSITION_BINDING = "newSinglePosition-binding"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SpecimenLinkEntryForm.class.getName());

    private static Mode mode = Mode.MULTIPLE;

    // TODO do not need a composite class anymore if only one link form is left
    private LinkFormPatientManagement linkFormPatientManagement;

    // single linking
    // source specimen / type relation when only one specimen
    private AliquotedSpecimenSelectionWidget singleTypesWidget;
    protected boolean inventoryIdModified;
    private Label newSinglePositionLabel;
    private BiobankText newSinglePositionText;
    private StringLengthValidator newSinglePositionValidator;
    private boolean positionTextModified;

    // Multiple linking
    // list of source specimen / type widget for multiple linking
    private List<AliquotedSpecimenSelectionWidget> specimenTypesWidgets;
    private Composite multipleOptionsFields;
    // contains widgets to select type row by row
    private Composite typesSelectionPerRowComposite;
    // row:total (for one row number, associate the number of specimen found on
    // this row)
    private Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
    // List of specimen types that a pallet can have.
    private List<SpecimenTypeWrapper> palletSpecimenTypes;
    // source/type hierarchy selected (use rows order)
    private List<SpecimenHierarchy> preSelections;

    protected boolean modifyingType;

    @Override
    protected void init() throws Exception {
        super.init();
        setPartName(Messages.getString("SpecimenLink.tab.title")); //$NON-NLS-1$
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
        setCanLaunchScan(true);

        // If the current center is a site, and if this site defines containers
        // of 8*12 size, then get the specimen types these containers can
        // contain
        if (SessionManager.getUser().getCurrentWorkingSite() != null) {
            List<SpecimenTypeWrapper> res = SpecimenTypeWrapper
                .getSpecimenTypeForPallet96(appService, SessionManager
                    .getUser().getCurrentWorkingSite());
            if (res.size() != 0)
                palletSpecimenTypes = res;
        }
    }

    @Override
    protected String getFormTitle() {
        return Messages.getString("SpecimenLink.form.title"); //$NON-NLS-1$
    }

    @Override
    protected boolean isSingleMode() {
        return mode.isSingleMode();
    }

    @Override
    protected void setMode(Mode m) {
        mode = m;
    }

    @Override
    protected String getActivityTitle() {
        return Messages.getString("SpecimenLink.activity.title"); //$NON-NLS-1$
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected String getOkMessage() {
        return Messages.getString("SpecimenLink.description.ok"); //$NON-NLS-1$
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected boolean showSinglePosition() {
        return true;
    }

    @Override
    protected void createCommonFields(Composite commonFieldsComposite) {
        // Patient number
        linkFormPatientManagement
            .createPatientNumberText(commonFieldsComposite);
        linkFormPatientManagement
            .setPatientTextCallback(new PatientTextCallback() {
                @Override
                public void focusLost() {
                    setTypeCombos();
                }

                @Override
                public void textModified() {
                }
            });
        // Processing event and Collection events lists
        linkFormPatientManagement.createEventsWidgets(commonFieldsComposite);
        linkFormPatientManagement
            .setCEventComboCallback(new CEventComboCallback() {
                @Override
                public void selectionChanged() {
                    setTypeCombos();
                }
            });
    }

    @Override
    protected void createMultipleFields(Composite parent) {
        multipleOptionsFields = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        multipleOptionsFields.setLayout(layout);
        toolkit.paintBordersFor(multipleOptionsFields);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleOptionsFields.setLayoutData(gd);
        // plate selection
        createPlateToScanField(multipleOptionsFields);
        createScanButton(parent);

        // source/type hierarchy widgets
        typesSelectionPerRowComposite = toolkit.createComposite(parent);
        layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        typesSelectionPerRowComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionPerRowComposite);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        typesSelectionPerRowComposite.setLayoutData(gd);

        toolkit.createLabel(typesSelectionPerRowComposite, ""); //$NON-NLS-1$
        toolkit.createLabel(typesSelectionPerRowComposite,
            Messages.getString("SpecimenLink.source.column.title")); //$NON-NLS-1$
        toolkit.createLabel(typesSelectionPerRowComposite,
            Messages.getString("SpecimenLink.result.column.title")); //$NON-NLS-1$
        toolkit.createLabel(typesSelectionPerRowComposite, ""); //$NON-NLS-1$

        specimenTypesWidgets = new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final AliquotedSpecimenSelectionWidget typeWidget = new AliquotedSpecimenSelectionWidget(
                typesSelectionPerRowComposite,
                ContainerLabelingSchemeWrapper.SBS_ROW_LABELLING_PATTERN
                    .charAt(i), widgetCreator, true);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (typeWidget.needToSave()) {
                            SpecimenHierarchy selection = typeWidget
                                .getSelection();
                            if (selection != null) {
                                @SuppressWarnings("unchecked")
                                Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) palletWidget
                                    .getCells();
                                if (cells != null) {
                                    for (RowColPos rcp : cells.keySet()) {
                                        if (rcp.row == indexRow) {
                                            PalletCell cell = cells.get(rcp);
                                            if (PalletCell.hasValue(cell)) {
                                                setHierarchyToCell(cell,
                                                    selection);
                                            }
                                        }
                                    }
                                    palletWidget.redraw();
                                }
                            }
                        }
                        if (palletWidget.isEverythingTyped()) {
                            setDirty(true);
                        }
                    }

                });
            typeWidget.addBindings();
            specimenTypesWidgets.add(typeWidget);
            if (precedent != null) {
                precedent.setNextWidget(typeWidget);
            }
            precedent = typeWidget;
            typeWidget.setEnabled(true);
        }
    }

    @Override
    protected void defaultInitialisation() {
        setNeedSinglePosition(mode == Mode.SINGLE_POSITION);
    }

    @Override
    protected void setNeedSinglePosition(boolean position) {
        widgetCreator.setBinding(NEW_SINGLE_POSITION_BINDING, position);
        widgetCreator.showWidget(newSinglePositionLabel, position);
        widgetCreator.showWidget(newSinglePositionText, position);
    }

    @Override
    protected void createSingleFields(Composite parent) {
        Composite fieldsComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        fieldsComposite.setLayoutData(gd);

        final NonEmptyStringValidator idValidator = new NonEmptyStringValidator(
            Messages.getString("SpecimenLink.inventoryId.validator.msg"));
        // inventoryID
        final BiobankText inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            fieldsComposite, BiobankText.class,
            SWT.NONE,
            Messages.getString("SpecimenLink.inventoryId.label"), //$NON-NLS-1$
            new String[0], singleSpecimen, SpecimenPeer.INVENTORY_ID.getName(),
            idValidator, //$NON-NLS-1$
            INVENTORY_ID_BINDING);
        inventoryIdText.addKeyListener(textFieldKeyListener);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (inventoryIdModified
                    && idValidator.validate(inventoryIdText.getText()) == Status.OK_STATUS) {
                    BusyIndicator.showWhile(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell().getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                checkInventoryId(inventoryIdText);
                            }
                        });
                }
                inventoryIdModified = false;
            }
        });
        inventoryIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                inventoryIdModified = true;
                newSinglePositionText.setText("");
                canSaveSingleSpecimen.setValue(false);
            }
        });

        // widget to select the source and the type
        singleTypesWidget = new AliquotedSpecimenSelectionWidget(
            fieldsComposite, null, widgetCreator, false);
        singleTypesWidget
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    modifyingType = true;
                    newSinglePositionText.setText("");
                    modifyingType = false;
                }
            });
        singleTypesWidget.addBindings();

        newSinglePositionLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("SpecimenAssign.single.position.label")); //$NON-NLS-1$
        newSinglePositionValidator = new StringLengthValidator(4,
            Messages.getString("SpecimenAssign.single.position.validationMsg")); //$NON-NLS-1$
        newSinglePositionText = (BiobankText) widgetCreator.createBoundWidget(
            fieldsComposite, BiobankText.class, SWT.NONE,
            newSinglePositionLabel, new String[0], new WritableValue("", //$NON-NLS-1$
                String.class), newSinglePositionValidator,
            NEW_SINGLE_POSITION_BINDING);
        newSinglePositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (positionTextModified
                    && newSinglePositionValidator
                        .validate(newSinglePositionText.getText()) == Status.OK_STATUS) {
                    BusyIndicator.showWhile(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell().getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                initContainersFromPosition(
                                    newSinglePositionText, false, null);
                                checkPositionAndSpecimen(inventoryIdText,
                                    newSinglePositionText);
                            }
                        });
                }
                positionTextModified = false;
            }
        });
        newSinglePositionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (!modifyingType) {
                    positionTextModified = true;
                    displaySinglePositions(false);
                    canSaveSingleSpecimen.setValue(false);
                }
            }
        });
        newSinglePositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
    }

    private void checkInventoryId(BiobankText inventoryIdText) {
        boolean ok = true;
        try {
            SpecimenWrapper specimen = SpecimenWrapper.getSpecimen(appService,
                inventoryIdText.getText());
            if (specimen != null) {
                BiobankGuiCommonPlugin.openAsyncError("InventoryId error",
                    "InventoryId " + inventoryIdText.getText()
                        + " already exists.");
                ok = false;
            }
        } catch (Exception e) {
            BiobankGuiCommonPlugin.openAsyncError("Error checking inventoryId",
                e);
            ok = false;
        }
        singleTypesWidget.setEnabled(ok);
        newSinglePositionText.setEnabled(ok);
        canSaveSingleSpecimen.setValue(ok);
        if (!ok)
            focusControl(inventoryIdText);

    }

    @Override
    protected void setBindings(boolean isSingleMode) {
        super.setBindings(isSingleMode);
        widgetCreator.setBinding(INVENTORY_ID_BINDING, isSingleMode);
        if (isSingleMode) {
            singleTypesWidget.addBindings();
        } else {
            singleTypesWidget.removeBindings();
        }
    }

    private void setTypeCombos() {
        setTypeCombos(null);
    }

    /**
     * Get types only defined in the patient's study. Then set these types to
     * the types combos
     * 
     * @param typesRows used only in multiple to indicate the count of each row
     *            after scan has been done.
     */
    private void setTypeCombos(Map<Integer, Integer> typesRows) {
        List<SpecimenTypeWrapper> studiesAliquotedTypes = null;
        List<SpecimenTypeWrapper> authorizedTypesInContainers = null;
        if (typesRows != null)
            authorizedTypesInContainers = palletSpecimenTypes;
        studiesAliquotedTypes = linkFormPatientManagement
            .getStudyAliquotedTypes(authorizedTypesInContainers);
        List<SpecimenWrapper> availableSourceSpecimens = linkFormPatientManagement
            .getParentSpecimenForPEventAndCEvent();
        if (authorizedTypesInContainers != null) {
            // availableSourceSpecimen should be parents of the authorized Types
            // !
            List<SpecimenWrapper> filteredSpecs = new ArrayList<SpecimenWrapper>();
            for (SpecimenWrapper spec : availableSourceSpecimens)
                if (!Collections.disjoint(authorizedTypesInContainers, spec
                    .getSpecimenType().getChildSpecimenTypeCollection(false)))
                    filteredSpecs.add(spec);
            availableSourceSpecimens = filteredSpecs;
        }
        // for multiple
        for (int row = 0; row < specimenTypesWidgets.size(); row++) {
            if (typesRows != null)
                setCountOnSpecimenWidget(typesRows, row);
            if (isFirstSuccessfulScan()) {
                AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                    .get(row);
                widget.setSourceSpecimens(availableSourceSpecimens);
                widget.setResultTypes(studiesAliquotedTypes);
            }
        }
        if (typesRows == null) {
            // for single
            singleTypesWidget.setSourceSpecimens(availableSourceSpecimens);
            singleTypesWidget.setResultTypes(studiesAliquotedTypes);
        }
    }

    @Override
    protected boolean fieldsValid() {
        return (mode.isSingleMode() ? true : isPlateValid())
            && linkFormPatientManagement.fieldsValid();
    }

    @Override
    protected void doBeforeSave() throws Exception {
        // can't access the combos in another thread, so do it now
        if (mode.isSingleMode()) {
            SpecimenHierarchy selection = singleTypesWidget.getSelection();
            singleSpecimen.setParentSpecimen(selection.getParentSpecimen());
            singleSpecimen
                .setSpecimenType(selection.getAliquotedSpecimenType());
            singleSpecimen.setCollectionEvent(linkFormPatientManagement
                .getSelectedCollectionEvent());
        }
    }

    @Override
    protected void saveForm() throws Exception {
        // FIXME need to use BatchQuery

        OriginInfoWrapper originInfo = new OriginInfoWrapper(
            SessionManager.getAppService());
        originInfo
            .setCenter(SessionManager.getUser().getCurrentWorkingCenter());
        originInfo.persist();
        if (mode.isSingleMode())
            saveSingleSpecimen(originInfo);
        else
            saveMultipleSpecimens(originInfo);
        setFinished(false);
    }

    private void saveMultipleSpecimens(OriginInfoWrapper originInfo)
        throws Exception {
        @SuppressWarnings("unchecked")
        Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) palletWidget
            .getCells();
        StringBuffer sb = new StringBuffer(
            Messages.getString("SpecimenLink.activitylog.specimens.start")); //$NON-NLS-1$
        int nber = 0;
        ActivityStatusWrapper activeStatus = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        // use a set because do not want to add the same parent twice
        CenterWrapper<?> currentSelectedCenter = SessionManager.getUser()
            .getCurrentWorkingCenter();
        Set<SpecimenWrapper> modifiedSources = new HashSet<SpecimenWrapper>();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == UICellStatus.TYPE) {
                SpecimenWrapper sourceSpecimen = cell.getSourceSpecimen();
                SpecimenWrapper aliquotedSpecimen = cell.getSpecimen();
                aliquotedSpecimen.setInventoryId(cell.getValue());
                aliquotedSpecimen.setCreatedAt(new Date());
                aliquotedSpecimen.setActivityStatus(activeStatus);
                aliquotedSpecimen.setCurrentCenter(currentSelectedCenter);
                aliquotedSpecimen.setOriginInfo(originInfo);
                aliquotedSpecimen.setParentSpecimen(sourceSpecimen);
                aliquotedSpecimen.setCollectionEvent(sourceSpecimen
                    .getCollectionEvent());
                aliquotedSpecimen.setQuantityFromType();
                sourceSpecimen.addToChildSpecimenCollection(Arrays
                    .asList(aliquotedSpecimen));
                modifiedSources.add(sourceSpecimen);
                // LINKED\: {0} - Type: {1} - Patient\: {2} - Visit\: {3} -
                // Center: {4} \n
                sb.append(Messages.getString(
                    "SpecimenLink.activitylog.specimen.linked", //$NON-NLS-1$
                    cell.getValue(), cell.getType().getName(), sourceSpecimen
                        .getSpecimenType().getNameShort(), sourceSpecimen
                        .getInventoryId(), sourceSpecimen.getCollectionEvent()
                        .getPatient().getPnumber(), sourceSpecimen
                        .getCollectionEvent().getVisitNumber(),
                    currentSelectedCenter.getNameShort()));
                nber++;
            }
        }
        // persist of parent will automatically persist children
        ModelWrapper.persistBatch(modifiedSources);
        // display logs only if persist succeeds.
        appendLog(sb.toString());

        // LINKING\: {0} specimens linked to patient {1} on center {2}
        appendLog(Messages.getString(
            "SpecimenLink.activitylog.save.summary", nber, //$NON-NLS-1$
            linkFormPatientManagement.getCurrentPatient().getPnumber(),
            currentSelectedCenter.getNameShort()));
    }

    private void saveSingleSpecimen(OriginInfoWrapper originInfo)
        throws Exception {
        singleSpecimen.setCreatedAt(new Date());
        singleSpecimen.setQuantityFromType();
        singleSpecimen.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        singleSpecimen.setCurrentCenter(SessionManager.getUser()
            .getCurrentWorkingCenter());

        singleSpecimen.setOriginInfo(originInfo);
        singleSpecimen.persist();
        String posStr = singleSpecimen.getPositionString(true, false);
        if (posStr == null) {
            posStr = Messages.getString("SpecimenLink.position.label.none"); //$NON-NLS-1$
        }
        // LINKED\: specimen {0} of type\: {1} to source\: {2} ({3}) -
        // Patient\: {4} - Visit\: {5} - Center\: {6} \n
        appendLog(Messages.getString(
            "SpecimenLink.activitylog.specimen.linked", singleSpecimen //$NON-NLS-1$
                .getInventoryId(), singleSpecimen.getSpecimenType().getName(),
            singleSpecimen.getParentSpecimen().getInventoryId(), singleSpecimen
                .getParentSpecimen().getSpecimenType().getNameShort(),
            linkFormPatientManagement.getCurrentPatient().getPnumber(),
            singleSpecimen.getCollectionEvent().getVisitNumber(),
            singleSpecimen.getCurrentCenter().getNameShort()));
    }

    @Override
    public void onReset() throws Exception {
        super.onReset();
        singleTypesWidget.deselectAll();
        showOnlyPallet(true);
        form.layout(true, true);
    }

    @Override
    public void reset(boolean resetAll) {
        linkFormPatientManagement.reset(resetAll);
        if (resetAll) {
            palletWidget.setCells(null);
            for (AliquotedSpecimenSelectionWidget stw : specimenTypesWidgets) {
                stw.resetValues(true, true);
            }
        }
        super.reset(resetAll);
    }

    @Override
    public boolean onClose() {
        linkFormPatientManagement.onClose();
        return super.onClose();
    }

    @Override
    /**
     * Multiple linking: do this before multiple scan is made
     */
    protected void beforeScanThreadStart() {
        super.beforeScanThreadStart();
        beforeScans(true);
    }

    @Override
    /**
     * Multiple linking: do this before scan of one tube is really made
     */
    protected void beforeScanTubeAlone() {
        super.beforeScanTubeAlone();
        beforeScans(false);
    }

    /**
     * Multiple linking: do this before any scan is really launched
     */
    private void beforeScans(boolean resetTypeRows) {
        preSelections = new ArrayList<SpecimenHierarchy>();
        for (AliquotedSpecimenSelectionWidget stw : specimenTypesWidgets) {
            preSelections.add(stw.getSelection());
        }
        if (resetTypeRows)
            typesRows.clear();
    }

    @Override
    /**
     * Multiple linking: return fake cells for testing
     */
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        if (isFakeScanRandom) {
            return PalletCell.getRandomScanLink();
        }
        try {
            return PalletCell.getRandomScanLinkWithSpecimensAlreadyLinked(
                appService, SessionManager.getUser().getCurrentWorkingCenter()
                    .getId());
        } catch (Exception ex) {
            BiobankGuiCommonPlugin.openAsyncError("Fake Scan problem", ex); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    protected ProcessData getProcessData() {
        return new LinkProcessData();
    }

    @Override
    /**
     *  Multiple linking: do this after multiple scan has been launched
     */
    protected void afterScanAndProcess(final Integer rowToProcess) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                // enabled the hierarchy combos
                typesSelectionPerRowComposite
                    .setEnabled(currentScanState != UICellStatus.ERROR);
                // set the combos lists
                if (typesRows.size() > 0)
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (rowToProcess == null)
                                setTypeCombos(typesRows);
                            else {
                                setCountOnSpecimenWidget(typesRows,
                                    rowToProcess);
                            }
                        }
                    });
                // focus first available list
                for (AliquotedSpecimenSelectionWidget typeWidget : specimenTypesWidgets) {
                    if (typeWidget.canFocus()) {
                        typeWidget.setFocus();
                        break;
                    }
                }
                // Show result in grid
                palletWidget.setCells(getCells());
                setRescanMode();
            }
        });
    }

    /**
     * Multiple linking: Post process of only one cell after server call has
     * been made
     */
    @Override
    protected void processCellResult(final RowColPos rcp, PalletCell cell) {
        Integer typesRowsCount = typesRows.get(rcp.row);
        if (typesRowsCount == null) {
            typesRowsCount = 0;
            specimenTypesWidgets.get(rcp.row).resetValues(!isRescanMode(),
                true, true);
        }
        SpecimenHierarchy selection = preSelections.get(cell.getRow());
        if (selection != null)
            setHierarchyToCell(cell, selection);
        if (PalletCell.hasValue(cell)) {
            typesRowsCount++;
            typesRows.put(rcp.row, typesRowsCount);
        }
    }

    /**
     * Multiple linking: apply a count on a specific row of 'hierarchy' widgets.
     * 
     * @param typesRows contains a row:count map
     * @param row row of the widget we want to update.
     */
    private void setCountOnSpecimenWidget(Map<Integer, Integer> typesRows,
        int row) {
        AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets.get(row);
        Integer number = typesRows.get(row);
        if (number == null) {
            number = 0;
            widget.deselectAll();
        }
        widget.setNumber(number);
    }

    /**
     * Multiple linking: apply a source and type to a specific cell.
     */
    private void setHierarchyToCell(PalletCell cell, SpecimenHierarchy selection) {
        cell.setSourceSpecimen(selection.getParentSpecimen());
        cell.setSpecimenType(selection.getAliquotedSpecimenType());
        if (cell.getStatus() != UICellStatus.ERROR)
            cell.setStatus(UICellStatus.TYPE);
    }

    @Override
    protected Mode initialisationMode() {
        return mode;
    }

    @Override
    protected void enableFields(boolean enable) {
        super.enableFields(enable);
        multipleOptionsFields.setEnabled(enable);
    }

}
