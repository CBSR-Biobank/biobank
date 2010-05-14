package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.widgets.SampleTypeSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.grids.ScanLinkPalletWidget;
import edu.ualberta.med.scannerconfig.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Link aliquots to a patient visit
 */
public class ScanLinkEntryForm extends AbstractPalletAliquotAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanLinkEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ScanLinkEntryForm.class.getName());

    private LinkFormPatientManagement linkFormPatientManagement;

    private ScanLinkPalletWidget spw;

    // choose selection mode - deactivated by default
    private Composite radioComponents;

    // select per row
    private Composite typesSelectionPerRowComposite;
    private List<SampleTypeSelectionWidget> sampleTypeWidgets;

    // custom selection with mouse
    private Composite typesSelectionCustomComposite;
    private SampleTypeSelectionWidget customSelectionWidget;

    // should be set to true when all scanned aliquots have a type set
    private IObservableValue typesFilledValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    // button to choose a fake scan - debug only
    private Button fakeScanRandom;

    // sampleTypes for containers of type that contains 'palletNameContains'
    private List<SampleTypeWrapper> authorizedSampleTypes;

    private Composite fieldsComposite;

    private boolean isFakeScanRandom;

    @Override
    protected void init() {
        super.init();
        setPartName(Messages.getString("ScanLink.tabTitle")); //$NON-NLS-1$
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
    }

    @Override
    protected String getOkMessage() {
        return Messages.getString("ScanLink.okMessage"); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ScanLink.form.title")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsComposite();

        createPalletSection();

        createCancelConfirmWidget();

        SampleTypeSelectionWidget lastWidget = sampleTypeWidgets
            .get(sampleTypeWidgets.size() - 1);
        lastWidget.setNextWidget(getCancelConfirmWidget());

        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            typesFilledValue, Messages
                .getString("ScanLink.sampleType.select.validationMsg"));
    }

    /**
     * Pallet visualisation
     */
    private void createPalletSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);

        spw = new ScanLinkPalletWidget(client);
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
        IPreferenceStore store = BioBankPlugin.getDefault()
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
        createTypeSelectionPerRowComposite(selectionComp, authorizedSampleTypes);
        createTypeSelectionCustom(selectionComp, authorizedSampleTypes);
        radioRowSelection.setSelection(true);
        selectionStackLayout.topControl = typesSelectionPerRowComposite;

        radioRowSelection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioRowSelection.getSelection()) {
                    selectionStackLayout.topControl = typesSelectionPerRowComposite;
                    selectionComp.layout();
                    for (SampleTypeSelectionWidget sampleType : sampleTypeWidgets) {
                        sampleType.addBinding(widgetCreator);
                        sampleType.resetValues(false);
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
                    for (SampleTypeSelectionWidget sampleType : sampleTypeWidgets) {
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
                                    pCell.setStatus(AliquotCellStatus.NO_TYPE);
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
        authorizedSampleTypes = SampleTypeWrapper
            .getSampleTypeForContainerTypes(appService, SessionManager
                .getInstance().getCurrentSite(), palletNameContains);
        if (authorizedSampleTypes.size() == 0) {
            BioBankPlugin.openAsyncError(Messages
                .getString("ScanLink.dialog.sampleTypesError.title"), //$NON-NLS-1$
                Messages.getFormattedString(
                    "ScanLink.dialog.sampleTypesError.msg", //$NON-NLS-1$
                    palletNameContains));
        }
    }

    /**
     * Give a sample type to selected aliquots
     */
    private void createTypeSelectionCustom(Composite parent,
        List<SampleTypeWrapper> sampleTypes) {
        typesSelectionCustomComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        typesSelectionCustomComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionCustomComposite);

        Label label = toolkit.createLabel(typesSelectionCustomComposite,
            Messages.getString("ScanLink.custom.type.label")); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        label.setLayoutData(gd);

        customSelectionWidget = new SampleTypeSelectionWidget(
            typesSelectionCustomComposite, null, sampleTypes, toolkit);
        customSelectionWidget.resetValues(true);

        Button applyType = toolkit.createButton(typesSelectionCustomComposite,
            "Apply", SWT.PUSH); //$NON-NLS-1$
        applyType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SampleTypeWrapper type = customSelectionWidget.getSelection();
                if (type != null) {
                    for (Cell cell : spw.getMultiSelectionManager()
                        .getSelectedCells()) {
                        PalletCell pCell = (PalletCell) cell;
                        pCell.setType(type);
                        pCell.setStatus(AliquotCellStatus.TYPE);
                    }
                    spw.getMultiSelectionManager().clearMultiSelection();
                    customSelectionWidget.resetValues(true);
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
        List<SampleTypeWrapper> sampleTypes) {
        typesSelectionPerRowComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        typesSelectionPerRowComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionPerRowComposite);

        sampleTypeWidgets = new ArrayList<SampleTypeSelectionWidget>();
        SampleTypeSelectionWidget precedent = null;
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final SampleTypeSelectionWidget typeWidget = new SampleTypeSelectionWidget(
                typesSelectionPerRowComposite,
                LabelingScheme.SBS_ROW_LABELLING_PATTERN.charAt(i),
                sampleTypes, toolkit);
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
        Composite leftSideComposite = toolkit.createComposite(form.getBody());
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

        linkFormPatientManagement.createPatientNumberText(fieldsComposite);

        linkFormPatientManagement.createVisitCombo(fieldsComposite);

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
    protected void saveUINeededInformation() {
        super.saveUINeededInformation();
        if (fakeScanRandom != null) {
            isFakeScanRandom = fakeScanRandom.getSelection();
        }
    }

    @Override
    protected void scanAndProcessResult(IProgressMonitor monitor)
        throws Exception {
        launchScan(monitor);
        final boolean everythingOk = processScanResult(monitor);
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                typesSelectionPerRowComposite.setEnabled(everythingOk);
                // Show result in grid
                spw.setCells(cells);
                setRescanMode();
                form.layout(true, true);
            }
        });
    }

    @Override
    protected void launchFakeScan() {
        if (isFakeScanRandom) {
            cells = PalletCell.getRandomScanLink();
        } else {
            try {
                cells = PalletCell.getRandomScanLinkWithAliquotsAlreadyLinked(
                    appService, SessionManager.getInstance().getCurrentSite()
                        .getId());
            } catch (Exception ex) {
                BioBankPlugin.openAsyncError("Fake Scan problem", ex); //$NON-NLS-1$
            }
        }
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    private boolean processScanResult(IProgressMonitor monitor)
        throws ApplicationException {
        boolean everythingOk = true;
        final Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
        for (RowColPos rcp : cells.keySet()) {
            monitor.subTask("Processing position "
                + LabelingScheme.rowColToSbs(rcp));
            Integer typesRowsCount = typesRows.get(rcp.row);
            if (typesRowsCount == null) {
                typesRowsCount = 0;
                sampleTypeWidgets.get(rcp.row).resetValues(true, true);
            }
            PalletCell cell = null;
            cell = cells.get(rcp);
            if (!isRescanMode()
                || (cell != null && cell.getStatus() != AliquotCellStatus.TYPE && cell
                    .getStatus() != AliquotCellStatus.NO_TYPE)) {
                processCellStatus(cell);
            }
            everythingOk = cell.getStatus() != AliquotCellStatus.ERROR
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
        return everythingOk;
    }

    /**
     * Get sample types only defined in the patient's study. Then set these
     * types to the types combos
     */
    private void setTypeCombosLists(Map<Integer, Integer> typesRows) {
        List<SampleTypeWrapper> studiesSampleTypes = null;
        if (!isRescanMode()) { // already done at first scan
            studiesSampleTypes = new ArrayList<SampleTypeWrapper>();
            for (SampleStorageWrapper ss : linkFormPatientManagement
                .getCurrentPatient().getStudy().getSampleStorageCollection()) {
                if (ss.getActivityStatus().isActive()) {
                    SampleTypeWrapper type = ss.getSampleType();
                    if (authorizedSampleTypes.contains(type)) {
                        studiesSampleTypes.add(type);
                    }
                }
            }
            if (studiesSampleTypes.size() == 0) {
                BioBankPlugin.openAsyncError("No sample types",
                    "There are no sample types that are defined in study '"
                        + linkFormPatientManagement.getCurrentPatient()
                            .getStudy().getNameShort()
                        + "' and that are types possibles inside a pallet.");
            }
        }
        for (Integer row : typesRows.keySet()) {
            SampleTypeSelectionWidget widget = sampleTypeWidgets.get(row);
            widget.setNumber(typesRows.get(row));
            if (!isRescanMode()) {
                widget.setTypes(studiesSampleTypes);
            }
        }
    }

    private void processCellStatus(PalletCell cell) throws ApplicationException {
        processCellStatus(cell, false);
    }

    /**
     * Process the cell: apply a status and set correct information
     */
    private void processCellStatus(PalletCell cell, boolean independantProcess)
        throws ApplicationException {
        if (cell != null) {
            String value = cell.getValue();
            if (value != null) {
                List<AliquotWrapper> aliquots = AliquotWrapper
                    .getAliquotsInSite(appService, value, SessionManager
                        .getInstance().getCurrentSite());
                if (aliquots.size() > 0) {
                    cell.setStatus(AliquotCellStatus.ERROR);
                    cell
                        .setInformation(Messages
                            .getString("ScanLink.scanStatus.aliquot.alreadyExists")); //$NON-NLS-1$
                    AliquotWrapper aliquot = aliquots.get(0);
                    String palletPosition = LabelingScheme
                        .rowColToSbs(new RowColPos(cell.getRow(), cell.getCol()));
                    appendLogNLS("ScanLink.activitylog.aliquot.existsError",
                        palletPosition, value, aliquot.getPatientVisit()
                            .getFormattedDateProcessed(), aliquot
                            .getPatientVisit().getPatient().getPnumber());
                } else {
                    cell.setStatus(AliquotCellStatus.NO_TYPE);
                    if (independantProcess) {
                        SampleTypeSelectionWidget widget = sampleTypeWidgets
                            .get(cell.getRow());
                        widget.addOneToNumber();
                        SampleTypeWrapper type = widget.getSelection();
                        if (type != null) {
                            cell.setType(type);
                            cell.setStatus(AliquotCellStatus.TYPE);
                        }
                    }
                }
            } else {
                cell.setStatus(AliquotCellStatus.EMPTY);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void saveForm() throws Exception {
        Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) spw
            .getCells();
        PatientVisitWrapper patientVisit = linkFormPatientManagement
            .getSelectedPatientVisit();
        StringBuffer sb = new StringBuffer("ALIQUOTS LINKED:\n"); //$NON-NLS-1$
        int nber = 0;
        StudyWrapper study = patientVisit.getPatient().getStudy();
        List<SampleStorageWrapper> sampleStorages = study
            .getSampleStorageCollection();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == AliquotCellStatus.TYPE) {
                patientVisit.addNewAliquot(cell.getValue(), cell.getType(),
                    sampleStorages, ActivityStatusWrapper.getActivityStatus(
                        appService, "Active"));
                sb.append(Messages.getFormattedString(
                    "ScanLink.activitylog.aliquot.linked", //$NON-NLS-1$
                    cell.getValue(), patientVisit.getPatient().getPnumber(),
                    patientVisit.getFormattedDateProcessed(), patientVisit
                        .getShipment().getClinic().getName(), cell.getType()
                        .getName()));
                nber++;
            }
        }
        appendLog(sb.toString());
        appendLogNLS(
            "ScanLink.activitylog.save.summary", nber, patientVisit.getFormattedDateProcessed()); //$NON-NLS-1$ 
        setFinished(false);
    }

    /**
     * update sample type of aliquots of one given row
     */
    @SuppressWarnings("unchecked")
    private void updateRowType(SampleTypeSelectionWidget typeWidget,
        int indexRow) {
        if (typeWidget.needToSave()) {
            SampleTypeWrapper type = typeWidget.getSelection();
            if (type != null) {
                Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) spw
                    .getCells();
                if (cells != null) {
                    for (RowColPos rcp : cells.keySet()) {
                        if (rcp.row == indexRow) {
                            PalletCell cell = cells.get(rcp);
                            if (PalletCell.hasValue(cell)) {
                                cell.setType(type);
                                cell.setStatus(AliquotCellStatus.TYPE);
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
        setDirty(false);
        reset(true);
        fieldsComposite.setEnabled(true);
    }

    public void reset(boolean resetAll) {
        linkFormPatientManagement.reset(resetAll);
        getCancelConfirmWidget().reset();
        removeRescanMode();
        setScanHasBeenLauched(false);
        setScanNotLauched();
        if (resetAll) {
            resetPlateToScan();
            spw.setCells(null);
            for (SampleTypeSelectionWidget stw : sampleTypeWidgets) {
                stw.resetValues(true);
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
        processCellStatus(cell, true);
        spw.redraw();
        form.layout();
    }
}
