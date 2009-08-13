package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.AddSamplesScanPalletWidget;
import edu.ualberta.med.biobank.widgets.SampleTypeSelectionWidget;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationEvent;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationListener;
import edu.ualberta.med.scanlib.ScanCell;
import edu.ualberta.med.scanlib.ScanLib;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class ScanLinkEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanLinkEntryForm";

    private Button scanButton;

    private PatientVisit patientVisit;

    private Composite typesSelectionPerRowComposite;

    private AddSamplesScanPalletWidget spw;

    private List<SampleTypeSelectionWidget> sampleTypeWidgets;

    private IObservableValue patientNumberValue = new WritableValue("",
        String.class);
    private IObservableValue visitSelectionValue = new WritableValue("",
        String.class);
    private IObservableValue plateToScanValue = new WritableValue("",
        String.class);
    private IObservableValue scannedValue = new WritableValue(Boolean.FALSE,
        Boolean.class);
    private IObservableValue typesFilledValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private Text patientNumberText;
    private Text plateToScanText;
    private CCombo comboVisits;
    private ComboViewer viewerVisits;

    private Composite typesSelectionCustomComposite;

    private SampleTypeSelectionWidget customSelection;

    private Composite radioComponents;

    private Button confirmAndNextButton;

    private static boolean activityToPrint = false;
    private static boolean testDisposeOn = true;

    private Patient currentPatient;

    @Override
    protected void init() {
        setPartName("Scan Link");
    }

    /**
     * Called from the BiobankPartListener
     */
    public void onClose() {
        // FIXME check it works all the time !
        if (testDisposeOn && activityToPrint) {
            print();
        }
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            confirmAndNextButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            confirmAndNextButton.setEnabled(false);
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
    protected void createFormContent() {
        form.setText("Link sample to patient visit using the scanner");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsComposite();
        createPalletSection();

        createButtonsSection();

        WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
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
        dbc.bindValue(wv, scannedValue, uvs, uvs);
        scannedValue.setValue(false);

        wv = new WritableValue(Boolean.TRUE, Boolean.class);
        uvs = new UpdateValueStrategy();
        uvs.setAfterConvertValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error("Give a type to each sample");
                } else {
                    return Status.OK_STATUS;
                }
            }

        });
        dbc.bindValue(wv, typesFilledValue, uvs, uvs);
    }

    private void createPalletSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);

        spw = new AddSamplesScanPalletWidget(client);
        spw.setVisible(true);
        toolkit.adapt(spw);
        spw.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

        spw.addModificationListener(new ScanPalletModificationListener() {
            @Override
            public void modification(ScanPalletModificationEvent spme) {
                customSelection.setNumber(spme.selections);
            }
        });
    }

    private void createTypesSelectionSection(Composite parent) {
        // Radio buttons
        radioComponents = toolkit.createComposite(parent);
        RowLayout compLayout = new RowLayout();
        radioComponents.setLayout(compLayout);
        toolkit.paintBordersFor(radioComponents);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        radioComponents.setLayoutData(gd);
        radioComponents.setEnabled(false);

        final Button radioRowSelection = toolkit.createButton(radioComponents,
            "Row choice", SWT.RADIO);
        final Button radioCustomSelection = toolkit.createButton(
            radioComponents, "Custom Selection choice", SWT.RADIO);

        // stackLayout
        final Composite selectionComp = toolkit.createComposite(parent);
        final StackLayout selectionStackLayout = new StackLayout();
        selectionComp.setLayout(selectionStackLayout);
        gd = new GridData();
        gd.horizontalSpan = 2;
        selectionComp.setLayoutData(gd);

        List<SampleType> sampleTypes = getAllSampleTypes();
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
                        sampleType.addBinding(dbc);
                        sampleType.resetValues(false);
                    }
                    customSelection.addBinding(dbc);
                    spw.disableSelection();
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
                        sampleType.removeBinding(dbc);
                    }
                    customSelection.addBinding(dbc);
                    spw.enableSelection();
                    typesFilledValue.setValue(spw.isEverythingTyped());
                    spw.redraw();
                }
            }
        });
    }

    private void createTypeSelectionCustom(Composite parent,
        List<SampleType> sampleTypes) {
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
                SampleType type = customSelection.getSelection();
                if (type != null) {
                    for (PalletCell cell : spw.getSelectedCells()) {
                        cell.setType(type);
                        cell.setStatus(SampleCellStatus.TYPE);
                    }
                    spw.clearSelection();
                    customSelection.resetValues(true);
                    typesFilledValue.setValue(spw.isEverythingTyped());
                    spw.redraw();
                }
            }
        });
    }

    private void createTypeSelectionPerRowComposite(Composite parent,
        List<SampleType> sampleTypes) {
        typesSelectionPerRowComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        typesSelectionPerRowComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionPerRowComposite);

        sampleTypeWidgets = new ArrayList<SampleTypeSelectionWidget>();
        char letter = 'A';
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final SampleTypeSelectionWidget typeWidget = new SampleTypeSelectionWidget(
                typesSelectionPerRowComposite, letter, sampleTypes, toolkit);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        setTypeForRow(typeWidget, indexRow);
                        setDirty(true);
                    }

                });
            typeWidget.addBinding(dbc);
            sampleTypeWidgets.add(typeWidget);
            letter += 1;
        }
    }

    private void createFieldsComposite() {
        Composite fieldsComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 400;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        patientNumberText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Patient Number", new String[0],
            patientNumberValue, NonEmptyString.class, "Enter a patient number");
        patientNumberText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                setVisitsList();
            }
        });
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setVisitsList();
            }
        });
        createVisitCombo(fieldsComposite);

        plateToScanText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Plate to Scan", new String[0],
            plateToScanValue, ScannerBarcodeValidator.class,
            "Enter a valid plate barcode");
        plateToScanText.removeKeyListener(keyListener);
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                scan();
            }
        });
        scanButton = toolkit.createButton(fieldsComposite, "Scan", SWT.PUSH);
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
        comboVisits = (CCombo) createBoundWidgetWithLabel(compositeFields,
            CCombo.class, SWT.READ_ONLY | SWT.BORDER | SWT.FLAT, "Visits",
            new String[0], visitSelectionValue, NonEmptyString.class,
            "A visit should be selected");
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        comboVisits.setLayoutData(gridData);
        comboVisits.setEnabled(false);

        viewerVisits = new ComboViewer(comboVisits);
        viewerVisits.setContentProvider(new ArrayContentProvider());
        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisit pv = (PatientVisit) element;
                return BioBankPlugin.getDateTimeFormatter().format(
                    pv.getDateDrawn());
            }
        });
        comboVisits.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        client.setLayoutData(gd);

        initCancelButton(client);

        confirmAndNextButton = toolkit
            .createButton(client, "Confirm", SWT.PUSH);
        confirmAndNextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveAndNext();
            }
        });
    }

    protected void setVisitsList() {
        String pNumber = patientNumberText.getText();
        currentPatient = ModelUtils.getObjectWithAttr(adapter.getAppService(),
            Patient.class, "number", String.class, pNumber);
        if (currentPatient != null) {
            // show visits list
            Collection<PatientVisit> collection = currentPatient
                .getPatientVisitCollection();
            viewerVisits.setInput(collection);
            comboVisits.setEnabled(true);
            if (collection.size() == 1) {
                comboVisits.setListVisible(true);
            }
        }
    }

    private void scan() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    PalletCell[][] cells;
                    if (BioBankPlugin.isRealScanEnabled()) {
                        int plateNum = BioBankPlugin.getDefault()
                            .getPlateNumber(
                                plateToScanValue.getValue().toString());
                        int r = ScanLib.getInstance().slDecodePlate(
                            ScanLib.DPI_300, plateNum);
                        if (r < 0) {
                            BioBankPlugin.openError("Scanner",
                                "Could not decode image. Return code is: " + r);
                            return;
                        }
                        cells = PalletCell.getScanLibResults();
                    } else {
                        cells = PalletCell.getRandomScanLink();
                    }

                    enabledOthersComponents();

                    for (int i = 0; i < cells.length; i++) { // rows
                        int samplesNumber = 0;
                        sampleTypeWidgets.get(i).resetValues(true);
                        for (int j = 0; j < cells[i].length; j++) { // columns
                            if (cells[i][j] != null) {
                                if (cells[i][j].getValue() != null) {
                                    samplesNumber++;
                                    cells[i][j].setStatus(SampleCellStatus.NEW);
                                } else {
                                    cells[i][j]
                                        .setStatus(SampleCellStatus.EMPTY);
                                }
                            }
                        }
                        sampleTypeWidgets.get(i).setNumber(samplesNumber);
                    }

                    // Show result in grid
                    spw.setScannedElements(cells);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error("Error while scanning", e);
                }
            }
        });
    }

    protected void enabledOthersComponents() {
        scannedValue.setValue(true);
        radioComponents.setEnabled(true);

    }

    private List<SampleType> getAllSampleTypes() {
        try {
            return appService.search(SampleType.class, new SampleType());
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception exp) {
        }
        return null;
    }

    @Override
    protected void saveForm() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    List<SDKQuery> queries = new ArrayList<SDKQuery>();
                    PalletCell[][] cells = spw.getScannedElements();
                    for (int indexRow = 0; indexRow < cells.length; indexRow++) {
                        for (int indexColumn = 0; indexColumn < cells[indexRow].length; indexColumn++) {
                            PalletCell cell = cells[indexRow][indexColumn];
                            if (PalletCell.hasValue(cell)
                                && cell.getStatus().equals(
                                    SampleCellStatus.TYPE)) {
                                // add new samples
                                Sample sample = new Sample();
                                sample.setInventoryId(cell.getValue());
                                sample.setPatientVisit(patientVisit);
                                sample.setProcessDate(new Date());
                                sample.setSampleType(cell.getType());
                                queries.add(new InsertExampleQuery(sample));
                            }
                        }
                    }
                    appService.executeBatchQuery(queries);
                    activityToPrint = true;
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (AccessDeniedException ade) {
                    BioBankPlugin.openAccessDeniedErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error(
                        "Error when adding samples", e);
                }
            }
        });
    }

    private void setTypeForRow(SampleTypeSelectionWidget typeWidget,
        int indexRow) {
        if (typeWidget.needToSave()) {
            SampleType type = typeWidget.getSelection();
            PalletCell[][] cells = spw.getScannedElements();
            for (int indexColumn = 0; indexColumn < cells[indexRow].length; indexColumn++) {
                PalletCell cell = cells[indexRow][indexColumn];
                if (PalletCell.hasValue(cell)) {
                    cell.setType(type);
                    cell.setStatus(SampleCellStatus.TYPE);
                    spw.redraw();
                }
            }
        }
    }

    @Override
    protected void cancelForm() {
        spw.setScannedElements(null);
        for (SampleTypeSelectionWidget stw : sampleTypeWidgets) {
            stw.resetValues(true);
        }
    }

    @Override
    public void setFocus() {
        // FIXME doesn't work !
        patientNumberText.setFocus();
    }

    private void print() {
        if (BioBankPlugin.isAskPrint()) {
            boolean doPrint = MessageDialog.openQuestion(PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getShell(), "Print",
                "Do you want to print information ?");
            if (doPrint) {
                // FIXME implement print functionality
            }
        }
        activityToPrint = false;
    }

    private void saveAndNext() {
        testDisposeOn = false;
        doSaveInternal();
        getSite().getPage().closeEditor(ScanLinkEntryForm.this, false);
        AdapterBase.openForm(new FormInput(adapter), ScanLinkEntryForm.ID);
    }

}
