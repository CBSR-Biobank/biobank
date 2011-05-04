package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.BiobankPlugin;
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
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

// FIXME the custom selection is not done in this version. 
public class SpecimenLinkEntryForm extends AbstractLinkAssignEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenLinkEntryForm"; //$NON-NLS-1$

    private static final String INVENTORY_ID_BINDING = "inventoryId-binding"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SpecimenLinkEntryForm.class.getName());

    private static boolean singleMode = false;

    // TODO do not need a composite class anymore if only one link form is left
    private LinkFormPatientManagement linkFormPatientManagement;

    // single linking
    // source specimen / type relation when only one specimen
    private AliquotedSpecimenSelectionWidget singleTypesWidget;

    // Multiple linking
    private ScanPalletWidget palletWidget;
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
        return singleMode;
    }

    @Override
    protected void setSingleMode(boolean single) {
        singleMode = single;
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
    protected void createContainersVisualisation(Composite parent) {
        Composite comp = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        comp.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        comp.setLayoutData(gd);

        palletWidget = new ScanPalletWidget(comp,
            UICellStatus.DEFAULT_PALLET_SCAN_LINK_STATUS_LIST);
        toolkit.adapt(palletWidget);
        palletWidget.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true,
            false));

        palletWidget.getMultiSelectionManager().addMultiSelectionListener(
            new MultiSelectionListener() {
                @Override
                public void selectionChanged(MultiSelectionEvent mse) {
                    // customSelectionWidget.setNumber(mse.selections);
                }
            });
        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                scanTubeAlone(e);
            }
        });
        // palletWidget.loadProfile(profilesCombo.getCombo().getText());

        createScanTubeAloneButton(comp);
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

        // inventoryID
        BiobankText inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            fieldsComposite,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("SpecimenLink.inventoryId.label"), //$NON-NLS-1$
            new String[0],
            singleSpecimen,
            SpecimenPeer.INVENTORY_ID.getName(),
            new NonEmptyStringValidator(Messages
                .getString("SpecimenLink.inventoryId.validator.msg")), //$NON-NLS-1$
            INVENTORY_ID_BINDING);
        inventoryIdText.addKeyListener(textFieldKeyListener);

        // widget to select the source and the type
        singleTypesWidget = new AliquotedSpecimenSelectionWidget(
            fieldsComposite, null, widgetCreator, false);
        singleTypesWidget.addBindings();
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

    /**
     * Get types only defined in the patient's study. Then set these types to
     * the types combos
     */
    private void setTypeCombos() {
        List<SpecimenTypeWrapper> studiesAliquotedTypes = linkFormPatientManagement
            .getStudyAliquotedTypes(null, null);
        List<SpecimenWrapper> availableSourceSpecimens = linkFormPatientManagement
            .getParentSpecimenForPEventAndCEvent();
        if (isFirstSuccessfulScan())
            // for multiple
            for (int row = 0; row < specimenTypesWidgets.size(); row++) {
                AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                    .get(row);
                widget.setSourceSpecimens(availableSourceSpecimens);
                widget.setResultTypes(studiesAliquotedTypes);
            }
        // for single
        singleTypesWidget.setSourceSpecimens(availableSourceSpecimens);
        singleTypesWidget.setResultTypes(studiesAliquotedTypes);
    }

    @Override
    protected boolean fieldsValid() {
        return (singleMode ? true : isPlateValid())
            && linkFormPatientManagement.fieldsValid();
    }

    @Override
    protected void doBeforeSave() throws Exception {
        // can't access the combos in another thread, so do it now
        if (singleMode) {
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
        OriginInfoWrapper originInfo = new OriginInfoWrapper(
            SessionManager.getAppService());
        originInfo
            .setCenter(SessionManager.getUser().getCurrentWorkingCenter());
        originInfo.persist();
        if (singleMode)
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
            Messages
                .getString("SpecimenLink.activitylog.specimens.start")); //$NON-NLS-1$
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
            posStr = Messages
                .getString("SpecimenLink.position.label.none"); //$NON-NLS-1$
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
    public void reset() throws Exception {
        super.reset();
        linkFormPatientManagement.reset(true);
        singleTypesWidget.deselectAll();
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
            BiobankPlugin.openAsyncError("Fake Scan problem", ex); //$NON-NLS-1$
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
                                setCombosLists(typesRows);
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
     * Multiple linking: get types only defined in the patient's study. Then set
     * these types to the hierarchy combos
     */
    private void setCombosLists(Map<Integer, Integer> typesRows) {
        List<SpecimenTypeWrapper> studiesAliquotedTypes = null;
        if (isFirstSuccessfulScan()) {
            studiesAliquotedTypes = linkFormPatientManagement
                .getStudyAliquotedTypes(palletSpecimenTypes, null);
        }
        List<SpecimenWrapper> availableSourceSpecimens = linkFormPatientManagement
            .getParentSpecimenForPEventAndCEvent();
        // set the list of aliquoted types to all widgets, in case the list is
        // activated using the handheld scanner
        for (int row = 0; row < specimenTypesWidgets.size(); row++) {
            AliquotedSpecimenSelectionWidget widget = setCountOnSpecimenWidget(
                typesRows, row);
            if (isFirstSuccessfulScan()) {
                widget.setSourceSpecimens(availableSourceSpecimens);
                widget.setResultTypes(studiesAliquotedTypes);
            }
        }
    }

    /**
     * Multiple linking: apply a count on a specific row of 'hierarchy' widgets.
     * 
     * @param typesRows contains a row:count map
     * @param row row of the widget we want to update.
     */
    private AliquotedSpecimenSelectionWidget setCountOnSpecimenWidget(
        Map<Integer, Integer> typesRows, int row) {
        AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets.get(row);
        Integer number = typesRows.get(row);
        if (number == null) {
            number = 0;
            widget.deselectAll();
        }
        widget.setNumber(number);
        return widget;
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
    protected boolean initializeWithSingle() {
        return isSingleMode();
    }

    @Override
    protected void enableFields(boolean enable) {
        super.enableFields(enable);
        multipleOptionsFields.setEnabled(enable);
    }

}
