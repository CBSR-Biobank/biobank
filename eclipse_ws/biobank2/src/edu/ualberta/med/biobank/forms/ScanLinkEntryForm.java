package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.SampleTypeSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.grids.ScanLinkPalletWidget;
import edu.ualberta.med.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Link aliquots to a patient visit
 */
public class ScanLinkEntryForm extends AbstractPalletAliquotAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanLinkEntryForm"; //$NON-NLS-1$

    private ScanLinkPalletWidget spw;

    private Text patientNumberText;
    private ComboViewer viewerVisits;

    // choose selection mode - deactivated by default
    private Composite radioComponents;

    // select per row
    private Composite typesSelectionPerRowComposite;
    private List<SampleTypeSelectionWidget> sampleTypeWidgets;

    // custom selection with mouse
    private Composite typesSelectionCustomComposite;
    private SampleTypeSelectionWidget customSelection;

    // should be set to true when all scanned aliquots have a type set
    private IObservableValue typesFilledValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    // currentPatient
    private PatientWrapper currentPatient;

    // button to choose a fake scan - debug only
    private Button fakeScanRandom;
    private Button fakeScanExists;

    // sampleTypes for containers of type that contains 'palletNameContains'
    private List<SampleTypeWrapper> authorizedSampleTypes;

    private boolean patientNumberTextModified = false;

    private Composite fieldsComposite;

    @Override
    protected void init() {
        super.init();
        setPartName(Messages.getString("ScanLink.tabTitle")); //$NON-NLS-1$
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
                    customSelection.setNumber(mse.selections);
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
        gd.horizontalSpan = 2;
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
                    customSelection.addBinding(widgetCreator);
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
                    customSelection.addBinding(widgetCreator);
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

        customSelection = new SampleTypeSelectionWidget(
            typesSelectionCustomComposite, null, sampleTypes, toolkit);
        customSelection.resetValues(true);

        Button applyType = toolkit.createButton(typesSelectionCustomComposite,
            "Apply", SWT.PUSH); //$NON-NLS-1$
        applyType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SampleTypeWrapper type = customSelection.getSelection();
                if (type != null) {
                    for (Cell cell : spw.getMultiSelectionManager()
                        .getSelectedCells()) {
                        PalletCell pCell = (PalletCell) cell;
                        pCell.setType(type);
                        pCell.setStatus(AliquotCellStatus.TYPE);
                    }
                    spw.getMultiSelectionManager().clearMultiSelection();
                    customSelection.resetValues(true);
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
                        setDirty(true);
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
        leftSideComposite.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(leftSideComposite);

        fieldsComposite = toolkit.createComposite(leftSideComposite);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.TOP;
        gd.horizontalSpan = 2;
        fieldsComposite.setLayoutData(gd);

        patientNumberText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, Messages
                .getString("ScanLink.patientNumber.label"), new String[0], //$NON-NLS-1$
            new WritableValue("", String.class), new NonEmptyStringValidator( //$NON-NLS-1$
                Messages.getString("ScanLink.patientNumber.validationMsg"))); //$NON-NLS-1$
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (patientNumberTextModified) {
                    setVisitsList();
                }
                patientNumberTextModified = false;
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                patientNumberTextModified = true;
            }
        });
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        firstControl = patientNumberText;

        createVisitCombo(fieldsComposite);

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
        gd.horizontalSpan = 2;
        gd.widthHint = 400;
        comp.setLayoutData(gd);
        fakeScanRandom = toolkit.createButton(comp, "Get random scan values", //$NON-NLS-1$
            SWT.RADIO);
        fakeScanRandom.setSelection(true);
        fakeScanExists = toolkit.createButton(comp,
            "Get random and already linked aliquots", SWT.RADIO); //$NON-NLS-1$
    }

    private void createVisitCombo(Composite compositeFields) {
        viewerVisits = createComboViewerWithNoSelectionValidator(
            compositeFields,
            Messages.getString("ScanLink.visit.label"), null, null, //$NON-NLS-1$
            Messages.getString("ScanLink.visit.validationMsg")); //$NON-NLS-1$
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerVisits.getCombo().setLayoutData(gridData);

        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisitWrapper pv = (PatientVisitWrapper) element;
                return pv.getFormattedDateProcessed() + " - " //$NON-NLS-1$
                    + pv.getShipment().getWaybill();
            }
        });
        viewerVisits.getCombo().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 13) {
                    focusOnPlateToScanText();
                    e.doit = false;
                }
            }
        });
        viewerVisits.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                PatientVisitWrapper pv = getSelectedPatientVisit();
                if (pv != null) {
                    appendLogNLS("linkAssign.activitylog.visit.selection", pv //$NON-NLS-1$
                        .getFormattedDateProcessed(), pv.getShipment()
                        .getClinic().getName());
                }
            }
        });
    }

    protected void setVisitsList() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatientInSite(appService,
                patientNumberText.getText(), SessionManager.getInstance()
                    .getCurrentSite());
        } catch (ApplicationException e) {
            BioBankPlugin.openError(Messages
                .getString("ScanLink.dialog.patient.errorMsg"), e); //$NON-NLS-1$
        }
        if (currentPatient != null) {
            appendLog("--------");
            appendLogNLS("linkAssign.activitylog.patient", //$NON-NLS-1$
                currentPatient.getPnumber());
            // show visits list
            List<PatientVisitWrapper> collection = currentPatient
                .getPatientVisitCollection();
            viewerVisits.setInput(collection);
            viewerVisits.getCombo().setFocus();
            viewerVisits.getCombo().setListVisible(true);
        } else {
            viewerVisits.setInput(null);
        }
    }

    @Override
    protected void scanAndProcessResult(IProgressMonitor monitor)
        throws Exception {
        launchScan(monitor);
        radioComponents.setEnabled(true);
        boolean everythingOk = processScanResult(monitor);
        radioComponents.setEnabled(everythingOk);
        typesSelectionPerRowComposite.setEnabled(everythingOk);

        // Show result in grid
        spw.setCells(cells);
        setRescanMode();
    }

    @Override
    protected void launchFakeScan() throws Exception {
        if (fakeScanRandom.getSelection()) {
            cells = PalletCell.getRandomScanLink();
        } else if (fakeScanExists.getSelection()) {
            cells = PalletCell.getRandomScanLinkWithAliquotsAlreadyLinked(
                appService, SessionManager.getInstance().getCurrentSite()
                    .getId());
        }
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    private boolean processScanResult(IProgressMonitor monitor)
        throws ApplicationException {
        boolean everythingOk = true;
        Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
        for (RowColPos rcp : cells.keySet()) {
            monitor.subTask("Processing position "
                + LabelingScheme.rowColToSbs(rcp));
            Integer typesRowsCount = typesRows.get(rcp.row);
            if (typesRowsCount == null) {
                typesRowsCount = 0;
                sampleTypeWidgets.get(rcp.row).resetValues(true);
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
        setTypeCombosLists(typesRows);
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
            for (SampleStorageWrapper ss : currentPatient.getStudy()
                .getSampleStorageCollection()) {
                if (ss.getActivityStatus().isActive()) {
                    SampleTypeWrapper type = ss.getSampleType();
                    if (authorizedSampleTypes.contains(type)) {
                        studiesSampleTypes.add(type);
                    }
                }
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

    /**
     * Process the cell: apply a status and set correct information
     */
    private void processCellStatus(PalletCell cell) throws ApplicationException {
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
        PatientVisitWrapper patientVisit = getSelectedPatientVisit();
        StringBuffer sb = new StringBuffer("ALIQUOTS LINKED:\n"); //$NON-NLS-1$
        int nber = 0;
        StudyWrapper study = patientVisit.getPatient().getStudy();
        List<SampleStorageWrapper> sampleStorages = study
            .getSampleStorageCollection();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == AliquotCellStatus.TYPE) {
                patientVisit.addNewAliquot(cell.getValue(), cell.getType(),
                    sampleStorages);
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
        setSaved(true);
    }

    /**
     * get selected patient visit
     */
    private PatientVisitWrapper getSelectedPatientVisit() {
        if (viewerVisits.getSelection() != null
            && viewerVisits.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) viewerVisits
                .getSelection();
            if (selection.size() == 1)
                return (PatientVisitWrapper) selection.getFirstElement();
        }
        return null;
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
                                spw.redraw();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reset() throws Exception {
        reset(true);
        fieldsComposite.setEnabled(true);
    }

    public void reset(boolean resetAll) {
        viewerVisits.setInput(null);
        currentPatient = null;
        getCancelConfirmWidget().reset();
        removeRescanMode();
        enableScan(false);
        setScanNotLauched();
        if (resetAll) {
            patientNumberText.setText(""); //$NON-NLS-1$
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

}
