package edu.ualberta.med.biobank.forms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.forms.listener.CancelConfirmKeyListener;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import edu.ualberta.med.biobank.wizard.ContainerChooserWizard;
import edu.ualberta.med.scanlib.ScanLib;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class AssignSamplesLocationEntryForm extends BiobankEntryForm implements
    CancelConfirmForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.AssignSamplesLocationEntryForm";

    private ScanPalletWidget palletWidget;
    private ViewContainerWidget hotelWidget;
    private ViewContainerWidget freezerWidget;

    private Text plateToScanText;
    private Text palletCodeText;
    private Text palletPositionText;
    private Button scanButton;
    private Text confirmCancelText;
    private Button locateButton;

    private IObservableValue plateToScanValue = new WritableValue("",
        String.class);
    private IObservableValue palletProductCodeValue = new WritableValue("",
        String.class);
    private IObservableValue palletPositionValue = new WritableValue("",
        String.class);
    private IObservableValue scanLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private IObservableValue hasLocationValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private PalletCell[][] cells;

    private Study currentStudy;

    protected Container currentPallet;

    protected Sample[][] currentPalletSamples;

    private SessionAdapter sessionAdapter;

    private Label freezerLabel;

    private Label palletLabel;

    private Label hotelLabel;

    private Composite containersComposite;

    // for debugging only :
    private Button existsButton;
    private Button notexistsButton;

    private Button confirmAndNextButton;

    private Button confirmAndClose;

    private static boolean activityToPrint = false;
    private static boolean testDisposeOn = true;

    @Override
    protected void init() {
        Assert.isTrue((adapter instanceof SessionAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        sessionAdapter = (SessionAdapter) adapter;
        testDisposeOn = true;
        setPartName("Assign locations for samples");
    }

    @Override
    public void dispose() {
        if (testDisposeOn && activityToPrint) {
            print();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Assign locations for samples");
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);

        createFieldsSection();

        createContainersSection();

        createButtonsSection();

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanLaunchedValue, "Scanner should be launched");
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue, "Error in scanning result");
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            hasLocationValue, "Pallet has no location");
    }

    private void createContainersSection() {
        containersComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = getNeutralGridLayout();
        layout.numColumns = 2;
        containersComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        containersComposite.setLayoutData(gd);
        toolkit.paintBordersFor(containersComposite);

        showAllContainers(false);

        Composite freezerComposite = toolkit
            .createComposite(containersComposite);
        freezerComposite.setLayout(getNeutralGridLayout());
        GridData gdFreezer = new GridData();
        gdFreezer.horizontalSpan = 2;
        gdFreezer.horizontalAlignment = SWT.RIGHT;
        freezerComposite.setLayoutData(gdFreezer);
        freezerLabel = toolkit.createLabel(freezerComposite, "Freezer");
        freezerLabel.setLayoutData(new GridData());
        freezerWidget = new ViewContainerWidget(freezerComposite);
        toolkit.adapt(freezerWidget);
        freezerWidget.setGridSizes(5, 10, ScanPalletWidget.PALLET_WIDTH, 100);
        try {
            // FIXME - homogenise
            List<ContainerType> types = ModelUtils.queryProperty(appService,
                ContainerType.class, "name", "Freezer", false);
            if (types.size() > 0) {
                freezerWidget.setParams(types.get(0), null);
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Composite hotelComposite = toolkit.createComposite(containersComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel");
        hotelWidget = new ViewContainerWidget(hotelComposite);
        toolkit.adapt(hotelWidget);
        hotelWidget.setGridSizes(11, 1, 100,
            ScanPalletWidget.PALLET_HEIGHT_AND_LEGEND);
        hotelWidget.setFirstColSign(null);
        hotelWidget.setFirstRowSign(1);

        Composite palletComposite = toolkit
            .createComposite(containersComposite);
        palletComposite.setLayout(getNeutralGridLayout());
        palletComposite.setLayoutData(new GridData());
        palletLabel = toolkit.createLabel(palletComposite, "Pallet");
        palletWidget = new ScanPalletWidget(palletComposite);
        toolkit.adapt(palletWidget);
    }

    private GridLayout getNeutralGridLayout() {
        GridLayout layout;
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    private void createFieldsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.widthHint = 300;
        client.setLayoutData(gd);
        toolkit.paintBordersFor(client);

        palletCodeText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Pallet product barcode", new String[0],
            palletProductCodeValue, NonEmptyString.class,
            "Enter pallet position code");
        palletCodeText.removeKeyListener(keyListener);
        palletCodeText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        palletPositionText = (Text) createBoundWidgetWithLabel(client,
            Text.class, SWT.NONE, "Pallet position barcode", new String[0],
            palletPositionValue, NonEmptyString.class, "Enter position code");
        palletPositionText.removeKeyListener(keyListener);
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        plateToScanText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Plate to scan", new String[0], plateToScanValue,
            ScannerBarcodeValidator.class, "Enter a valid plate barcode");
        plateToScanText.removeKeyListener(keyListener);
        plateToScanText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        if (!BioBankPlugin.isRealScanEnabled()) {
            gd.widthHint = 250;
            Composite comp = toolkit.createComposite(client);
            comp.setLayout(new GridLayout());
            gd = new GridData();
            gd.horizontalSpan = 2;
            comp.setLayoutData(gd);
            notexistsButton = toolkit.createButton(comp,
                "Scan non localised Samples", SWT.RADIO);
            notexistsButton.setSelection(true);
            existsButton = toolkit.createButton(comp, "Scan localised sample",
                SWT.RADIO);
            toolkit.createButton(comp, "Default sample", SWT.RADIO);
        }

        scanButton = toolkit.createButton(client, "Scan", SWT.PUSH);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scan();
            }
        });
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        int columns = 4;
        GridLayout layout = new GridLayout(columns, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        // locateButton = toolkit
        // .createButton(client, "Choose Location", SWT.PUSH);
        // locateButton.setVisible(false);
        // GridData gd = new GridData();
        // gd.horizontalSpan = columns;
        // locateButton.setLayoutData(gd);
        // locateButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // chooseLocation();
        // }
        // });

        confirmCancelText = toolkit.createText(client, "");
        confirmCancelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData gd = new GridData();
        gd.widthHint = 100;
        confirmCancelText.setLayoutData(gd);
        confirmCancelText.addKeyListener(new CancelConfirmKeyListener(this));

        initCancelButton(client);

        confirmAndNextButton = toolkit.createButton(client,
            "Confirm and scan next", SWT.PUSH);
        confirmAndNextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveAndNext();
            }
        });
        confirmAndClose = toolkit.createButton(client, "Confirm  Close",
            SWT.PUSH);
        confirmAndClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveAndClose();
            }
        });
    }

    protected void chooseLocation() {
        if (currentStudy == null) {
            BioBankPlugin.openError("Wizard Problem",
                "No study has been found on this pallet");
            return;
        }
        ContainerChooserWizard wizard = new ContainerChooserWizard(appService,
            currentStudy.getSite());
        WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
        int res = dialog.open();
        if (res == Window.OK) {
            initNewPallet(wizard.getSelectedPosition(), wizard
                .getContainerType());
            showOnlyPallet(false);
            showPalletPosition(currentPallet);
        }
        form.reflow(true);
    }

    private void initNewPallet(ContainerPosition position, ContainerType type) {
        currentPallet.setPosition(position);
        currentPallet.setContainerType(type);
        currentPallet.setLabel(getPalletPositionString(position));
        currentPallet.setProductBarcode(palletProductCodeValue.getValue()
            .toString());
        currentPallet.setSite(currentStudy.getSite());
    }

    public String getPalletPositionString(ContainerPosition position) {
        Container parent = position.getParentContainer();
        String positionString = parent.getLabel();
        // FIXME generalize using numbering scheme
        int dim1Capacity = parent.getContainerType().getCapacity()
            .getDimensionOneCapacity();
        int pos;
        // For hotel, use the dim with for than one size
        // FIXME generalize !
        if (dim1Capacity > 1) {
            pos = position.getPositionDimensionOne() + 1;
        } else {
            pos = position.getPositionDimensionTwo() + 1;
        }
        DecimalFormat df1 = new DecimalFormat("00");
        return positionString + df1.format(pos);
    }

    protected void scan() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    boolean showResult = getPalletInformation();
                    if (showResult) {
                        if (BioBankPlugin.isRealScanEnabled()) {
                            int plateNum = BioBankPlugin.getDefault()
                                .getPlateNumber(
                                    plateToScanValue.getValue().toString());
                            int r = ScanLib.getInstance().slDecodePlate(
                                ScanLib.DPI_300, plateNum);
                            if (r < ScanLib.SC_SUCCESS) {
                                BioBankPlugin.openError("Scanner",
                                    "Could not decode image. Return code is: "
                                        + r);
                                return;
                            }
                            cells = PalletCell.getScanLibResults();
                        } else {
                            if (notexistsButton.getSelection()) {
                                cells = PalletCell
                                    .getRandomScanProcessNotInPallet(appService);
                            } else if (existsButton.getSelection()) {
                                cells = PalletCell
                                    .getRandomScanProcessAlreadyInPallet(appService);
                            } else {
                                cells = PalletCell.getRandomScanProcess();
                            }
                        }
                        currentStudy = null;
                        boolean result = true;
                        for (int i = 0; i < cells.length; i++) { // rows
                            for (int j = 0; j < cells[i].length; j++) { // columns
                                Sample positionSample = null;
                                if (currentPalletSamples != null) {
                                    positionSample = currentPalletSamples[i][j];
                                }
                                result = setStatus(cells[i][j], positionSample)
                                    && result;
                            }
                        }

                        scanValidValue.setValue(result);
                        palletWidget.setScannedElements(cells);
                        showStudyInformation();
                        scanLaunchedValue.setValue(true);
                        setDirty(true);

                        showAllContainers(true);
                    } else {
                        showAllContainers(false);
                    }
                    scanButton.traverse(SWT.TRAVERSE_TAB_NEXT);
                    form.reflow(true);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error("Error while scanning", e);
                    scanValidValue.setValue(false);
                }
            }
        });
    }

    private void showAllContainers(boolean show) {
        ((GridData) containersComposite.getLayoutData()).exclude = !show;
        containersComposite.setVisible(show);
    }

    private void showOnlyPallet(boolean show) {
        freezerLabel.getParent().setVisible(!show);
        ((GridData) freezerLabel.getParent().getLayoutData()).exclude = show;
        hotelLabel.getParent().setVisible(!show);
        ((GridData) hotelLabel.getParent().getLayoutData()).exclude = show;
        // locateButton.setVisible(show);
        // ((GridData) locateButton.getLayoutData()).exclude = !show;
    }

    protected void showPalletPosition(Container pallet) {
        ContainerPosition palletPosition = pallet.getPosition();
        if (palletPosition != null) {
            Container hotelContainer = palletPosition.getParentContainer();
            ContainerPosition hotelPosition = hotelContainer.getPosition();
            Container freezerContainer = hotelPosition.getParentContainer();

            freezerLabel.setText(freezerContainer.getLabel());
            int dim1 = freezerContainer.getContainerType().getCapacity()
                .getDimensionOneCapacity();
            int dim2 = freezerContainer.getContainerType().getCapacity()
                .getDimensionTwoCapacity();
            freezerWidget.setStorageSize(dim1, dim2);
            freezerWidget.setSelectedBox(new Point(hotelPosition
                .getPositionDimensionOne(), hotelPosition
                .getPositionDimensionTwo()));

            hotelLabel.setText(hotelContainer.getLabel());
            dim1 = hotelContainer.getContainerType().getCapacity()
                .getDimensionOneCapacity();
            dim2 = hotelContainer.getContainerType().getCapacity()
                .getDimensionTwoCapacity();
            hotelWidget.setStorageSize(dim1, dim2);
            hotelWidget.setSelectedBox(new Point(palletPosition
                .getPositionDimensionOne(), palletPosition
                .getPositionDimensionTwo()));

            palletLabel.setText(pallet.getLabel());
            hasLocationValue.setValue(Boolean.TRUE);
        } else {
            hasLocationValue.setValue(Boolean.FALSE);
        }
    }

    /**
     * if a study is found, show the name in title
     */
    protected void showStudyInformation() {
        if (currentStudy == null) {
            setPartName("Assigning samples location");
        } else {
            setPartName("Assigning samples location for study "
                + currentStudy.getNameShort());
        }
    }

    protected boolean setStatus(PalletCell scanCell, Sample positionSample)
        throws ApplicationException {
        String value = scanCell.getValue();
        if (value == null) {
            if (positionSample == null) {
                return true;
            }
            scanCell.setStatus(SampleCellStatus.MISSING);
            scanCell.setInformation("Sample " + positionSample.getInventoryId()
                + " missing");
            scanCell.setTitle("?");
            return false;
        }
        if (value.isEmpty()) {
            scanCell.setStatus(SampleCellStatus.ERROR);
            scanCell.setInformation("Error retrieving bar code");
            scanCell.setTitle("?");
            return false;
        }
        Sample sample = new Sample();
        sample.setInventoryId(value);
        List<Sample> samples = appService.search(Sample.class, sample);
        if (samples.size() == 0) {
            scanCell.setStatus(SampleCellStatus.ERROR);
            scanCell.setInformation("Sample not found");
            scanCell.setTitle("-");
            return false;
        } else if (samples.size() == 1) {
            sample = samples.get(0);
            if (positionSample != null
                && !sample.getId().equals(positionSample.getId())) {
                scanCell.setStatus(SampleCellStatus.ERROR);
                scanCell
                    .setInformation("Sample different from the one registered");
                scanCell.setTitle("!");
                return false;
            }
            scanCell.setSample(sample);
            if (positionSample != null) {
                scanCell.setStatus(SampleCellStatus.FILLED);
                scanCell.setSample(positionSample);
            } else {
                if (sample.getSamplePosition() != null
                    && !sample.getSamplePosition().getContainer().getId()
                        .equals(currentPallet.getId())) {
                    scanCell.setStatus(SampleCellStatus.ERROR);
                    Container samplePallet = sample.getSamplePosition()
                        .getContainer();
                    String posString = samplePallet.getLabel();
                    Container parent = samplePallet.getPosition()
                        .getParentContainer();
                    while (parent != null) {
                        posString = parent.getLabel() + "-" + posString;
                        parent = parent.getPosition().getParentContainer();
                    }
                    scanCell
                        .setInformation("Sample registered on anothe pallet with position "
                            + posString + "!");
                    scanCell.setTitle("!");
                    return false;
                }
                scanCell.setStatus(SampleCellStatus.NEW);
            }
            Study cellStudy = sample.getPatientVisit().getPatient().getStudy();
            if (currentStudy == null) {
                // look which study is on the pallet from the first cell
                currentStudy = cellStudy;
            } else if (!currentStudy.getId().equals(cellStudy.getId())) {
                // FIXME problem if try currentStudy.equals(cellStudy)... should
                // work !!
                scanCell.setStatus(SampleCellStatus.ERROR);
                scanCell.setInformation("Not same study (study="
                    + cellStudy.getNameShort() + ")");
                return false;
            }
            scanCell
                .setTitle(sample.getPatientVisit().getPatient().getNumber());
            return true;
        } else {
            Assert.isTrue(false, "InventoryId should be unique !");
            return false;
        }
    }

    private boolean isNewPallet() {
        return currentPallet.getId() == null;
    }

    @Override
    protected void saveForm() throws Exception {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    SDKQuery query;
                    if (isNewPallet()) {
                        query = new InsertExampleQuery(currentPallet);
                        SDKQueryResult res = appService.executeQuery(query);
                        currentPallet = (Container) res.getObjectResult();
                    }

                    List<SDKQuery> queries = new ArrayList<SDKQuery>();
                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            PalletCell cell = cells[i][j];
                            // cell.getStatus()
                            if (cell != null) {
                                Sample sample = cell.getSample();
                                if (sample != null
                                    && sample.getSamplePosition() == null) {
                                    SamplePosition samplePosition = new SamplePosition();
                                    samplePosition.setPositionDimensionOne(i);
                                    samplePosition.setPositionDimensionTwo(j);
                                    samplePosition.setContainer(currentPallet);
                                    samplePosition.setSample(sample);
                                    sample.setSamplePosition(samplePosition);
                                    queries.add(new UpdateExampleQuery(sample));
                                }
                            }
                        }
                    }
                    appService.executeBatchQuery(queries);
                    activityToPrint = true;
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error(
                        "Error when saving pallet location", e);
                }
            }
        });
    }

    @Override
    protected void cancelForm() {
        freezerWidget.setSelectedBox(null);
        hotelWidget.setSelectedBox(null);
        palletWidget.setScannedElements(null);
        cells = null;
        currentStudy = null;
        scanLaunchedValue.setValue(false);
        setDirty(false);
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            confirmAndNextButton.setEnabled(true);
            confirmAndClose.setEnabled(true);
            scanButton.setEnabled(true);
            locateButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            confirmAndNextButton.setEnabled(false);
            confirmAndClose.setEnabled(false);
            if (!BioBankPlugin.getDefault().isValidPlateBarcode(
                plateToScanText.getText())) {
                scanButton.setEnabled(false);
            } else {
                scanButton.setEnabled(!palletCodeText.getText().isEmpty()
                    && !palletPositionText.getText().isEmpty());
            }
            locateButton.setEnabled((Boolean) scanValidValue.getValue());
        }
    }

    @Override
    protected String getOkMessage() {
        return "Assigning samples location.";
    }

    @Override
    public void setFocus() {
        if (plateToScanValue.getValue().toString().isEmpty()) {
            plateToScanText.setFocus();
        }
    }

    // CancelConfirmForm implementation
    public void cancel() throws Exception {
        cancelForm();
    }

    public void confirm() throws Exception {
        saveAndNext();
    }

    public boolean isConfirmEnabled() {
        return confirmAndNextButton.isEnabled();
    }

    // End CancelConfirmForm implementation

    /**
     * From the pallet barcode, get existing information form database
     */
    private boolean getPalletInformation() throws Exception {
        currentPalletSamples = null;
        String barcode = (String) palletProductCodeValue.getValue();
        currentPallet = ModelUtils.getContainerWithLabel(appService, barcode,
            "Pallet");
        if (currentPallet != null) {
            boolean result = MessageDialog
                .openConfirm(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(),
                    "Pallet product barcode",
                    "This pallet is already registered in the database. Do you want to continue ?");
            if (!result) {
                return false;
            }
            showPalletPosition(currentPallet);
            Capacity palletCapacity = currentPallet.getContainerType()
                .getCapacity();
            currentPalletSamples = new Sample[palletCapacity
                .getDimensionOneCapacity()][palletCapacity
                .getDimensionTwoCapacity()];
            for (SamplePosition position : currentPallet
                .getSamplePositionCollection()) {
                currentPalletSamples[position.getPositionDimensionOne()][position
                    .getPositionDimensionTwo()] = position.getSample();
            }
            showOnlyPallet(false);
        } else {
            currentPallet = new Container();
            currentPallet.setProductBarcode(barcode);
            showOnlyPallet(true);
            hasLocationValue.setValue(Boolean.FALSE);
            palletLabel.setText("New Pallet");
        }
        return true;
    }

    private void saveAndNext() {
        testDisposeOn = false;
        doSaveInternal();
        getSite().getPage().closeEditor(AssignSamplesLocationEntryForm.this,
            false);
        AdapterBase.openForm(new FormInput(sessionAdapter),
            AssignSamplesLocationEntryForm.ID);
    }

    private void saveAndClose() {
        testDisposeOn = true;
        doSaveInternal();
        getSite().getPage().closeEditor(AssignSamplesLocationEntryForm.this,
            false);
        // Node node = sessionAdapter.accept(new NodeSearchVisitor(
        // Container.class, currentPallet.getId()));
        // if (node != null) {
        // SessionManager.getInstance().getTreeViewer().setSelection(
        // new StructuredSelection(node));
        // }
        // node.performDoubleClick();
    }

    private void print() {
        if (BioBankPlugin.isAskPrint()) {
            boolean doPrint = MessageDialog.openQuestion(PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getShell(), "Print",
                "Do you want to print information ?");
            if (doPrint) {
                // FIXME implement print functionnality
            }
        }
        activityToPrint = false;
    }

}
