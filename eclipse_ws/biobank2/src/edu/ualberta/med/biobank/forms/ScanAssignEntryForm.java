package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.utils.SiteUtils;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class ScanAssignEntryForm extends AbstractPatientAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanAssignEntryForm";

    private ComboViewer palletTypesViewer;
    private Text plateToScanText;
    private Text palletCodeText;
    private Text palletPositionText;
    private Button scanButton;

    private Label freezerLabel;
    private ViewContainerWidget freezerWidget;
    private Label palletLabel;
    private ScanPalletWidget palletWidget;
    private Label hotelLabel;
    private ViewContainerWidget hotelWidget;

    private IObservableValue plateToScanValue = new WritableValue("",
        String.class);
    private IObservableValue palletPositionValue = new WritableValue("",
        String.class);
    private IObservableValue scanLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private PalletCell[][] cells;

    private Study currentStudy;

    protected ContainerWrapper currentPalletWrapper;

    protected Sample[][] currentPalletSamples;

    // for debugging only :
    private Button existsButton;
    private Button notexistsButton;

    private CancelConfirmWidget cancelConfirmWidget;

    @Override
    protected void init() {
        setPartName("Scan Assign");
        currentPalletWrapper = new ContainerWrapper(appService, new Container());
        initPalletValues();
    }

    @Override
    protected void createFormContent() {
        form.setText("Assign samples locations using the scanner");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();

        createContainersSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);
        cancelConfirmWidget.showCloseButton(true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanLaunchedValue, "Scanner should be launched");
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue, "Error in scanning result");
    }

    private void createFieldsSection() {
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
            PojoObservables
                .observeValue(currentPalletWrapper, "productBarcode"),
            NonEmptyString.class, "Enter pallet position code");
        palletCodeText.removeKeyListener(keyListener);
        palletCodeText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        palletPositionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Pallet label", null, palletPositionValue,
            NonEmptyString.class, "Enter position code");
        palletPositionText.removeKeyListener(keyListener);
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        plateToScanText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Plate to scan", new String[0],
            plateToScanValue, ScannerBarcodeValidator.class,
            "Enter a valid plate barcode");
        plateToScanText.removeKeyListener(keyListener);
        plateToScanText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        if (!BioBankPlugin.isRealScanEnabled()) {
            gd.widthHint = 250;
            Composite comp = toolkit.createComposite(fieldsComposite);
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

        scanButton = toolkit.createButton(fieldsComposite, "Scan", SWT.PUSH);
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
        freezerWidget.setGridSizes(5, 10, ScanPalletWidget.PALLET_WIDTH, 100);
        setFreezerWidgetType();

        Composite hotelComposite = toolkit.createComposite(containersComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel");
        hotelWidget = new ViewContainerWidget(hotelComposite);
        toolkit.adapt(hotelWidget);
        hotelWidget.setGridSizes(11, 1, 100,
            ScanPalletWidget.PALLET_HEIGHT_AND_LEGEND);
        setHotelWidgetType();

        Composite palletComposite = toolkit
            .createComposite(containersComposite);
        palletComposite.setLayout(getNeutralGridLayout());
        palletComposite.setLayoutData(new GridData());
        palletLabel = toolkit.createLabel(palletComposite, "Pallet");
        palletWidget = new ScanPalletWidget(palletComposite);
        toolkit.adapt(palletWidget);

        showOnlyPallet(true);
    }

    private void setHotelWidgetType() {
        try {
            List<ContainerType> types = SiteUtils.getContainerTypesInSite(
                appService, currentPalletWrapper.getSite(), "Hotel");
            if (types.size() > 0) {
                freezerWidget.setContainerType(types.get(0));
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setFreezerWidgetType() {
        try {
            List<ContainerType> types = SiteUtils.getContainerTypesInSite(
                appService, currentPalletWrapper.getSite(), "Freezer");
            if (types.size() > 0) {
                freezerWidget.setContainerType(types.get(0));
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private GridLayout getNeutralGridLayout() {
        GridLayout layout;
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    private void createContainerTypeSection(Composite parent) {
        try {
            List<ContainerType> palletContainerTypes = SiteUtils
                .getContainerTypesInSite(appService, currentPalletWrapper
                    .getSite(), "Pallet");
            if (palletContainerTypes.size() == 0) {
                BioBankPlugin
                    .openError("No Pallet defined ?",
                        "No container type found with name starting with Pallet...");
            } else if (palletContainerTypes.size() == 1) {
                currentPalletWrapper.setContainerType(palletContainerTypes
                    .get(0));
            } else {
                palletTypesViewer = createCComboViewerWithNoSelectionValidator(
                    parent, "Pallet Container Type", palletContainerTypes,
                    "A pallet type should be selected");
            }
        } catch (ApplicationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    protected void scan() throws Exception {
        boolean showResult = checkPallet();
        if (showResult) {
            showOnlyPallet(false);
            if (BioBankPlugin.isRealScanEnabled()) {
                int plateNum = BioBankPlugin.getDefault().getPlateNumber(
                    plateToScanValue.getValue().toString());
                cells = PalletCell.convertArray(ScannerConfigPlugin
                    .scan(plateNum));
            } else {
                if (notexistsButton.getSelection()) {
                    cells = PalletCell.getRandomScanProcessNotInPallet(
                        appService, SessionManager.getInstance()
                            .getCurrentSite());
                } else if (existsButton.getSelection()) {
                    cells = PalletCell.getRandomScanProcessAlreadyInPallet(
                        appService, SessionManager.getInstance()
                            .getCurrentSite());
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
                    result = setStatus(cells[i][j], positionSample) && result;
                }
            }

            scanValidValue.setValue(result);
            palletWidget.setScannedElements(cells);
            showStudyInformation();
            scanLaunchedValue.setValue(true);
            setDirty(true);
        } else {
            showOnlyPallet(true);
        }
        showPalletPosition();
        scanButton.traverse(SWT.TRAVERSE_TAB_NEXT);
        form.layout(true, true);
    }

    private void showOnlyPallet(boolean show) {
        freezerLabel.getParent().setVisible(!show);
        ((GridData) freezerLabel.getParent().getLayoutData()).exclude = show;
        hotelLabel.getParent().setVisible(!show);
        ((GridData) hotelLabel.getParent().getLayoutData()).exclude = show;
    }

    protected void showPalletPosition() {
        ContainerPosition palletPosition = currentPalletWrapper.getPosition();
        if (palletPosition != null) {
            ContainerWrapper hotelContainer = new ContainerWrapper(appService,
                palletPosition.getParentContainer());
            ContainerPosition hotelPosition = hotelContainer.getPosition();
            ContainerWrapper freezerContainer = new ContainerWrapper(
                appService, hotelPosition.getParentContainer());

            freezerLabel.setText(freezerContainer.getFullInfoLabel());
            freezerWidget.setContainerType(freezerContainer.getContainerType());
            freezerWidget.setSelectedBox(new Point(hotelPosition
                .getPositionDimensionOne(), hotelPosition
                .getPositionDimensionTwo()));

            hotelLabel.setText(hotelContainer.getFullInfoLabel());
            hotelWidget.setContainerType(hotelContainer.getContainerType());
            hotelWidget.setSelectedBox(new Point(palletPosition
                .getPositionDimensionOne(), palletPosition
                .getPositionDimensionTwo()));

            palletLabel.setText(currentPalletWrapper.getLabel());
        }
    }

    /**
     * if a study is found, show the name in title
     */
    protected void showStudyInformation() {
        // FIXME show the study in the nice place !
        // if (currentStudy == null) {
        // setPartName("Assigning samples location");
        // } else {
        // setPartName("Assigning samples location for study "
        // + currentStudy.getNameShort());
        // }
    }

    protected boolean setStatus(PalletCell scanCell, Sample positionSample) {
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
        List<Sample> samples = SiteUtils.getSamplesInSite(appService, value,
            SessionManager.getInstance().getCurrentSite());
        if (samples.size() == 0) {
            scanCell.setStatus(SampleCellStatus.ERROR);
            scanCell.setInformation("Sample not found");
            scanCell.setTitle("-");
            return false;
        } else if (samples.size() == 1) {
            Sample sample = samples.get(0);
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
                        .equals(currentPalletWrapper.getId())) {
                    scanCell.setStatus(SampleCellStatus.ERROR);
                    String posString = ModelUtils.getSamplePosition(sample);
                    scanCell
                        .setInformation("Sample registered on another pallet with position "
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
            Assert
                .isTrue(false, "InventoryId " + value + " should be unique !");
            return false;
        }
    }

    private ContainerType getSelectedPalletType() {
        IStructuredSelection selection = (IStructuredSelection) palletTypesViewer
            .getSelection();
        if (selection.size() > 0) {
            return (ContainerType) selection.getFirstElement();
        }
        return null;
    }

    @Override
    protected void saveForm() throws Exception {
        currentPalletWrapper.persist();

        List<SDKQuery> queries = new ArrayList<SDKQuery>();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                PalletCell cell = cells[i][j];
                // cell.getStatus()
                if (cell != null) {
                    Sample sample = cell.getSample();
                    if (sample != null && sample.getSamplePosition() == null) {
                        SamplePosition samplePosition = new SamplePosition();
                        samplePosition.setPositionDimensionOne(i);
                        samplePosition.setPositionDimensionTwo(j);
                        samplePosition.setContainer(currentPalletWrapper
                            .getWrappedObject());
                        samplePosition.setSample(sample);
                        sample.setSamplePosition(samplePosition);
                        queries.add(new UpdateExampleQuery(sample));
                    }
                }
            }
        }
        appService.executeBatchQuery(queries);
        setSaved(true);
    }

    @Override
    public void cancelForm() {
        freezerWidget.setSelectedBox(null);
        hotelWidget.setSelectedBox(null);
        palletWidget.setScannedElements(null);
        cells = null;
        currentStudy = null;
        scanLaunchedValue.setValue(false);
        initPalletValues();
        setDirty(false);
    }

    private void initPalletValues() {
        try {
            currentPalletWrapper.reset();
            currentPalletWrapper.setActivityStatus("Active");
            currentPalletWrapper.setSite(SessionManager.getInstance()
                .getCurrentSite());
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while reseting pallet values");
        }
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            scanButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            if (!BioBankPlugin.getDefault().isValidPlateBarcode(
                plateToScanText.getText())) {
                scanButton.setEnabled(false);
            } else {
                scanButton.setEnabled(!palletCodeText.getText().isEmpty()
                    && !palletPositionText.getText().isEmpty());
            }
            if (palletTypesViewer != null && getSelectedPalletType() == null) {
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
        if (plateToScanValue.getValue().toString().isEmpty()) {
            plateToScanText.setFocus();
        }
    }

    /**
     * From the pallet product barcode, get existing information form database
     */
    private boolean checkPallet() throws Exception {
        currentPalletSamples = null;
        boolean pursue = true;
        boolean needToCheckPosition = true;
        Container palletFound = ContainerWrapper.getContainerWithTypeInSite(
            appService, SessionManager.getInstance().getCurrentSite(),
            currentPalletWrapper.getProductBarcode(), "Pallet");
        if (palletFound == null) {
            // a pallet with this product barcode does not exists yet on the
            // database (for the current site)
            if (currentPalletWrapper.getContainerType() == null
                && palletTypesViewer != null) {
                currentPalletWrapper.setContainerType(getSelectedPalletType());
            }
            currentPalletWrapper.setLabel(palletPositionText.getText());
        } else {
            // a pallet with this product barcode already exists in the database
            if (palletFound.getLabel().equals(currentPalletWrapper.getLabel())) {
                // in this case, the position already contains the same pallet.
                // Don't need to check it
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
                    // need to use the container object retrieve from the
                    // database !
                    palletFound.setLabel(currentPalletWrapper.getLabel());
                    currentPalletWrapper.setWrappedObject(palletFound);
                } else {
                    return false;
                }
            }
            // get the existing samples to be able to check added an missing
            // samples
            Capacity palletCapacity = currentPalletWrapper.getContainerType()
                .getCapacity();
            currentPalletSamples = new Sample[palletCapacity
                .getDimensionOneCapacity()][palletCapacity
                .getDimensionTwoCapacity()];
            for (SamplePosition position : currentPalletWrapper
                .getSamplePositionCollection()) {
                currentPalletSamples[position.getPositionDimensionOne()][position
                    .getPositionDimensionTwo()] = position.getSample();
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
        String positionLabel = palletPositionValue.getValue().toString();
        Container containerAtPosition = ContainerWrapper
            .getContainerWithTypeInSite(appService, currentPalletWrapper
                .getSite(), positionLabel, "Pallet");
        if (containerAtPosition == null) {
            currentPalletWrapper.setNewPositionFromLabel("Freezer");
            return true;
        } else {
            // 
            BioBankPlugin.openError("Position error",
                "There is already a different pallet (product barcode = "
                    + containerAtPosition.getProductBarcode()
                    + ") on this position");
            return false;
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected void print() {
        // FIXME implement print functionnality
        System.out.println("PRINT activity");
    }

}
