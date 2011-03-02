package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Link aliquots to a patient visit
 */
public class ScanLinkEntryForm extends AbstractPalletSpecimenAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanLinkEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ScanLinkEntryForm.class.getName());

    private LinkFormPatientManagement linkFormPatientManagement;

    private ScanPalletWidget spw;

    // choose selection mode - deactivated by default
    private Composite radioComponents;

    // select per row
    private Composite typesSelectionPerRowComposite;
    private List<AliquotedSpecimenSelectionWidget> sampleTypeWidgets;

    // custom selection with mouse
    private Composite typesSelectionCustomComposite;
    private AliquotedSpecimenSelectionWidget customSelectionWidget;

    // should be set to true when all scanned aliquots have a type set
    private IObservableValue typesFilledValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    // button to choose a fake scan - debug only
    private Button fakeScanRandom;

    @Deprecated
    private List<SpecimenTypeWrapper> sourceSpecimenTypes;

    // sampleTypes for containers of type that contains 'palletNameContains'
    private List<SpecimenTypeWrapper> authorizedSampleTypes;

    private Composite fieldsComposite;

    private boolean processScanResult;

    private boolean isFakeScanRandom;

    private ScrolledComposite containersScroll;

    private ArrayList<SpecimenTypeWrapper> preSelectedSampleTypes;

    private SiteWrapper currentSelectedSite;

    @Override
    protected void init() throws Exception {
        super.init();
        setPartName(Messages.getString("ScanLink.tabTitle")); //$NON-NLS-1$
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
        setCanLaunchScan(true);
    }

    @Override
    protected String getOkMessage() {
        return Messages.getString("ScanLink.okMessage"); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ScanLink.form.title")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);

        createFieldsComposite();

        createPalletSection();

        createCancelConfirmWidget();

        AliquotedSpecimenSelectionWidget lastWidget = sampleTypeWidgets
            .get(sampleTypeWidgets.size() - 1);
        lastWidget.setNextWidget(cancelConfirmWidget);

        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            typesFilledValue,
            Messages.getString("ScanLink.sampleType.select.validationMsg"));
    }

    /**
     * Pallet visualisation
     */
    private void createPalletSection() {
        containersScroll = new ScrolledComposite(page, SWT.H_SCROLL);
        containersScroll.setExpandHorizontal(true);
        containersScroll.setExpandVertical(true);
        containersScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        containersScroll.setLayoutData(scrollData);
        Composite client = toolkit.createComposite(containersScroll);
        GridLayout layout = new GridLayout(2, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);
        containersScroll.setContent(client);

        spw = new ScanPalletWidget(client,
            CellStatus.DEFAULT_PALLET_SCAN_LINK_STATUS_LIST);
        spw.setVisible(true);
        toolkit.adapt(spw);
        spw.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

        spw.getMultiSelectionManager().addMultiSelectionListener(
            new MultiSelectionListener() {
                @Override
                public void selectionChanged(MultiSelectionEvent mse) {
                    customSelectionWidget.setNumber(mse.selections);
                }
            });
        spw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                scanTubeAlone(e);
            }
        });
        spw.loadProfile(profilesCombo.getCombo().getText());

        createScanTubeAloneButton(client);

        containersScroll.setMinSize(client
            .computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * Aliquot types selection.
     */
    private void createTypesSelectionSection(Composite parent) throws Exception {
        // Radio buttons
        radioComponents = toolkit.createComposite(parent);
        RowLayout compLayout = new RowLayout();
        radioComponents.setLayout(compLayout);
        toolkit.paintBordersFor(radioComponents);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        radioComponents.setLayoutData(gd);
        radioComponents.setEnabled(false);

        // radio button to choose how the sample types are selected
        final Button radioRowSelection = toolkit.createButton(radioComponents,
            Messages.getString("ScanLink.rowChoice.label"), SWT.RADIO); //$NON-NLS-1$
        final Button radioCustomSelection = toolkit.createButton(
            radioComponents,
            Messages.getString("ScanLink.customChoice.label"), SWT.RADIO); //$NON-NLS-1$
        IPreferenceStore store = BiobankPlugin.getDefault()
            .getPreferenceStore();
        boolean hideRadio = store
            .getBoolean(PreferenceConstants.SCAN_LINK_ROW_SELECT_ONLY);
        radioComponents.setVisible(!hideRadio);

        // stackLayout
        final Composite selectionComp = toolkit.createComposite(parent);
        final StackLayout selectionStackLayout = new StackLayout();
        selectionComp.setLayout(selectionStackLayout);
        gd = new GridData();
        gd.horizontalSpan = 2;
        selectionComp.setLayoutData(gd);

        initAuthorizedSampleTypeList();
        createTypeSelectionPerRowComposite(selectionComp, sourceSpecimenTypes,
            authorizedSampleTypes);
        createTypeSelectionCustom(selectionComp, sourceSpecimenTypes,
            authorizedSampleTypes);
        radioRowSelection.setSelection(true);
        selectionStackLayout.topControl = typesSelectionPerRowComposite;

        radioRowSelection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioRowSelection.getSelection()) {
                    selectionStackLayout.topControl = typesSelectionPerRowComposite;
                    selectionComp.layout();
                    for (AliquotedSpecimenSelectionWidget sampleType : sampleTypeWidgets) {
                        sampleType.addBinding(widgetCreator);
                        sampleType.resetValues(true, false);
                    }
                    customSelectionWidget.addBinding(widgetCreator);
                    spw.getMultiSelectionManager().disableMultiSelection();
                    typesFilledValue.setValue(Boolean.TRUE);
                    spw.redraw();
                }
            }
        });
        radioCustomSelection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioCustomSelection.getSelection()) {
                    selectionStackLayout.topControl = typesSelectionCustomComposite;
                    selectionComp.layout();
                    for (AliquotedSpecimenSelectionWidget sampleType : sampleTypeWidgets) {
                        sampleType.removeBinding(widgetCreator);
                    }
                    customSelectionWidget.addBinding(widgetCreator);
                    spw.getMultiSelectionManager().enableMultiSelection(
                        new MultiSelectionSpecificBehaviour() {
                            @Override
                            public void removeSelection(Cell cell) {
                                PalletCell pCell = (PalletCell) cell;
                                if (pCell != null && pCell.getValue() != null) {
                                    pCell.setType(null);
                                    pCell.setStatus(CellStatus.NO_TYPE);
                                }
                            }

                            @Override
                            public boolean isSelectable(Cell cell) {
                                return ((PalletCell) cell).getValue() != null;
                            }
                        });
                    typesFilledValue.setValue(spw.isEverythingTyped());
                    spw.redraw();
                }
            }
        });
    }

    private void initAuthorizedSampleTypeList() throws ApplicationException {
        SiteWrapper currentSite = SessionManager.getUser()
            .getCurrentWorkingCentre();
        if (currentSite != null) {
            authorizedSampleTypes = SpecimenTypeWrapper
                .getSpecimenTypeForPallet96(appService, currentSite);
            if (authorizedSampleTypes.size() == 0) {
                BiobankPlugin.openAsyncError(Messages
                    .getString("ScanLink.dialog.sampleTypesError.title"), //$NON-NLS-1$
                    Messages.getString("ScanLink.dialog.sampleTypesError.msg")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Give a sample type to selected aliquots
     */
    private void createTypeSelectionCustom(Composite parent,
        List<SpecimenTypeWrapper> sourceSpecimenTypes,
        List<SpecimenTypeWrapper> resultSpecimenTypes) {
        typesSelectionCustomComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(4, false);
        typesSelectionCustomComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionCustomComposite);

        Label label = toolkit.createLabel(typesSelectionCustomComposite,
            Messages.getString("ScanLink.custom.type.label")); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        label.setLayoutData(gd);

        customSelectionWidget = new AliquotedSpecimenSelectionWidget(
            typesSelectionCustomComposite, null, sourceSpecimenTypes,
            resultSpecimenTypes, toolkit);
        customSelectionWidget.resetValues(true, true);

        Button applyType = toolkit.createButton(typesSelectionCustomComposite,
            "Apply", SWT.PUSH); //$NON-NLS-1$
        applyType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SpecimenTypeWrapper type = customSelectionWidget.getSelection();
                if (type != null) {
                    for (Cell cell : spw.getMultiSelectionManager()
                        .getSelectedCells()) {
                        PalletCell pCell = (PalletCell) cell;
                        pCell.setType(type);
                        pCell.setStatus(CellStatus.TYPE);
                    }
                    spw.getMultiSelectionManager().clearMultiSelection();
                    customSelectionWidget.resetValues(true, true);
                    typesFilledValue.setValue(spw.isEverythingTyped());
                    spw.redraw();
                }
            }
        });
    }

    /**
     * give sample type row per row (default)
     */
    private void createTypeSelectionPerRowComposite(Composite parent,
        List<SpecimenTypeWrapper> sourceSpecimenTypes,
        List<SpecimenTypeWrapper> resultSpecimenTypes) {
        typesSelectionPerRowComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        typesSelectionPerRowComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionPerRowComposite);
        GridData gd = new GridData();
        gd.widthHint = 500;
        typesSelectionPerRowComposite.setLayoutData(gd);

        toolkit.createLabel(typesSelectionPerRowComposite, "");
        toolkit.createLabel(typesSelectionPerRowComposite,
            "Source Specimen Types");
        toolkit.createLabel(typesSelectionPerRowComposite,
            "Aliquoted Specimen Types");
        toolkit.createLabel(typesSelectionPerRowComposite, "");

        sampleTypeWidgets = new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final AliquotedSpecimenSelectionWidget typeWidget = new AliquotedSpecimenSelectionWidget(
                typesSelectionPerRowComposite,
                ContainerLabelingSchemeWrapper.SBS_ROW_LABELLING_PATTERN
                    .charAt(i), sourceSpecimenTypes, resultSpecimenTypes,
                toolkit);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        updateRowType(typeWidget, indexRow);
                        if (spw.isEverythingTyped()) {
                            setDirty(true);
                        }
                    }

                });
            typeWidget.addBinding(widgetCreator);
            sampleTypeWidgets.add(typeWidget);
            if (precedent != null) {
                precedent.setNextWidget(typeWidget);
            }
            precedent = typeWidget;
        }
    }

    private void createFieldsComposite() throws Exception {
        Composite leftSideComposite = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        leftSideComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        leftSideComposite.setLayoutData(gd);
        toolkit.paintBordersFor(leftSideComposite);

        fieldsComposite = toolkit.createComposite(leftSideComposite);
        layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        gd = new GridData();
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.TOP;
        gd.horizontalSpan = 2;
        fieldsComposite.setLayoutData(gd);

        initAuthorizedSampleTypeList();

        linkFormPatientManagement.createPatientNumberText(fieldsComposite);
        linkFormPatientManagement.createCollectionEventWidgets(fieldsComposite);
        linkFormPatientManagement.createWorksheetText(fieldsComposite);

        createProfileComboBox(fieldsComposite);
        // specific for scan link:
        profilesCombo.getCombo().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                spw.loadProfile(profilesCombo.getCombo().getText());
            }
        });

        profilesCombo.getCombo().notifyListeners(666, new Event());

        createPlateToScanField(fieldsComposite);

        createScanButton(leftSideComposite);

        createTypesSelectionSection(leftSideComposite);

    }

    @Override
    protected void createFakeOptions(Composite fieldsComposite) {
        GridData gd;
        Composite comp = toolkit.createComposite(fieldsComposite);
        comp.setLayout(new GridLayout());
        gd = new GridData();
        gd.horizontalSpan = 3;
        gd.widthHint = 400;
        comp.setLayoutData(gd);
        fakeScanRandom = toolkit.createButton(comp, "Get random scan values", //$NON-NLS-1$
            SWT.RADIO);
        fakeScanRandom.setSelection(true);
        toolkit.createButton(comp,
            "Get random and already linked aliquots", SWT.RADIO); //$NON-NLS-1$
    }

    @Override
    protected void afterScanAndProcess() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                typesSelectionPerRowComposite.setEnabled(processScanResult);
                for (AliquotedSpecimenSelectionWidget typeWidget : sampleTypeWidgets) {
                    if (typeWidget.canFocus()) {
                        typeWidget.setFocus();
                        break;
                    }
                }
                // Show result in grid
                spw.setCells(getCells());
                setRescanMode();
                // not needed on windows. This was if the textfield number
                // go after 9, needed to resize on linux : need to check that
                // again
                // form.layout(true, true);
            }
        });
        setScanValid(processScanResult);
    }

    @Override
    protected void beforeScanThreadStart() {
        isFakeScanRandom = fakeScanRandom != null
            && fakeScanRandom.getSelection();
        currentSelectedSite = SessionManager.getUser()
            .getCurrentWorkingCentre();
        preSelectedSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        for (AliquotedSpecimenSelectionWidget stw : sampleTypeWidgets) {
            preSelectedSampleTypes.add(stw.getSelection());
        }
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        if (isFakeScanRandom) {
            return PalletCell.getRandomScanLink();
        }
        try {
            return PalletCell.getRandomScanLinkWithAliquotsAlreadyLinked(
                appService, currentSelectedSite.getId());
        } catch (Exception ex) {
            BiobankPlugin.openAsyncError("Fake Scan problem", ex); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        processScanResult = false;
        boolean everythingOk = true;
        Map<RowColPos, PalletCell> cells = getCells();
        if (cells != null) {
            final Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
            for (RowColPos rcp : cells.keySet()) {
                monitor.subTask("Processing position "
                    + ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                Integer typesRowsCount = typesRows.get(rcp.row);
                if (typesRowsCount == null) {
                    typesRowsCount = 0;
                    sampleTypeWidgets.get(rcp.row).resetValues(!isRescanMode(),
                        true, true);
                }
                PalletCell cell = null;
                cell = cells.get(rcp);
                if (!isRescanMode()
                    || (cell != null && cell.getStatus() != CellStatus.TYPE && cell
                        .getStatus() != CellStatus.NO_TYPE)) {
                    processCellStatus(cell, false);
                }
                everythingOk = cell.getStatus() != CellStatus.ERROR
                    && everythingOk;
                if (PalletCell.hasValue(cell)) {
                    typesRowsCount++;
                    typesRows.put(rcp.row, typesRowsCount);
                }
            }
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setTypeCombosLists(typesRows);
                }
            });
            processScanResult = everythingOk;
        }
    }

    /**
     * Get sample types only defined in the patient's study. Then set these
     * types to the types combos
     */
    private void setTypeCombosLists(Map<Integer, Integer> typesRows) {
        List<SpecimenTypeWrapper> studiesSampleTypes = null;
        if (isFirstSuccessfulScan()) {
            // done at first successful scan
            studiesSampleTypes = new ArrayList<SpecimenTypeWrapper>();
            for (AliquotedSpecimenWrapper ss : linkFormPatientManagement
                .getCurrentPatient().getStudy()
                .getAliquotedSpecimenCollection(true)) {
                if (ss.getActivityStatus().isActive()) {
                    SpecimenTypeWrapper type = ss.getSpecimenType();
                    if (authorizedSampleTypes.contains(type)) {
                        studiesSampleTypes.add(type);
                    }
                }
            }
            if (studiesSampleTypes.size() == 0) {
                BiobankPlugin.openAsyncError("No sample types",
                    "There are no sample types that are defined in study '"
                        + linkFormPatientManagement.getCurrentPatient()
                            .getStudy().getNameShort()
                        + "' and that are types possibles inside a pallet.");
            }
        }
        // set the list of sample type to all widget, in case the list is
        // activated using the handheld scanner
        for (int row = 0; row < sampleTypeWidgets.size(); row++) {
            AliquotedSpecimenSelectionWidget widget = sampleTypeWidgets
                .get(row);
            Integer number = typesRows.get(row);
            if (number != null)
                widget.setNumber(number);
            if (isFirstSuccessfulScan()) {
                widget.setTypes(studiesSampleTypes);
            }
        }
    }

    /**
     * Process the cell: apply a status and set correct information
     * 
     * @throws BiobankCheckException
     */
    private CellStatus processCellStatus(PalletCell cell,
        boolean independantProcess) throws ApplicationException,
        BiobankCheckException {
        if (cell == null) {
            return CellStatus.EMPTY;
        } else {
            String value = cell.getValue();
            if (value != null) {
                // FIXME test what happen if can't read site
                SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(
                    appService, value, SessionManager.getUser());
                if (foundAliquot != null) {
                    cell.setStatus(CellStatus.ERROR);
                    cell.setInformation(Messages
                        .getString("ScanLink.scanStatus.aliquot.alreadyExists")); //$NON-NLS-1$
                    String palletPosition = ContainerLabelingSchemeWrapper
                        .rowColToSbs(new RowColPos(cell.getRow(), cell.getCol()));
                    appendLogNLS("ScanLink.activitylog.aliquot.existsError",
                        palletPosition, value, foundAliquot
                            .getParentProcessingEvent()
                            .getFormattedDateProcessed(), foundAliquot
                            .getCollectionEvent().getPatient().getPnumber(),
                        foundAliquot.getCurrentCenter().getNameShort());
                } else {
                    cell.setStatus(CellStatus.NO_TYPE);
                    if (independantProcess) {
                        AliquotedSpecimenSelectionWidget widget = sampleTypeWidgets
                            .get(cell.getRow());
                        widget.increaseNumber();
                    }
                    SpecimenTypeWrapper type = preSelectedSampleTypes.get(cell
                        .getRow());
                    if (type != null) {
                        cell.setType(type);
                        cell.setStatus(CellStatus.TYPE);
                    }
                }
            } else {
                cell.setStatus(CellStatus.EMPTY);
            }
            return cell.getStatus();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void saveForm() throws Exception {
        Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) spw
            .getCells();
        CollectionEventWrapper collectionEvent = linkFormPatientManagement
            .getSelectedCollectionEvent();

        StringBuffer sb = new StringBuffer("ALIQUOTS LINKED:\n"); //$NON-NLS-1$
        int nber = 0;
        ActivityStatusWrapper activeStatus = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        List<SpecimenWrapper> newAliquots = new ArrayList<SpecimenWrapper>();
        SiteWrapper site = SessionManager.getUser().getCurrentWorkingCentre();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == CellStatus.TYPE) {
                SpecimenWrapper aliquot = new SpecimenWrapper(appService);
                aliquot.setInventoryId(cell.getValue());
                aliquot.setCreatedAt(new Date());
                aliquot.setSpecimenType(cell.getType());
                aliquot.setActivityStatus(activeStatus);
                newAliquots.add(aliquot);

                // FIXME find out correct messaging
                // sb.append(Messages.getString(
                //                    "ScanLink.activitylog.aliquot.linked", //$NON-NLS-1$
                // cell.getValue(), collectionEvent.getPatient().getPnumber(),
                // site.getNameShort(), collectionEvent
                // .getFormattedDateDrawn(), collectionEvent
                // .getCollectionEvent().getClinic().getName(), cell
                // .getType().getName()));
                nber++;
            }
        }

        // FIXME link source specimens to aliquotes specimens
        // collectionEvent.addAliquots(newAliquots);
        collectionEvent.persist();
        appendLog(sb.toString());

        // FIXME uncomment this log message after fixing
        // appendLogNLS("ScanLink.activitylog.save.summary", nber,
        // collectionEvent
        // .getPatient().getPnumber(), site.getNameShort(),
        // collectionEvent.getFormattedCreatedAt(),
        //            collectionEvent.getFormattedDateProcessed()); //$NON-NLS-1$

        setFinished(false);
    }

    /**
     * update sample type of aliquots of one given row
     */
    @SuppressWarnings("unchecked")
    private void updateRowType(AliquotedSpecimenSelectionWidget typeWidget,
        int indexRow) {
        if (typeWidget.needToSave()) {
            SpecimenTypeWrapper type = typeWidget.getSelection();
            if (type != null) {
                Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) spw
                    .getCells();
                if (cells != null) {
                    for (RowColPos rcp : cells.keySet()) {
                        if (rcp.row == indexRow) {
                            PalletCell cell = cells.get(rcp);
                            if (PalletCell.hasValue(cell)) {
                                cell.setType(type);
                                cell.setStatus(CellStatus.TYPE);
                            }
                        }
                    }
                    spw.redraw();
                }
            }
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        setDirty(false);
        fieldsComposite.setEnabled(true);
        setScanValid(true);
        reset(true);
    }

    public void reset(boolean resetAll) {
        linkFormPatientManagement.reset(resetAll);
        cancelConfirmWidget.reset();
        removeRescanMode();
        setScanHasBeenLauched(false);
        if (resetAll) {
            resetPlateToScan();
            spw.setCells(null);
            for (AliquotedSpecimenSelectionWidget stw : sampleTypeWidgets) {
                stw.resetValues(true, true);
            }
        }
        setFocus();
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected String getActivityTitle() {
        return "Scan link activity"; //$NON-NLS-1$
    }

    @Override
    protected void disableFields() {
        fieldsComposite.setEnabled(false);
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    public boolean onClose() {
        linkFormPatientManagement.onClose();
        return super.onClose();
    }

    @Override
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        CellStatus status = processCellStatus(cell, true);
        boolean ok = isScanValid() && (status != CellStatus.ERROR);
        setScanValid(ok);
        typesSelectionPerRowComposite.setEnabled(ok);
        spw.redraw();
        form.layout();
    }

    @Override
    protected boolean fieldsValid() {
        return isPlateValid() && linkFormPatientManagement.fieldsValid();
    }

}
