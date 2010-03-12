package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.springframework.remoting.RemoteConnectFailureException;

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
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.SampleTypeSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.grids.ScanLinkPalletWidget;
import edu.ualberta.med.scanlib.ScanCell;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Link samples to a patient visit
 */
public class ScanLinkEntryForm extends AbstractAliquotAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanLinkEntryForm";

    private Button scanButton;

    private Composite typesSelectionPerRowComposite;

    private ScanLinkPalletWidget spw;

    private List<SampleTypeSelectionWidget> sampleTypeWidgets;

    private IObservableValue patientNumberValue = new WritableValue("",
        String.class);
    private IObservableValue plateToScanValue = new WritableValue("",
        String.class);
    private IObservableValue scannedValue = new WritableValue(Boolean.FALSE,
        Boolean.class);
    private IObservableValue scanOkValue = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private IObservableValue typesFilledValue = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private boolean scanOk;

    private Text patientNumberText;
    private Text plateToScanText;
    private ComboViewer viewerVisits;

    private CancelConfirmWidget cancelConfirmWidget;

    private Composite typesSelectionCustomComposite;

    private SampleTypeSelectionWidget customSelection;

    private Composite radioComponents;

    private PatientWrapper currentPatient;

    private String palletNameContains;

    private Button randomScan;

    private Button existsScan;

    @Override
    protected void init() {
        super.init();
        setPartName("Scan Link");
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        palletNameContains = store
            .getString(PreferenceConstants.PALLET_SCAN_CONTAINER_NAME_CONTAINS);
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
            if (!BioBankPlugin.getDefault().isValidPlateBarcode(
                plateToScanText.getText())) {
                scanButton.setEnabled(false);
            } else {
                scanButton.setEnabled(true);
            }
        }
    }

    @Override
    protected String getOkMessage() {
        return "Adding samples.";
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Link samples to patient visit using the scanner");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsComposite();

        UpdateValueStrategy uvs = new UpdateValueStrategy();

        uvs = new UpdateValueStrategy();
        uvs.setAfterConvertValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error("Scanner should be launched");
                } else {
                    return Status.OK_STATUS;
                }
            }
        });
        bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
            scannedValue, uvs, uvs);
        scannedValue.setValue(false);

        uvs.setAfterConvertValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error("Errors in scan !");
                } else {
                    return Status.OK_STATUS;
                }
            }
        });
        bindValue(new WritableValue(Boolean.FALSE, Boolean.class), scanOkValue,
            uvs, uvs);
        scanOkValue.setValue(false);

        createPalletSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);
        SampleTypeSelectionWidget lastWidget = sampleTypeWidgets
            .get(sampleTypeWidgets.size() - 1);
        lastWidget.setNextWidget(cancelConfirmWidget);

        uvs = new UpdateValueStrategy();
        uvs.setAfterConvertValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus
                        .error("Give a type to each aliquot");
                } else {
                    return Status.OK_STATUS;
                }
            }

        });
        bindValue(new WritableValue(Boolean.TRUE, Boolean.class),
            typesFilledValue, uvs, uvs);
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
            "Row choice", SWT.RADIO);
        final Button radioCustomSelection = toolkit.createButton(
            radioComponents, "Custom Selection choice", SWT.RADIO);
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

        List<SampleTypeWrapper> sampleTypes = SampleTypeWrapper
            .getSampleTypeForContainerTypes(appService, SessionManager
                .getInstance().getCurrentSite(), palletNameContains);
        if (sampleTypes.size() == 0) {
            BioBankPlugin.openAsyncError("Aliquot Types",
                "No sample type found for containers of container type containing '"
                    + palletNameContains + "'...");
        }
        createTypeSelectionPerRowComposite(selectionComp, sampleTypes);
        createTypeSelectionCustom(selectionComp, sampleTypes);
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

    /**
     * Give a sample type to selected samples
     */
    private void createTypeSelectionCustom(Composite parent,
        List<SampleTypeWrapper> sampleTypes) {
        typesSelectionCustomComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        typesSelectionCustomComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionCustomComposite);

        Label label = toolkit.createLabel(typesSelectionCustomComposite,
            "Choose type for selected samples:");
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        label.setLayoutData(gd);

        customSelection = new SampleTypeSelectionWidget(
            typesSelectionCustomComposite, null, sampleTypes, toolkit);
        customSelection.resetValues(true);

        Button applyType = toolkit.createButton(typesSelectionCustomComposite,
            "Apply", SWT.PUSH);
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
                        setTypeForRow(typeWidget, indexRow);
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
        Composite fieldsComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        patientNumberText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Patient Number", new String[0],
            patientNumberValue, new NonEmptyStringValidator(
                "Enter a patient number"));
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setVisitsList();
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                reset(false);
            }
        });
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        firstControl = patientNumberText;

        createVisitCombo(fieldsComposite);

        plateToScanText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Plate to Scan", new String[0],
            plateToScanValue, new ScannerBarcodeValidator(
                "Enter a valid plate barcode"));
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    scan();
                }
            }
        });

        String scanButtonTitle = "Launch scan";
        if (!BioBankPlugin.isRealScanEnabled()) {
            gd.widthHint = 400;
            Composite comp = toolkit.createComposite(fieldsComposite);
            comp.setLayout(new GridLayout());
            gd = new GridData();
            gd.horizontalSpan = 2;
            comp.setLayoutData(gd);
            randomScan = toolkit.createButton(comp, "Get random scan values",
                SWT.RADIO);
            randomScan.setSelection(true);
            existsScan = toolkit.createButton(comp,
                "Get random and already linked samples", SWT.RADIO);
            scanButtonTitle = "Fake scan";
        }
        scanButton = toolkit.createButton(fieldsComposite, scanButtonTitle,
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        scanButton.setLayoutData(gd);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scan();
            }
        });

        createTypesSelectionSection(fieldsComposite);
    }

    private void createVisitCombo(Composite compositeFields) {
        viewerVisits = createComboViewerWithNoSelectionValidator(
            compositeFields, "Visit date processed", null, null,
            "A visit should be selected");
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerVisits.getCombo().setLayoutData(gridData);

        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisitWrapper pv = (PatientVisitWrapper) element;
                return pv.getFormattedDateProcessed() + " - "
                    + pv.getShipment().getWaybill();
            }
        });
        viewerVisits.getCombo().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 13) {
                    plateToScanText.setFocus();
                    e.doit = false;
                }
            }
        });
        viewerVisits.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                PatientVisitWrapper pv = getSelectedPatientVisit();
                if (pv != null) {
                    appendLog("Visit selected "
                        + pv.getFormattedDateProcessed() + " - "
                        + pv.getShipment().getClinic().getName());
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
            BioBankPlugin.openError("Error getting the patient", e);
        }
        if (currentPatient != null) {
            appendLog("-----");
            appendLog("Found patient with number "
                + currentPatient.getPnumber());
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

    private void scan() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    scanOk = true;
                    Map<RowColPos, PalletCell> cells = null;
                    appendLog("----");
                    appendLog("Scanning plate "
                        + plateToScanValue.getValue().toString());
                    int plateNum = BioBankPlugin.getDefault().getPlateNumber(
                        plateToScanValue.getValue().toString());
                    if (BioBankPlugin.isRealScanEnabled()) {
                        cells = PalletCell.convertArray(ScannerConfigPlugin
                            .scan(plateNum));
                    } else {
                        if (randomScan.getSelection()) {
                            cells = PalletCell.getRandomScanLink();
                        } else if (existsScan.getSelection()) {
                            cells = PalletCell
                                .getRandomScanLinkWithSamplesAlreadyLinked(
                                    appService, SessionManager.getInstance()
                                        .getCurrentSite().getId());
                        }
                    }
                    scannedValue.setValue(true);
                    radioComponents.setEnabled(true);
                    Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
                    for (RowColPos rcp : cells.keySet()) {
                        Integer typesRowsCount = typesRows.get(rcp.row);
                        if (typesRowsCount == null) {
                            typesRowsCount = 0;
                            sampleTypeWidgets.get(rcp.row).resetValues(true);
                        }
                        boolean addSampleNumber = setCellStatus(cells.get(rcp));
                        if (addSampleNumber) {
                            typesRowsCount++;
                            typesRows.put(rcp.row, typesRowsCount);
                        }
                    }
                    for (Integer row : typesRows.keySet()) {
                        sampleTypeWidgets.get(row)
                            .setNumber(typesRows.get(row));
                    }
                    scanOkValue.setValue(scanOk);
                    radioComponents.setEnabled(scanOk);
                    typesSelectionPerRowComposite.setEnabled(scanOk);

                    // Show result in grid
                    spw.setCells(cells);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    BioBankPlugin.openError("Error while scanning", e);
                }
            }
        });
    }

    private boolean setCellStatus(PalletCell cell) throws ApplicationException {
        if (cell != null) {
            String value = cell.getValue();
            if (value != null) {
                List<AliquotWrapper> samples = AliquotWrapper.getAliquotsInSite(
                    appService, value, SessionManager.getInstance()
                        .getCurrentSite());
                if (samples.size() > 0) {
                    cell.setStatus(AliquotCellStatus.ERROR);
                    String msg = "Aliquot already in database";
                    cell.setInformation(msg);
                    scanOk = false;
                    AliquotWrapper aliquot = samples.get(0);
                    appendLog("ERROR: " + value + " - " + msg + " see visit "
                        + aliquot.getPatientVisit().getFormattedDateProcessed()
                        + " of patient "
                        + aliquot.getPatientVisit().getPatient().getPnumber());
                } else {
                    cell.setStatus(AliquotCellStatus.NO_TYPE);
                }
                return true;
            } else {
                cell.setStatus(AliquotCellStatus.EMPTY);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void saveForm() throws Exception {
        Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) spw
            .getCells();
        PatientVisitWrapper patientVisit = getSelectedPatientVisit();
        StringBuffer sb = new StringBuffer("ALIQUOTS LINKED:");
        int nber = 0;
        StudyWrapper study = patientVisit.getPatient().getStudy();
        List<SampleStorageWrapper> sampleStorages = study
            .getSampleStorageCollection();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == AliquotCellStatus.TYPE) {
                patientVisit.addNewAliquot(cell.getValue(), cell.getType(),
                    sampleStorages);
                appendSampleLogMessage(sb, patientVisit, cell.getValue(), cell
                    .getType());
                nber++;
            }
        }
        appendLog("----");
        appendLog(sb.toString());
        appendLog("SCAN-LINK: " + nber + " samples linked to visit");
        setSaved(true);
    }

    private void appendSampleLogMessage(StringBuffer sb,
        PatientVisitWrapper patientVisit, String cellValue,
        SampleTypeWrapper cellType) {
        sb.append("\nLINKED: ").append(cellValue);
        sb.append(" - patient: ")
            .append(patientVisit.getPatient().getPnumber());
        sb.append(" - Visit: ")
            .append(patientVisit.getFormattedDateProcessed());
        sb.append(" - ").append(
            patientVisit.getShipment().getClinic().getName());
        sb.append(" - ").append(cellType.getName());
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
     * set the sample type to the samples of the given row
     */
    @SuppressWarnings("unchecked")
    private void setTypeForRow(SampleTypeSelectionWidget typeWidget,
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
    }

    public void reset(boolean resetAll) {
        viewerVisits.setInput(null);
        currentPatient = null;
        cancelConfirmWidget.reset();
        scanButton.setEnabled(false);
        scannedValue.setValue(Boolean.FALSE);
        if (resetAll) {
            patientNumberText.setText("");
            plateToScanText.setText("");
            plateToScanValue.setValue("");
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
        return "Scan link activity";
    }

}
