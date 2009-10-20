package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.PalletBarCodeValidator;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.PalletWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ScanAssignEntryForm extends AbstractPatientAdminForm {

    private static Logger LOGGER = Logger.getLogger(ScanAssignEntryForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanAssignEntryForm";

    private ComboViewer palletTypesViewer;
    private Text plateToScanText;
    private Text palletCodeText;
    private Text palletPositionText;
    private Button scanButton;

    private Label freezerLabel;
    private ViewContainerWidget freezerWidget;
    private Label palletLabel;
    private PalletWidget palletWidget;
    private Label hotelLabel;
    private ViewContainerWidget hotelWidget;

    private IObservableValue plateToScanValue = new WritableValue("",
        String.class);
    private IObservableValue scanLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private PalletCell[][] cells;

    protected ContainerWrapper currentPalletWrapper;

    private ContainerTypeWrapper onlyTypePossible;

    protected SampleWrapper[][] currentPalletSamples;

    // for debugging only (fake scan) :
    private Button linkedOnlyButton;
    private Button linkedAssignButton;

    private String palletNameContains = "";

    private CancelConfirmWidget cancelConfirmWidget;

    @Override
    protected void init() {
        super.init();
        setPartName("Scan Assign");
        currentPalletWrapper = new ContainerWrapper(appService);
        initPalletValues();
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        palletNameContains = store
            .getString(PreferenceConstants.PALLET_SCAN_CONTAINER_NAME_CONTAINS);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Assign samples locations using the scanner");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();

        createContainersSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanLaunchedValue, "Scanner should be launched");
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue, "Error in scanning result");
    }

    private void createFieldsSection() throws Exception {
        Composite fieldsComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 400;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        createContainerTypeSection(fieldsComposite);

        palletCodeText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Pallet product barcode", null,
            BeansObservables.observeValue(currentPalletWrapper,
                "productBarcode"), new NonEmptyString(
                "Enter pallet position code"));
        palletCodeText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        palletPositionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Pallet label", null, BeansObservables
                .observeValue(currentPalletWrapper, "label"),
            new PalletBarCodeValidator("Enter position code (ie. 01AA02)"));
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        plateToScanText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Plate to scan", new String[0],
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
            gd.widthHint = 300;
            Composite comp = toolkit.createComposite(fieldsComposite);
            comp.setLayout(new GridLayout());
            gd = new GridData();
            gd.horizontalSpan = 2;
            comp.setLayoutData(gd);
            linkedAssignButton = toolkit.createButton(comp,
                "Select linked only samples", SWT.RADIO);
            linkedAssignButton.setSelection(true);
            linkedOnlyButton = toolkit.createButton(comp,
                "Select linked and assigned samples", SWT.RADIO);
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
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        try {
                            scan();
                        } catch (RemoteConnectFailureException exp) {
                            BioBankPlugin.openRemoteConnectErrorMessage();
                        } catch (Exception e) {
                            BioBankPlugin.openError("Scan result error", e);
                            scanValidValue.setValue(false);
                        }
                    }
                });
            }
        });
    }

    private void createContainersSection() {
        Composite containersComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = getNeutralGridLayout();
        layout.numColumns = 2;
        containersComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        containersComposite.setLayoutData(gd);
        toolkit.paintBordersFor(containersComposite);

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
        freezerWidget.setGridSizes(5, 10, PalletWidget.PALLET_WIDTH, 100);

        Composite hotelComposite = toolkit.createComposite(containersComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel");
        hotelWidget = new ViewContainerWidget(hotelComposite);
        toolkit.adapt(hotelWidget);
        hotelWidget.setGridSizes(11, 1, 100,
            PalletWidget.PALLET_HEIGHT_AND_LEGEND);

        Composite palletComposite = toolkit
            .createComposite(containersComposite);
        palletComposite.setLayout(getNeutralGridLayout());
        palletComposite.setLayoutData(new GridData());
        palletLabel = toolkit.createLabel(palletComposite, "Pallet");
        palletWidget = new PalletWidget(palletComposite);
        toolkit.adapt(palletWidget);

        showOnlyPallet(true);
    }

    private GridLayout getNeutralGridLayout() {
        GridLayout layout;
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    /**
     * If can't know which pallet type we need, add a combo
     */
    private void createContainerTypeSection(Composite parent) throws Exception {
        List<ContainerTypeWrapper> palletContainerTypes = ContainerTypeWrapper
            .getContainerTypesInSite(appService,
                currentPalletWrapper.getSite(), palletNameContains, false);
        if (palletContainerTypes.size() == 1) {
            onlyTypePossible = palletContainerTypes.get(0);
            currentPalletWrapper.setContainerType(onlyTypePossible);
        } else {
            if (palletContainerTypes.size() == 0) {
                BioBankPlugin.openError("No Pallet defined ?",
                    "No container type found with name containing "
                        + palletNameContains + "...");
            }
            palletTypesViewer = createCComboViewerWithNoSelectionValidator(
                parent, "Pallet Container Type", palletContainerTypes, null,
                "A pallet type should be selected");
            palletTypesViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        setContainerType();
                        scanLaunchedValue.setValue(false);
                    }
                });
        }
    }

    protected void scan() {
        try {
            boolean showResult = checkPallet();
            currentPalletWrapper.getWrappedObject().setId(null);
            if (showResult) {
                appendLog("----");
                appendLog("Scanning plate "
                    + plateToScanValue.getValue().toString());
                showOnlyPallet(false);
                if (BioBankPlugin.isRealScanEnabled()) {
                    int plateNum = BioBankPlugin.getDefault().getPlateNumber(
                        plateToScanValue.getValue().toString());
                    cells = PalletCell.convertArray(ScannerConfigPlugin
                        .scan(plateNum));
                } else {
                    if (linkedAssignButton.getSelection()) {
                        cells = PalletCell.getRandomSamplesNotAssigned(
                            appService, SessionManager.getInstance()
                                .getCurrentSiteWrapper().getId());
                    } else if (linkedOnlyButton.getSelection()) {
                        cells = PalletCell.getRandomSamplesAlreadyAssigned(
                            appService, SessionManager.getInstance()
                                .getCurrentSiteWrapper().getId());
                    }
                }
                boolean result = true;
                for (int i = 0; i < cells.length; i++) { // rows
                    for (int j = 0; j < cells[i].length; j++) { // columns
                        SampleWrapper expectedSample = null;
                        if (currentPalletSamples != null) {
                            expectedSample = currentPalletSamples[i][j];
                        }
                        cells[i][j].setExpectedSample(expectedSample);
                        result = setStatus(cells[i][j]) && result;
                    }
                }

                scanValidValue.setValue(result);
                palletWidget.setScannedElements(cells);
                scanLaunchedValue.setValue(true);
                setDirty(true);
            } else {
                palletWidget.setScannedElements(PalletCell.getEmptyCells());
                showOnlyPallet(true);
                scanValidValue.setValue(false);
            }
            showPalletPosition();
            scanButton.traverse(SWT.TRAVERSE_TAB_NEXT);
            form.layout(true, true);
        } catch (RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            BioBankPlugin.openError("Error while scanning", e);
            String msg = e.getMessage();
            if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
                msg = e.getCause().getMessage();
            }
            appendLog("ERROR: " + msg);
        }
    }

    private void showOnlyPallet(boolean show) {
        freezerLabel.getParent().setVisible(!show);
        ((GridData) freezerLabel.getParent().getLayoutData()).exclude = show;
        hotelLabel.getParent().setVisible(!show);
        ((GridData) hotelLabel.getParent().getLayoutData()).exclude = show;
    }

    protected void showPalletPosition() {
        if (currentPalletWrapper.hasParent()) {
            ContainerWrapper hotelContainer = currentPalletWrapper.getParent();
            ContainerWrapper freezerContainer = hotelContainer.getParent();

            freezerLabel.setText(freezerContainer.getFullInfoLabel());
            freezerWidget.setContainerType(freezerContainer.getContainerType());
            freezerWidget.setSelectedBox(new Point(
                hotelContainer.getPosition().row,
                hotelContainer.getPosition().col));

            hotelLabel.setText(hotelContainer.getFullInfoLabel());
            hotelWidget.setContainerType(hotelContainer.getContainerType());
            hotelWidget.setSelectedBox(new Point(currentPalletWrapper
                .getPosition().row, currentPalletWrapper.getPosition().col));

            palletLabel.setText(currentPalletWrapper.getLabel());
        }
    }

    protected boolean setStatus(PalletCell scanCell)
        throws ApplicationException {
        SampleWrapper expectedSample = scanCell.getExpectedSample();
        String value = scanCell.getValue();
        String positionString = LabelingScheme.RowColToSbs(new RowColPos(
            scanCell.getRow(), scanCell.getCol()));
        if (value == null) {
            // no sample scanned
            if (expectedSample == null) {
                // no existing sample should be there
                return true;
            }
            // sample missing
            scanCell.setStatus(SampleCellStatus.MISSING);
            scanCell.setInformation("Sample " + expectedSample.getInventoryId()
                + " missing");
            scanCell.setTitle("?");
            appendLog("MISSING: " + expectedSample.getInventoryId()
                + " missing from " + positionString);
            return false;
        }
        if (value.isEmpty()) {
            // scan failed ? (not sure could happened...
            scanCell.setStatus(SampleCellStatus.ERROR);
            scanCell.setInformation("Error retrieving bar code");
            scanCell.setTitle("?");
            return false;
        }
        List<SampleWrapper> samples = SampleWrapper.getSamplesInSite(
            appService, value, SessionManager.getInstance()
                .getCurrentSiteWrapper());
        if (samples.size() == 0) {
            // sample not found in site (not yet linked ?)
            scanCell.setStatus(SampleCellStatus.ERROR);
            scanCell.setInformation("Sample not found");
            scanCell.setTitle("-");
            appendLogError(positionString, "tube " + value
                + " not linked to any patient");
            return false;
        } else if (samples.size() == 1) {
            SampleWrapper foundSample = samples.get(0);
            if (expectedSample != null
                && !foundSample.getId().equals(expectedSample.getId())) {
                // sample found but another sample already at this position
                scanCell.setStatus(SampleCellStatus.ERROR);
                scanCell
                    .setInformation("Sample different from the one registered at this position");
                scanCell.setTitle("!");
                appendLogError(positionString, "Expected inventoryId "
                    + expectedSample.getInventoryId() + " from patient "
                    + expectedSample.getPatientVisit().getPatient().getNumber()
                    + " -- Found inventoryId " + foundSample.getInventoryId()
                    + " from patient "
                    + foundSample.getPatientVisit().getPatient().getNumber());
                return false;
            }
            scanCell.setSample(foundSample);
            if (expectedSample != null) {
                // sample scanned is already registered at this position
                // (everything is ok !)
                scanCell.setStatus(SampleCellStatus.FILLED);
                scanCell.setSample(expectedSample);
            } else {
                if (foundSample.hasParent()
                    && !foundSample.getParent().getId().equals(
                        currentPalletWrapper.getId())) {
                    // the scanned sample has already a position but a different
                    // one
                    scanCell.setStatus(SampleCellStatus.ERROR);
                    String expectedPosition = foundSample
                        .getPositionString(true);
                    scanCell
                        .setInformation("Sample registered on another pallet with position "
                            + expectedPosition + "!");
                    scanCell.setTitle("!");
                    appendLogError(positionString, " tube " + value
                        + " registered on another pallet at position "
                        + expectedPosition);
                    return false;
                }
                // sample is a new one !
                if (!currentPalletWrapper.canHoldSample(foundSample)) {
                    // pallet can't hold this sample type
                    scanCell.setStatus(SampleCellStatus.ERROR);
                    scanCell.setInformation("This pallet type "
                        + currentPalletWrapper.getContainerType().getName()
                        + " can't hold this sample of type "
                        + foundSample.getSampleType().getName());
                    appendLogError(positionString, "This pallet type "
                        + currentPalletWrapper.getContainerType().getName()
                        + " can't hold this sample of type "
                        + foundSample.getSampleType().getName());
                    return false;
                }
                scanCell.setStatus(SampleCellStatus.NEW);
            }
            scanCell.setTitle(foundSample.getPatientVisit().getPatient()
                .getNumber());
            return true;
        } else {
            Assert
                .isTrue(false, "InventoryId " + value + " should be unique !");
            appendLogError(positionString,
                "More than one sample found with the inventoryId " + value);
            return false;
        }
    }

    private void appendLogError(String position, String msg) {
        appendLog("ERROR in " + position + ": " + msg);
    }

    @Override
    protected void saveForm() throws Exception {
        currentPalletWrapper.persist();
        int totalNb = 0;
        try {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    PalletCell cell = cells[i][j];
                    if (cell != null) {
                        SampleWrapper sample = cell.getSample();
                        if (sample != null) {
                            sample.setPosition(new RowColPos(i, j));
                            sample.setParent(currentPalletWrapper);
                            sample.persist();
                            totalNb++;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            scanLaunchedValue.setValue(false);
            throw ex;
        }
        appendLog("----");
        appendLog("SCAN-ASSIGN: " + totalNb + " samples assign to pallet "
            + LabelingScheme.getPositionString(currentPalletWrapper));
        setSaved(true);
    }

    @Override
    public void reset() {
        freezerWidget.setSelectedBox(null);
        hotelWidget.setSelectedBox(null);
        palletWidget.setScannedElements(null);
        cells = null;
        scanLaunchedValue.setValue(false);
        initPalletValues();
        if (onlyTypePossible != null) {
            currentPalletWrapper.setContainerType(onlyTypePossible);
        } else {
            setContainerType();
        }
        setDirty(false);
    }

    private void initPalletValues() {
        try {
            currentPalletWrapper.reset();
            currentPalletWrapper.setActivityStatus("Active");
            currentPalletWrapper.setSite(SessionManager.getInstance()
                .getCurrentSiteWrapper());
        } catch (Exception e) {
            LOGGER.error("Error while reseting pallet values", e);
        }
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
            scanButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
            if (!BioBankPlugin.getDefault().isValidPlateBarcode(
                plateToScanText.getText())) {
                scanButton.setEnabled(false);
            } else {
                scanButton.setEnabled(!palletCodeText.getText().isEmpty()
                    && !palletPositionText.getText().isEmpty());
            }
            if (palletTypesViewer != null
                && palletTypesViewer.getCCombo().getSelectionIndex() == -1) {
                scanButton.setEnabled(false);
            }
        }
    }

    @Override
    protected String getOkMessage() {
        return "Assigning samples location.";
    }

    @Override
    public void setFocus() {
        palletCodeText.setFocus();
    }

    /**
     * From the pallet product barcode, get existing information from database
     */
    private boolean checkPallet() throws Exception {
        currentPalletSamples = null;
        boolean pursue = true;
        boolean needToCheckPosition = true;
        appendLog("----");
        appendLog("Checking product barcode "
            + currentPalletWrapper.getProductBarcode());
        ContainerWrapper palletFound = ContainerWrapper
            .getContainerWithProductBarcodeInSite(appService, SessionManager
                .getInstance().getCurrentSiteWrapper(), currentPalletWrapper
                .getProductBarcode());
        if (palletFound != null) {
            appendLog("Checking label position "
                + currentPalletWrapper.getLabel());
            // a pallet with this product barcode already exists in the database
            if (palletFound.getLabel().equals(currentPalletWrapper.getLabel())) {
                // in this case, the position already contains the same pallet.
                // Don't need to check it
                // need to use the container object retrieved from the
                // database !
                currentPalletWrapper.setWrappedObject(palletFound
                    .getWrappedObject());
                needToCheckPosition = false;
            } else {
                pursue = MessageDialog.openConfirm(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(),
                    "Pallet product barcode",
                    "This pallet is already registered in the database in the position "
                        + palletFound.getLabel()
                        + ". Do you want to move it to new position "
                        + currentPalletWrapper.getLabel() + "?");
                if (pursue) {
                    // need to use the container object retrieved from the
                    // database !
                    palletFound.setLabel(currentPalletWrapper.getLabel());
                    currentPalletWrapper.setWrappedObject(palletFound
                        .getWrappedObject());
                } else {
                    return false;
                }
            }
            // get the existing samples to be able to check added an missing
            // samples
            appendLog("Pallet container type used: "
                + currentPalletWrapper.getContainerType().getName());
            currentPalletSamples = new SampleWrapper[currentPalletWrapper
                .getContainerType().getRowCapacity()][currentPalletWrapper
                .getContainerType().getColCapacity()];
            List<SampleWrapper> samples = currentPalletWrapper.getSamples();
            if (samples != null) {
                for (SampleWrapper sample : samples) {
                    RowColPos position = sample.getPosition();
                    currentPalletSamples[position.row][position.col] = sample;
                }
            }
        }
        if (needToCheckPosition) {
            pursue = checkAndSetPosition();
        }
        return pursue;
    }

    /**
     * Check if position is available and set the ContainerPosition if it is
     * free
     * 
     * @return true if was able to create the ContainerPosition
     */
    private boolean checkAndSetPosition() throws Exception {
        appendLog("Checking position label " + currentPalletWrapper.getLabel());
        ContainerWrapper containerAtPosition = currentPalletWrapper
            .getContainer(currentPalletWrapper.getLabel(), currentPalletWrapper
                .getContainerType());
        if (containerAtPosition == null) {
            currentPalletWrapper.computePositionFromLabel();
            return true;
        } else {
            String barcode = containerAtPosition.getProductBarcode();
            if (barcode == null) {
                barcode = "[none]";
            }
            BioBankPlugin.openError("Position error",
                "There is already a different pallet (product barcode = "
                    + barcode + ") on this position");
            appendLog("Pallet with product barcode " + barcode
                + " is already in position " + currentPalletWrapper.getLabel());
            return false;
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected String getActivityTitle() {
        return "Scan assign activity";
    }

    private void setContainerType() {
        IStructuredSelection selection = (IStructuredSelection) palletTypesViewer
            .getSelection();
        if (selection.size() > 0) {
            currentPalletWrapper
                .setContainerType((ContainerTypeWrapper) selection
                    .getFirstElement());
        }
    }
}
