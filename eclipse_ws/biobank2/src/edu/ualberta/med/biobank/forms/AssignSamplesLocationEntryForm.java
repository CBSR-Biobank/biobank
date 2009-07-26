package edu.ualberta.med.biobank.forms;

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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
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
import edu.ualberta.med.biobank.model.PaletteCell;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.ScanPaletteWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import edu.ualberta.med.biobank.wizard.ContainerChooserWizard;
import edu.ualberta.med.scanlib.ScanLib;
import edu.ualberta.med.scanlib.ScanLibFactory;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class AssignSamplesLocationEntryForm extends BiobankEntryForm implements
    CancelConfirmForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.AssignSamplesLocationEntryForm";

    private ScanPaletteWidget paletteWidget;
    private ViewContainerWidget hotelWidget;
    private ViewContainerWidget freezerWidget;

    private Text plateToScanText;
    private Text paletteCodeText;
    private Button scanButton;
    private Text confirmCancelText;
    private Button locateButton;

    private IObservableValue plateToScanValue = new WritableValue("",
        String.class);
    private IObservableValue paletteCodeValue = new WritableValue("",
        String.class);
    private IObservableValue scanLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private IObservableValue hasLocationValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private PaletteCell[][] cells;

    private Study currentStudy;

    protected Container currentPalette;

    protected Sample[][] currentPaletteSamples;

    private SessionAdapter sessionAdapter;

    private Label freezerLabel;

    private Label paletteLabel;

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
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        Assert
            .isTrue((node instanceof SessionAdapter),
                "Invalid editor input: object of type "
                    + node.getClass().getName());

        sessionAdapter = (SessionAdapter) node;
        appService = node.getAppService();

        setPartName("Assign samples location");

        testDisposeOn = true;
    }

    @Override
    public void dispose() {
        if (testDisposeOn && activityToPrint) {
            print();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Assign samples location");

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
            hasLocationValue, "Palette has no location");
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
        freezerWidget.setGridSizes(5, 10, ScanPaletteWidget.PALETTE_WIDTH, 100);

        Composite hotelComposite = toolkit.createComposite(containersComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel");
        hotelWidget = new ViewContainerWidget(hotelComposite);
        toolkit.adapt(hotelWidget);
        hotelWidget.setGridSizes(11, 1, 100,
            ScanPaletteWidget.PALETTE_HEIGHT_AND_LEGEND);
        hotelWidget.setFirstColSign(null);
        hotelWidget.setFirstRowSign(1);

        Composite paletteComposite = toolkit
            .createComposite(containersComposite);
        paletteComposite.setLayout(getNeutralGridLayout());
        paletteComposite.setLayoutData(new GridData());
        paletteLabel = toolkit.createLabel(paletteComposite, "Palette");
        paletteWidget = new ScanPaletteWidget(paletteComposite);
        toolkit.adapt(paletteWidget);
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
        gd.widthHint = 200;
        client.setLayoutData(gd);
        toolkit.paintBordersFor(client);

        plateToScanText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Plate to scan", new String[0], plateToScanValue,
            ScannerBarcodeValidator.class, "Enter a valid plate barcode");
        plateToScanText.removeKeyListener(keyListener);
        plateToScanText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        paletteCodeText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Palette barcode", new String[0], paletteCodeValue,
            NonEmptyString.class, "Enter palette barcode");
        paletteCodeText.removeKeyListener(keyListener);
        paletteCodeText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

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

        locateButton = toolkit
            .createButton(client, "Choose Location", SWT.PUSH);
        locateButton.setVisible(false);
        GridData gd = new GridData();
        gd.horizontalSpan = columns;
        locateButton.setLayoutData(gd);
        locateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                chooseLocation();
            }
        });

        confirmCancelText = toolkit.createText(client, "");
        confirmCancelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        gd = new GridData();
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
                "No study has been found on this palette");
            return;
        }
        ContainerChooserWizard wizard = new ContainerChooserWizard(appService,
            currentStudy.getSite());
        WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
        int res = dialog.open();
        if (res == Window.OK) {
            initNewPalette(wizard.getSelectedPosition(), wizard
                .getContainerType());
            showOnlyPalette(false);
            showPalettePosition(currentPalette);
        }
        form.reflow(true);
    }

    private void initNewPalette(ContainerPosition position, ContainerType type) {
        currentPalette.setPosition(position);
        currentPalette.setContainerType(type);
        currentPalette.setPositionCode(paletteCodeValue.getValue().toString());
        currentPalette
            .setProductBarcode(paletteCodeValue.getValue().toString());
        currentPalette.setSite(currentStudy.getSite());
    }

    protected void scan() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    boolean showResult = getPaletteInformation();
                    if (showResult) {
                        if (BioBankPlugin.isRealScanEnabled()) {
                            int plateNum = BioBankPlugin.getDefault()
                                .getPlateNumber(
                                    plateToScanValue.getValue().toString());
                            ScanLib scanLib = ScanLibFactory.getScanLib();
                            int r = scanLib.slDecodePlate(ScanLib.DPI_300,
                                plateNum);
                            if (r < ScanLib.SC_SUCCESS) {
                                BioBankPlugin.openError("Scanner",
                                    "Could not decode image. Return code is: "
                                        + r);
                                return;
                            }
                            cells = PaletteCell.getScanLibResults();
                        } else {
                            if (notexistsButton.getSelection()) {
                                cells = PaletteCell
                                    .getRandomScanProcessNotInPalette(appService);
                            } else if (existsButton.getSelection()) {
                                cells = PaletteCell
                                    .getRandomScanProcessAlreadyInPalette(appService);
                            } else {
                                cells = PaletteCell.getRandomScanProcess();
                            }
                        }
                        currentStudy = null;
                        boolean result = true;
                        for (int i = 0; i < cells.length; i++) { // rows
                            for (int j = 0; j < cells[i].length; j++) { // columns
                                Sample positionSample = null;
                                if (currentPaletteSamples != null) {
                                    positionSample = currentPaletteSamples[i][j];
                                }
                                result = setStatus(cells[i][j], positionSample)
                                    && result;
                            }
                        }

                        scanValidValue.setValue(result);
                        paletteWidget.setScannedElements(cells);
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

    private void showOnlyPalette(boolean show) {
        freezerLabel.getParent().setVisible(!show);
        ((GridData) freezerLabel.getParent().getLayoutData()).exclude = show;
        hotelLabel.getParent().setVisible(!show);
        ((GridData) hotelLabel.getParent().getLayoutData()).exclude = show;
        locateButton.setVisible(show);
        ((GridData) locateButton.getLayoutData()).exclude = !show;
    }

    protected void showPalettePosition(Container palette) {
        ContainerPosition palettePosition = palette.getPosition();
        if (palettePosition != null) {
            Container hotelContainer = palettePosition.getParentContainer();
            ContainerPosition hotelPosition = hotelContainer.getPosition();
            Container freezerContainer = hotelPosition.getParentContainer();

            freezerLabel.setText(freezerContainer.getPositionCode());
            int dim1 = freezerContainer.getContainerType().getCapacity()
                .getDimensionOneCapacity();
            int dim2 = freezerContainer.getContainerType().getCapacity()
                .getDimensionTwoCapacity();
            freezerWidget.setStorageSize(dim1, dim2);
            freezerWidget.setSelectedBox(new Point(hotelPosition
                .getPositionDimensionOne() - 1, hotelPosition
                .getPositionDimensionTwo() - 1));

            hotelLabel.setText(hotelContainer.getPositionCode());
            dim1 = hotelContainer.getContainerType().getCapacity()
                .getDimensionOneCapacity();
            dim2 = hotelContainer.getContainerType().getCapacity()
                .getDimensionTwoCapacity();
            hotelWidget.setStorageSize(dim1, dim2);
            hotelWidget.setSelectedBox(new Point(palettePosition
                .getPositionDimensionOne() - 1, palettePosition
                .getPositionDimensionTwo() - 1));

            paletteLabel.setText(palette.getPositionCode());
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
            form.setText("Assigning samples location");
        } else {
            form.setText("Assigning samples location for study "
                + currentStudy.getNameShort());
        }
    }

    protected boolean setStatus(PaletteCell scanCell, Sample positionSample)
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
                        .equals(currentPalette.getId())) {
                    scanCell.setStatus(SampleCellStatus.ERROR);
                    Container samplePalette = sample.getSamplePosition()
                        .getContainer();
                    String posString = samplePalette.getPositionCode();
                    Container parent = samplePalette.getPosition()
                        .getParentContainer();
                    while (parent != null) {
                        posString = parent.getPositionCode() + "-" + posString;
                        parent = parent.getPosition().getParentContainer();
                    }
                    scanCell
                        .setInformation("Sample registered on anothe palette with position "
                            + posString + "!");
                    scanCell.setTitle("!");
                    return false;
                }
                scanCell.setStatus(SampleCellStatus.NEW);
            }
            Study cellStudy = sample.getPatientVisit().getPatient().getStudy();
            if (currentStudy == null) {
                // look which study is on the palette from the first cell
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

    private boolean isNewPalette() {
        return currentPalette.getId() == null;
    }

    @Override
    protected void saveForm() throws Exception {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    SDKQuery query;
                    if (isNewPalette()) {
                        query = new InsertExampleQuery(currentPalette);
                        SDKQueryResult res = appService.executeQuery(query);
                        currentPalette = (Container) res.getObjectResult();
                    }

                    List<SDKQuery> queries = new ArrayList<SDKQuery>();
                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            PaletteCell cell = cells[i][j];
                            // cell.getStatus()
                            if (cell != null) {
                                Sample sample = cell.getSample();
                                if (sample != null
                                    && sample.getSamplePosition() == null) {
                                    SamplePosition samplePosition = new SamplePosition();
                                    samplePosition.setPositionDimensionOne(i);
                                    samplePosition.setPositionDimensionTwo(j);
                                    samplePosition.setContainer(currentPalette);
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
                        "Error when saving palette location", e);
                }
            }
        });
    }

    @Override
    protected void cancelForm() {
        freezerWidget.setSelectedBox(null);
        hotelWidget.setSelectedBox(null);
        paletteWidget.setScannedElements(null);
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
                scanButton.setEnabled(!paletteCodeText.getText().isEmpty());
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
     * From the palette barcode, get existing information form database
     */
    private boolean getPaletteInformation() throws ApplicationException {
        currentPaletteSamples = null;
        String barcode = (String) paletteCodeValue.getValue();
        currentPalette = ModelUtils
            .getContainerWithPositionCode(appService, barcode);
        if (currentPalette != null) {
            boolean result = MessageDialog
                .openConfirm(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), "Palette barcode",
                    "This palette is already registered in the database. Do you want to continue ?");
            if (!result) {
                return false;
            }
            showPalettePosition(currentPalette);
            Capacity paletteCapacity = currentPalette.getContainerType()
                .getCapacity();
            currentPaletteSamples = new Sample[paletteCapacity
                .getDimensionOneCapacity()][paletteCapacity
                .getDimensionTwoCapacity()];
            for (SamplePosition position : currentPalette
                .getSamplePositionCollection()) {
                currentPaletteSamples[position.getPositionDimensionOne()][position
                    .getPositionDimensionTwo()] = position.getSample();
            }
            showOnlyPalette(false);
        } else {
            currentPalette = new Container();
            currentPalette.setProductBarcode(barcode);
            showOnlyPalette(true);
            hasLocationValue.setValue(Boolean.FALSE);
            paletteLabel.setText("New Palette");
        }
        return true;
    }

    private void saveAndNext() {
        testDisposeOn = false;
        doSaveInternal();
        getSite().getPage().closeEditor(AssignSamplesLocationEntryForm.this,
            false);
        Node.openForm(new FormInput(sessionAdapter),
            AssignSamplesLocationEntryForm.ID);
    }

    private void saveAndClose() {
        testDisposeOn = true;
        doSaveInternal();
        getSite().getPage().closeEditor(AssignSamplesLocationEntryForm.this,
            false);
        // Node node = sessionAdapter.accept(new NodeSearchVisitor(
        // Container.class, currentPalette.getId()));
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
