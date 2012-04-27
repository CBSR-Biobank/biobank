package edu.ualberta.med.biobank.forms.linkassign;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osgi.util.NLS;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenHierarchyInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenLinkProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenResInfo;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.linkassign.LinkFormPatientManagement.CEventComboCallback;
import edu.ualberta.med.biobank.forms.linkassign.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCellPos;
import gov.nih.nci.system.applicationservice.ApplicationException;

// FIXME the custom selection is not done in this version. 
public class SpecimenLinkEntryForm extends AbstractLinkAssignEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenLinkEntryForm";

    protected static BgcLogger log = BgcLogger
        .getLogger(SpecimenLinkEntryForm.class.getName());

    private static final String INVENTORY_ID_BINDING = "inventoryId-binding";
    private static final String NEW_SINGLE_POSITION_BINDING =
        "newSinglePosition-binding";

    private static BgcLogger logger = BgcLogger
        .getLogger(SpecimenLinkEntryForm.class.getName());

    private static Mode mode = Mode.MULTIPLE;

    // TODO do not need a composite class anymore if only one link form is left
    private final LinkFormPatientManagement linkFormPatientManagement;

    // single linking
    // source specimen / type relation when only one specimen
    private AliquotedSpecimenSelectionWidget singleTypesWidget;
    protected boolean inventoryIdModified;
    private Label newSinglePositionLabel;
    private BgcBaseText newSinglePositionText;
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
    private final Map<Integer, Integer> typesRows =
        new HashMap<Integer, Integer>();
    // List of specimen types that a pallet can have.
    // private List<SpecimenTypeWrapper> palletSpecimenTypes;

    // source/type hierarchy selected (use rows order)
    private List<SpecimenHierarchyInfo> preSelections;

    public SpecimenLinkEntryForm() {
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
    }

    @Override
    protected void init() throws Exception {
        log.debug("init");
        super.init();
        setPartName("Linking specimens");
        setCanLaunchScan(true);
    }

    @Override
    protected String getFormTitle() {
        return "Linking specimens";
    }

    @Override
    protected boolean isSingleMode() {
        log.debug("isSingleMode:" + mode.isSingleMode());
        return mode.isSingleMode();
    }

    @Override
    protected void setMode(Mode m) {
        log.debug("setMode: " + mode);
        mode = m;
    }

    @Override
    protected String getActivityTitle() {
        return "Specimen Link";
    }

    @Override
    public BgcLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected String getOkMessage() {
        return "Link specimens to their source specimens";
    }

    @Override
    public String getNextOpenedFormId() {
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

        toolkit.createLabel(typesSelectionPerRowComposite, "");
        toolkit.createLabel(typesSelectionPerRowComposite,
            "Source Specimen");
        toolkit.createLabel(typesSelectionPerRowComposite,
            "Aliquoted Specimen Types");
        toolkit.createLabel(typesSelectionPerRowComposite, "");

        specimenTypesWidgets =
            new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;
        for (int i = 0; i < ScanCellPos.ROW_MAX; i++) {
            final AliquotedSpecimenSelectionWidget typeWidget =
                new AliquotedSpecimenSelectionWidget(
                    typesSelectionPerRowComposite,
                    ContainerLabelingSchemeWrapper.SBS_ROW_LABELLING_PATTERN
                        .charAt(i), widgetCreator, true);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (typeWidget.needToSave()) {
                            SpecimenHierarchyInfo selection = typeWidget
                                .getSelection();
                            if (selection != null) {
                                @SuppressWarnings("unchecked")
                                Map<RowColPos, PalletCell> cells =
                                    (Map<RowColPos, PalletCell>) palletWidget
                                        .getCells();
                                if (cells != null) {
                                    for (RowColPos rcp : cells.keySet()) {
                                        if (rcp.getRow() == indexRow) {
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
            typeWidget.setNumber(0);
            typeWidget.setEnabled(false);
        }
    }

    @Override
    protected void defaultInitialisation() {
        log.debug("defaultInitialisation");
        super.defaultInitialisation();
        setNeedSinglePosition(mode == Mode.SINGLE_POSITION);
    }

    @Override
    protected void setNeedSinglePosition(boolean position) {
        log.debug("setNeedSinglePosition: " + position);
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

        final NonEmptyStringValidator idValidator =
            new NonEmptyStringValidator(
                "Inventory ID cannot be empty");
        // inventoryID
        final BgcBaseText inventoryIdText =
            (BgcBaseText) createBoundWidgetWithLabel(
                fieldsComposite, BgcBaseText.class, SWT.NONE,
                "Inventory ID",
                new String[0],
                singleSpecimen, SpecimenPeer.INVENTORY_ID.getName(),
                idValidator,
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

        // position field
        newSinglePositionLabel = widgetCreator.createLabel(fieldsComposite,
            "Position");
        newSinglePositionValidator = new StringLengthValidator(4,
            "Enter a position");
        newSinglePositionText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite, BgcBaseText.class, SWT.NONE,
            newSinglePositionLabel, new String[0], new WritableValue("",
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
                positionTextModified = true;
                displaySinglePositions(false);
                canSaveSingleSpecimen.setValue(false);
            }
        });
        newSinglePositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // widget to select the source and the type
        singleTypesWidget = new AliquotedSpecimenSelectionWidget(
            fieldsComposite, null, widgetCreator, false);
        singleTypesWidget.addBindings();
    }

    private void checkInventoryId(BgcBaseText inventoryIdText) {
        log.debug("checkInventoryId: " + inventoryIdText.getText());
        boolean ok = true;
        try {
            SpecimenWrapper specimen = SpecimenWrapper.getSpecimen(
                SessionManager.getAppService(), inventoryIdText.getText());
            if (specimen != null) {
                BgcPlugin
                    .openAsyncError(
                        "InventoryId error",
                        NLS.bind(
                            "InventoryId {0} already exists.",
                            inventoryIdText.getText()));
                ok = false;
            }
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(
                    "Error checking inventoryId",
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
        singleTypesWidget.removeBindings();
        if (isSingleMode) {
            singleTypesWidget.addBindings();
            singleTypesWidget.deselectAll();
        }
    }

    @Override
    protected void updateAvailableSpecimenTypes() {
        log.debug("updateAvailableSpecimenTypes");
        setTypeCombos();
    }

    /**
     * Get types only defined in the patient's study. Then set these types to
     * the types combos
     * 
     * @param typesRowss used only in multiple to indicate the count of each row
     *            after scan has been done.
     * @throws ApplicationException
     */
    private void setTypeCombos() {
        log.debug("setTypeCombos");

        List<SpecimenTypeWrapper> studiesAliquotedTypes = null;
        List<SpecimenTypeWrapper> authorizedTypesInContainers = null;
        if (isSingleMode()) {
            log.debug("setTypeCombos: single mode");

            if ((parentContainers != null) && (parentContainers.size() >= 1)) {
                authorizedTypesInContainers =
                    parentContainers.get(0).getContainerType()
                        .getSpecimenTypeCollection();
            }
        } else {
            log.debug("setTypeCombos: multiple mode");

            /*
             * If the current center is a site, and if this site defines
             * containers of 8*12 size, then get the specimen types these
             * containers can contain
             */
            if (SessionManager.getUser().getCurrentWorkingSite() != null) {
                List<SpecimenTypeWrapper> res = null;
                try {
                    res = SpecimenTypeWrapper.getSpecimenTypeForPallet96(
                        SessionManager.getAppService(), SessionManager
                            .getUser()
                            .getCurrentWorkingSite());
                } catch (ApplicationException e) {
                    BgcPlugin.openAsyncError("Error",
                        "Failed to retrieve specimen types.");
                }
                if (res.size() != 0)
                    authorizedTypesInContainers = res;
            }
        }

        studiesAliquotedTypes = linkFormPatientManagement
            .getStudyAliquotedTypes(authorizedTypesInContainers);
        List<SpecimenWrapper> availableSourceSpecimens =
            linkFormPatientManagement
                .getParentSpecimenForPEventAndCEvent();
        if (authorizedTypesInContainers != null) {
            // availableSourceSpecimen should be parents of the authorized Types
            // !
            List<SpecimenWrapper> filteredSpecs =
                new ArrayList<SpecimenWrapper>();
            for (SpecimenWrapper spec : availableSourceSpecimens)
                if (!Collections.disjoint(authorizedTypesInContainers, spec
                    .getSpecimenType().getChildSpecimenTypeCollection(false)))
                    filteredSpecs.add(spec);
            availableSourceSpecimens = filteredSpecs;
        }
        // for single
        singleTypesWidget.resetValues(true, false);
        singleTypesWidget.setSourceSpecimens(availableSourceSpecimens);
        singleTypesWidget.setResultTypes(studiesAliquotedTypes);
        if (!isSingleMode())
            for (int row = 0; row < specimenTypesWidgets.size(); row++) {
                AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                    .get(row);
                // if rescan, want to keep previous selection
            SpecimenHierarchyInfo previousSelection = widget.getSelection();
            widget.setSourceSpecimens(availableSourceSpecimens);
            widget.setResultTypes(studiesAliquotedTypes);
            widget.setSelection(previousSelection);
        }
    }

    @Override
    protected boolean fieldsValid() {
        return (mode.isSingleMode() ? true : isPlateValid())
            && linkFormPatientManagement.fieldsValid();
    }

    @Override
    protected void doBeforeSave() throws Exception {
        log.debug("doBeforeSave");
        // can't access the combos in another thread, so do it now
        if (mode.isSingleMode()) {
            SpecimenHierarchyInfo selection = singleTypesWidget.getSelection();
            singleSpecimen.setParentSpecimen(selection.getParentSpecimen());
            singleSpecimen
                .setSpecimenType(selection.getAliquotedSpecimenType());
            singleSpecimen.setCollectionEvent(linkFormPatientManagement
                .getSelectedCollectionEvent());
        }
    }

    @Override
    protected void saveForm() throws Exception {
        log.debug("saveForm");
        if (mode.isSingleMode())
            saveSingleSpecimen();
        else
            saveMultipleSpecimens();
        setFinished(false);
        SessionManager.log("save", null,
            "SpecimenLink");
    }

    private void saveMultipleSpecimens() throws Exception {
        log.debug("saveForm");

        @SuppressWarnings("unchecked")
        Map<RowColPos, PalletCell> cells =
            (Map<RowColPos, PalletCell>) palletWidget
                .getCells();
        List<AliquotedSpecimenInfo> asiList =
            new ArrayList<AliquotedSpecimenInfo>();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == UICellStatus.TYPE) {
                SpecimenWrapper sourceSpecimen = cell.getSourceSpecimen();
                SpecimenWrapper aliquotedSpecimen = cell.getSpecimen();
                AliquotedSpecimenInfo asi = new AliquotedSpecimenInfo();
                asi.activityStatus = ActivityStatus.ACTIVE;
                asi.typeId = aliquotedSpecimen.getSpecimenType().getId();
                asi.inventoryId = cell.getValue();
                asi.parentSpecimenId = sourceSpecimen.getId();
                asiList.add(asi);
            }
        }
        List<AliquotedSpecimenResInfo> resList = SessionManager.getAppService()
            .doAction(
                new SpecimenLinkSaveAction(SessionManager.getUser()
                    .getCurrentWorkingCenter().getId(),
                    linkFormPatientManagement.getCurrentPatient().getStudy()
                        .getId(), asiList)).getList();
        printSaveMultipleLogMessage(resList);
    }

    protected void printSaveMultipleLogMessage(
        List<AliquotedSpecimenResInfo> resList) {
        log.debug("printSaveMultipleLogMessage");

        StringBuffer sb = new StringBuffer(
            "ALIQUOTED SPECIMENS:\n");
        for (AliquotedSpecimenResInfo resInfo : resList) {
            sb.append(MessageFormat
                .format(
                    "LINKED: ''{0}'' with type ''{1}'' to source: {2} ({3}) - Patient: {4} - Visit: {5} - Center: {6}\n",
                    resInfo.inventoryId, resInfo.typeName,
                    resInfo.parentTypeName,
                    resInfo.parentInventoryId, resInfo.patientPNumber,
                    resInfo.visitNumber, resInfo.currentCenterName));
        }
        // Want only one common 'log entry' so use a stringbuffer to print
        // everything together
        appendLog(sb.toString());

        // LINKING\: {0} specimens linked to patient {1} on center {2}
        appendLog(MessageFormat.format(
            "LINKING: {0} specimens linked to patient {1} on center {2}",
            resList
                .size(), linkFormPatientManagement.getCurrentPatient()
                .getPnumber(), SessionManager.getUser()
                .getCurrentWorkingCenter().getNameShort()));
    }

    private void saveSingleSpecimen() throws Exception {
        log.debug("saveSingleSpecimen");

        AliquotedSpecimenInfo asi = new AliquotedSpecimenInfo();
        asi.activityStatus = ActivityStatus.ACTIVE;
        asi.typeId = singleSpecimen.getSpecimenType().getId();
        if (singleSpecimen.getParentContainer() != null) {
            asi.containerId = singleSpecimen.getParentContainer().getId();
            asi.position = singleSpecimen.getPosition();
        }
        asi.inventoryId = singleSpecimen.getInventoryId();
        asi.parentSpecimenId = singleSpecimen.getParentSpecimen().getId();

        List<AliquotedSpecimenResInfo> resList = SessionManager.getAppService()
            .doAction(
                new SpecimenLinkSaveAction(SessionManager.getUser()
                    .getCurrentWorkingCenter().getId(),
                    linkFormPatientManagement.getCurrentPatient().getStudy()
                        .getId(), Arrays.asList(asi))).getList();
        printSaveSingleLogMessage(resList);
    }

    protected void printSaveSingleLogMessage(
        List<AliquotedSpecimenResInfo> resList) {
        log.debug("printSaveSingleLogMessage");

        if (resList.size() == 1) {
            AliquotedSpecimenResInfo resInfo = resList.get(0);
            String posStr = resInfo.position;
            if (posStr == null) {
                posStr = "none";
            }
            appendLog(MessageFormat
                .format(
                    "LINKED: ''{0}'' with type ''{1}'' to source: {2} ({3}) - Patient: {4} - Visit: {5} - Center: {6} - Position: {7}\n",
                    resInfo.inventoryId, resInfo.typeName,
                    resInfo.parentInventoryId, resInfo.parentTypeName,
                    resInfo.patientPNumber, resInfo.visitNumber,
                    resInfo.currentCenterName, posStr));
        } else {
            throw new RuntimeException("Result size incorrect");
        }
    }

    @Override
    public void setValues() throws Exception {
        log.debug("setValues");

        super.setValues();
        if (isSingleMode())
            singleTypesWidget.deselectAll();
        showOnlyPallet(true);
        form.layout(true, true);
    }

    @Override
    public void reset(boolean resetAll) {
        log.debug("reset: " + resetAll);

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
        log.debug("onClose");
        linkFormPatientManagement.onClose();
        return super.onClose();
    }

    @Override
    /**
     * Multiple linking: do this before multiple scan is made
     */
    protected void beforeScanThreadStart() {
        log.debug("beforeScanThreadStart");
        super.beforeScanThreadStart();
        setTypeCombos();
        beforeScans(true);
    }

    @Override
    /**
     * Multiple linking: do this before scan of one tube is really made
     */
    protected void beforeScanTubeAlone() {
        log.debug("beforeScanTubeAlone");
        super.beforeScanTubeAlone();
        beforeScans(false);
    }

    /**
     * Multiple linking: do this before any scan is really launched
     */
    private void beforeScans(boolean resetTypeRows) {
        log.debug("beforeScans: " + resetTypeRows);
        preSelections = new ArrayList<SpecimenHierarchyInfo>();
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
                SessionManager.getAppService(), SessionManager.getUser()
                    .getCurrentWorkingCenter().getId());
        } catch (Exception ex) {
            BgcPlugin.openAsyncError("Fake Scan problem", ex);
        }
        return null;
    }

    @Override
    protected Action<ProcessResult> getCellProcessAction(Integer centerId,
        CellInfo cell, Locale locale) {
        log.debug("getCellProcessAction");
        return new SpecimenLinkProcessAction(centerId,
            linkFormPatientManagement
                .getCurrentPatient().getStudy().getId(), cell, locale);
    }

    @Override
    protected Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, boolean isRescanMode,
        Locale locale) {
        log.debug("getPalletProcessAction");
        return new SpecimenLinkProcessAction(centerId,
            linkFormPatientManagement
                .getCurrentPatient().getStudy().getId(), cells, isRescanMode,
            locale);
    }

    @Override
    /**
     *  Multiple linking: do this after multiple scan has been launched
     *  @param rowToProcess is null if scanning everything, is the current row if is scanning a single cell 
     */
    protected void afterScanAndProcess(final Integer rowToProcess) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                log.debug("afterScanAndProcess: asyncExec");

                // enabled the hierarchy combos
                typesSelectionPerRowComposite
                    .setEnabled(currentScanState != UICellStatus.ERROR);
                // set the combos lists
                if (typesRows.size() > 0)
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (rowToProcess == null) {
                                if (typesRows != null)
                                    for (int row = 0; row < specimenTypesWidgets
                                        .size(); row++)
                                        setCountOnSpecimenWidget(typesRows, row);
                            } else
                                setCountOnSpecimenWidget(typesRows,
                                    rowToProcess);
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
        log.debug("processCellResult");
        Integer typesRowsCount = typesRows.get(rcp.getRow());
        if (typesRowsCount == null) {
            typesRowsCount = 0;
            specimenTypesWidgets.get(rcp.getRow()).resetValues(!isRescanMode(),
                true, true);
        }
        SpecimenHierarchyInfo selection = preSelections.get(cell.getRow());
        if (selection != null)
            setHierarchyToCell(cell, selection);
        if (PalletCell.hasValue(cell)) {
            typesRowsCount++;
            typesRows.put(rcp.getRow(), typesRowsCount);
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
        log.debug("setCountOnSpecimenWidget");
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
    private void setHierarchyToCell(PalletCell cell,
        SpecimenHierarchyInfo selection) {
        log.debug("setHierarchyToCell");
        cell.setSourceSpecimen(selection.getParentSpecimen());
        cell.setSpecimenType(selection.getAliquotedSpecimenType());
        if (cell.getStatus() != UICellStatus.ERROR)
            cell.setStatus(UICellStatus.TYPE);
    }

    @Override
    protected Mode initialisationMode() {
        log.debug("initialisationMode: " + mode);
        return mode;
    }

    @Override
    protected void enableFields(boolean enable) {
        log.debug("enableFields: " + enable);
        super.enableFields(enable);
        multipleOptionsFields.setEnabled(enable);
    }

}
