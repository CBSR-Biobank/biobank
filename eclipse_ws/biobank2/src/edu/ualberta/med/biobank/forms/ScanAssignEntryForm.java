package edu.ualberta.med.biobank.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.PalletBarCodeValidator;
import edu.ualberta.med.biobank.widgets.grids.GridContainerWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ScanAssignEntryForm extends AbstractPalletAliquotAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanAssignEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ScanAssignEntryForm.class.getName());

    private Text palletproductBarcodeText;
    private Text palletPositionText;
    private ComboViewer palletTypesViewer;

    private Label freezerLabel;
    private GridContainerWidget freezerWidget;
    private Label palletLabel;
    private ScanPalletWidget palletWidget;
    private Label hotelLabel;
    private GridContainerWidget hotelWidget;

    // true if no errors in aliquots scan and process
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    protected ContainerWrapper currentPalletWrapper;

    // for debugging only (fake scan) :
    private Button fakeScanLinkedOnlyButton;
    private Button fakeScanLinkedAssignButton;

    private Composite containersComposite;

    // pallet found with given product barcode
    protected ContainerWrapper palletFoundWithProductBarcode;

    // true if the pallet is a new one
    private boolean newPallet;

    // if the pallet was already in another position, this is its old label
    private String oldPalletLabel;

    // global state of the pallet process
    private AliquotCellStatus currentScanState;

    // contains moved and missing aliquots. a missing one is set to into the
    // missing rowColPos. A moved one is set into its old RowColPos
    private Map<RowColPos, PalletCell> movedAndMissingAliquotsFromPallet = new HashMap<RowColPos, PalletCell>();

    private boolean noFieldModification = true;

    private ModifyListener textModifiedListener;

    @Override
    protected void init() {
        super.init();
        setPartName(Messages.getString("ScanAssign.tabTitle")); //$NON-NLS-1$
        currentPalletWrapper = new ContainerWrapper(appService);
        initPalletValues();
        textModifiedListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                noFieldModification = false;
                setScanNotLauched();
            }
        };
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ScanAssign.formTitle")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();

        createContainersVisualisationSection();

        createCancelConfirmWidget();

        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue, Messages.getString("ScanAssign.scanErrorMsg")); //$NON-NLS-1$
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

        palletproductBarcodeText = (Text) createBoundWidgetWithLabel(
            fieldsComposite, Text.class, SWT.NONE, Messages
                .getString("ScanAssign.productBarcode.label"), //$NON-NLS-1$
            null, BeansObservables.observeValue(currentPalletWrapper,
                "productBarcode"), new NonEmptyStringValidator( //$NON-NLS-1$
                Messages.getString("ScanAssign.productBarcode.validationMsg"))); //$NON-NLS-1$
        palletproductBarcodeText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletproductBarcodeText.setLayoutData(gd);
        firstControl = palletproductBarcodeText;
        palletproductBarcodeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                getExistingPalletFromProductBarcode();
            }
        });
        palletproductBarcodeText.addModifyListener(textModifiedListener);

        palletPositionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, Messages
                .getString("ScanAssign.palletLabel.label"), null, //$NON-NLS-1$
            BeansObservables.observeValue(currentPalletWrapper, "label"), //$NON-NLS-1$
            new PalletBarCodeValidator(Messages
                .getString("ScanAssign.palletLabel.validationMsg"))); //$NON-NLS-1$
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletPositionText.setLayoutData(gd);
        palletPositionText.addModifyListener(textModifiedListener);

        createPalletTypesViewer(fieldsComposite);

        createScanComponents(fieldsComposite);
    }

    private void createPalletTypesViewer(Composite parent) throws Exception {
        List<ContainerTypeWrapper> palletContainerTypes = getPalletContainerTypes();
        palletTypesViewer = createComboViewerWithNoSelectionValidator(parent,
            Messages.getString("ScanAssign.palletType.label"), //$NON-NLS-1$
            palletContainerTypes, null, Messages
                .getString("ScanAssign.palletType.validationMsg")); //$NON-NLS-1$
        palletTypesViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) palletTypesViewer
                        .getSelection();
                    if (selection.size() > 0) {
                        currentPalletWrapper
                            .setContainerType((ContainerTypeWrapper) selection
                                .getFirstElement());
                    }
                    setScanNotLauched();
                    noFieldModification = false;
                }
            });
        if (palletContainerTypes.size() == 1) {
            currentPalletWrapper.setContainerType(palletContainerTypes.get(0));
            palletTypesViewer.setSelection(new StructuredSelection(
                palletContainerTypes.get(0)));
        }
    }

    /**
     * get container with type name that contains 'palletNameContains'
     */
    private List<ContainerTypeWrapper> getPalletContainerTypes()
        throws ApplicationException {
        List<ContainerTypeWrapper> palletContainerTypes = ContainerTypeWrapper
            .getContainerTypesInSite(appService,
                currentPalletWrapper.getSite(), palletNameContains, false);
        if (palletContainerTypes.size() == 0) {
            BioBankPlugin.openAsyncError(Messages
                .getString("ScanAssign.dialog.noPalletFoundError.title"), //$NON-NLS-1$
                Messages.getFormattedString(
                    "ScanAssign.dialog.noPalletFoundError.msg", //$NON-NLS-1$
                    palletNameContains));
        }
        return palletContainerTypes;
    }

    @Override
    protected void createFakeOptions(Composite fieldsComposite) {
        Composite comp = toolkit.createComposite(fieldsComposite);
        comp.setLayout(new GridLayout());
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        comp.setLayoutData(gd);
        fakeScanLinkedAssignButton = toolkit.createButton(comp,
            "Select linked only aliquots", SWT.RADIO); //$NON-NLS-1$
        fakeScanLinkedAssignButton.setSelection(true);
        fakeScanLinkedOnlyButton = toolkit.createButton(comp,
            "Select linked and assigned aliquots", SWT.RADIO); //$NON-NLS-1$
    }

    private void createContainersVisualisationSection() {
        containersComposite = toolkit.createComposite(form.getBody());
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
        freezerLabel = toolkit.createLabel(freezerComposite, "Freezer"); //$NON-NLS-1$
        freezerLabel.setLayoutData(new GridData());
        freezerWidget = new GridContainerWidget(freezerComposite);
        toolkit.adapt(freezerWidget);
        freezerWidget.setGridSizes(5, 10, ScanPalletWidget.PALLET_WIDTH, 100);

        Composite hotelComposite = toolkit.createComposite(containersComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel"); //$NON-NLS-1$
        hotelWidget = new GridContainerWidget(hotelComposite);
        toolkit.adapt(hotelWidget);
        hotelWidget.setGridSizes(11, 1, 100,
            ScanPalletWidget.PALLET_HEIGHT_AND_LEGEND);

        Composite palletComposite = toolkit
            .createComposite(containersComposite);
        palletComposite.setLayout(getNeutralGridLayout());
        palletComposite.setLayoutData(new GridData());
        palletLabel = toolkit.createLabel(palletComposite, "Pallet"); //$NON-NLS-1$
        palletWidget = new ScanPalletWidget(palletComposite);
        toolkit.adapt(palletWidget);

        showOnlyPallet(true);
    }

    private GridLayout getNeutralGridLayout() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    private void getExistingPalletFromProductBarcode() {
        try {
            palletFoundWithProductBarcode = ContainerWrapper
                .getContainerWithProductBarcodeInSite(appService,
                    SessionManager.getInstance().getCurrentSite(),
                    currentPalletWrapper.getProductBarcode());
            if (palletFoundWithProductBarcode == null) {
                // no pallet found with this barcode
                palletTypesViewer.getCombo().setEnabled(true);
            } else {
                // a pallet has been found
                palletPositionText.setText(palletFoundWithProductBarcode
                    .getLabel());
                palletPositionText.selectAll();
                palletTypesViewer.getCombo().setEnabled(false);
                palletTypesViewer.setSelection(new StructuredSelection(
                    palletFoundWithProductBarcode.getContainerType()));
            }
        } catch (Exception ex) {
            BioBankPlugin.openError("Product barcode", ex); //$NON-NLS-1$
        }
    }

    @Override
    protected void scanAndProcessResult(IProgressMonitor monitor)
        throws Exception {
        boolean canLaunchScan = false;
        if (isRescanMode() && noFieldModification) {
            canLaunchScan = true;
        } else {
            // if another scan has been done on this same form, need to
            // reset values set before
            reset(true);
            canLaunchScan = checkPallet();
        }
        if (canLaunchScan) {
            showOnlyPallet(false);
            launchScan(monitor);
            processScanResult(monitor);
            palletWidget.setCells(cells);
            setDirty(true);
        } else {
            palletWidget.setCells(new TreeMap<RowColPos, PalletCell>());
            showOnlyPallet(true);
            scanValidValue.setValue(false);
        }
        displayPalletPositions();
        focusOnCancelConfirmText();
        setRescanMode();
        containersComposite.layout(true, true);
    }

    @Override
    protected void setScanOk(boolean scanOk) {
        scanValidValue.setValue(scanOk);
    }

    @Override
    protected void launchFakeScan() throws Exception {
        if (fakeScanLinkedAssignButton.getSelection()) {
            cells = PalletCell.getRandomAliquotsNotAssigned(appService,
                SessionManager.getInstance().getCurrentSite().getId());
        } else if (fakeScanLinkedOnlyButton.getSelection()) {
            cells = PalletCell.getRandomAliquotsAlreadyAssigned(appService,
                SessionManager.getInstance().getCurrentSite().getId());
        }
    }

    /**
     * go through cells retrieved from scan, set status of aliquots to be added
     * to the current pallet
     */
    private void processScanResult(IProgressMonitor monitor) throws Exception {
        Map<RowColPos, AliquotWrapper> expectedAliquots = currentPalletWrapper
            .getAliquots();
        currentScanState = AliquotCellStatus.EMPTY;
        for (int row = 0; row < currentPalletWrapper.getRowCapacity(); row++) {
            for (int col = 0; col < currentPalletWrapper.getColCapacity(); col++) {
                RowColPos rcp = new RowColPos(row, col);
                monitor.subTask("Processing position "
                    + LabelingScheme.rowColToSbs(rcp));
                PalletCell cell = cells.get(rcp);
                if (!isRescanMode()
                    || (cell == null
                        || cell.getStatus() == AliquotCellStatus.EMPTY
                        || cell.getStatus() == AliquotCellStatus.ERROR || cell
                        .getStatus() == AliquotCellStatus.MISSING)) {
                    AliquotWrapper expectedAliquot = null;
                    if (expectedAliquots != null) {
                        expectedAliquot = expectedAliquots.get(rcp);
                        if (expectedAliquot != null) {
                            if (cell == null) {
                                cell = new PalletCell(new ScanCell(row, col,
                                    null));
                                cells.put(rcp, cell);
                            }
                            cell.setExpectedAliquot(expectedAliquot);
                        }
                    }
                    if (cell != null) {
                        processCellStatus(cell);
                    }
                }
                AliquotCellStatus newStatus = AliquotCellStatus.EMPTY;
                if (cell != null) {
                    newStatus = cell.getStatus();
                }
                currentScanState = currentScanState.mergeWith(newStatus);
            }
        }
        scanValidValue.setValue(currentScanState != AliquotCellStatus.ERROR);
    }

    private void showOnlyPallet(boolean show) {
        freezerLabel.getParent().setVisible(!show);
        ((GridData) freezerLabel.getParent().getLayoutData()).exclude = show;
        hotelLabel.getParent().setVisible(!show);
        ((GridData) hotelLabel.getParent().getLayoutData()).exclude = show;
    }

    protected void displayPalletPositions() {
        if (currentPalletWrapper.hasParent()) {
            ContainerWrapper hotelContainer = currentPalletWrapper.getParent();
            ContainerWrapper freezerContainer = hotelContainer.getParent();

            freezerLabel.setText(freezerContainer.getFullInfoLabel());
            freezerWidget.setContainerType(freezerContainer.getContainerType());
            freezerWidget.setSelection(hotelContainer.getPosition());

            hotelLabel.setText(hotelContainer.getFullInfoLabel());
            hotelWidget.setContainerType(hotelContainer.getContainerType());
            hotelWidget.setSelection(currentPalletWrapper.getPosition());

            palletLabel.setText(currentPalletWrapper.getLabel());
        }
    }

    /**
     * set the status of the cell
     */
    protected void processCellStatus(PalletCell scanCell) throws Exception {
        AliquotWrapper expectedAliquot = scanCell.getExpectedAliquot();
        String value = scanCell.getValue();
        String positionString = currentPalletWrapper.getLabel()
            + LabelingScheme.rowColToSbs(new RowColPos(scanCell.getRow(),
                scanCell.getCol()));
        if (value == null) { // no aliquot scanned
            updateCellAsMissing(positionString, scanCell, expectedAliquot);
        } else {
            List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
                appService, value, SessionManager.getInstance()
                    .getCurrentSite());
            if (aliquots.size() == 0) {
                updateCellAsNotLinked(positionString, scanCell);
            } else if (aliquots.size() == 1) {
                AliquotWrapper foundAliquot = aliquots.get(0);
                if (expectedAliquot != null
                    && !foundAliquot.equals(expectedAliquot)) {
                    updateCellAsPositionAlreadyTaken(positionString, scanCell,
                        expectedAliquot, foundAliquot);
                } else {
                    scanCell.setAliquot(foundAliquot);
                    if (expectedAliquot != null) {
                        // aliquot scanned is already registered at this
                        // position (everything is ok !)
                        scanCell.setStatus(AliquotCellStatus.FILLED);
                        scanCell.setTitle(foundAliquot.getPatientVisit()
                            .getPatient().getPnumber());
                        scanCell.setAliquot(expectedAliquot);
                    } else {
                        if (currentPalletWrapper.canHoldAliquot(foundAliquot)) {
                            if (foundAliquot.hasParent()) { // moved
                                processCellWithPreviousPosition(scanCell,
                                    positionString, foundAliquot);
                            } else { // new
                                scanCell.setStatus(AliquotCellStatus.NEW);
                                scanCell.setTitle(foundAliquot
                                    .getPatientVisit().getPatient()
                                    .getPnumber());
                            }
                        } else {
                            // pallet can't hold this aliquot type
                            updateCellAsTypeError(positionString, scanCell,
                                foundAliquot);
                        }
                    }
                }
            } else {
                Assert.isTrue(false,
                    "InventoryId " + value + " should be unique !"); //$NON-NLS-1$ //$NON-NLS-2$
                updateCellAsInventoryIdError(positionString, scanCell);
            }
        }
    }

    /**
     * this cell has already a position. Check if it was on the pallet or not
     */
    private void processCellWithPreviousPosition(PalletCell scanCell,
        String positionString, AliquotWrapper foundAliquot) {
        if (foundAliquot.getParent().equals(currentPalletWrapper)) {
            // same pallet
            RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
            if (!foundAliquot.getPosition().equals(rcp)) {
                // moved inside the same pallet
                updateCellAsMoved(positionString, scanCell, foundAliquot);
                RowColPos movedFromPosition = foundAliquot.getPosition();
                PalletCell missingAliquot = movedAndMissingAliquotsFromPallet
                    .get(movedFromPosition);
                if (missingAliquot == null) {
                    // missing position has not yet been processed
                    movedAndMissingAliquotsFromPallet.put(movedFromPosition,
                        scanCell);
                } else {
                    // missing position has already been processed: remove the
                    // MISSING flag
                    missingAliquot.setStatus(AliquotCellStatus.EMPTY);
                    missingAliquot.setTitle("");
                    movedAndMissingAliquotsFromPallet.remove(movedFromPosition);
                }
            }
        } else {
            // old position was on another pallet
            updateCellAsMoved(positionString, scanCell, foundAliquot);
        }
    }

    private void updateCellAsInventoryIdError(String position,
        PalletCell scanCell) {
        String cellValue = scanCell.getValue();

        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages.getFormattedString(
            "ScanAssign.scanStatus.aliquot.inventoryIdError", cellValue)); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.inventoryIdError", position, cellValue); //$NON-NLS-1$
    }

    private void updateCellAsTypeError(String position, PalletCell scanCell,
        AliquotWrapper foundAliquot) {
        String palletType = currentPalletWrapper.getContainerType().getName();
        String sampleType = foundAliquot.getSampleType().getName();

        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages.getFormattedString(
            "ScanAssign.scanStatus.aliquot.typeError", palletType, sampleType)); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.typeError", position, palletType, //$NON-NLS-1$
            sampleType);
    }

    private void updateCellAsMoved(String position, PalletCell scanCell,
        AliquotWrapper foundAliquot) {
        String expectedPosition = foundAliquot.getPositionString();
        if (expectedPosition == null) {
            expectedPosition = "none"; //$NON-NLS-1$
        }

        scanCell.setStatus(AliquotCellStatus.MOVED);
        scanCell.setTitle(foundAliquot.getPatientVisit().getPatient()
            .getPnumber());
        scanCell.setInformation(Messages.getFormattedString(
            "ScanAssign.scanStatus.aliquot.moved", expectedPosition)); //$NON-NLS-1$

        appendLogNLS(
            "ScanAssign.activitylog.aliquot.moved", position, scanCell.getValue(), //$NON-NLS-1$
            expectedPosition);
    }

    /**
     * aliquot found but another aliquot already at this position
     */
    private void updateCellAsPositionAlreadyTaken(String position,
        PalletCell scanCell, AliquotWrapper expectedAliquot,
        AliquotWrapper foundAliquot) {
        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.positionTakenError")); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.positionTaken", position, expectedAliquot //$NON-NLS-1$
                .getInventoryId(), expectedAliquot.getPatientVisit()
                .getPatient().getPnumber(), foundAliquot.getInventoryId(),
            foundAliquot.getPatientVisit().getPatient().getPnumber());
    }

    /**
     * aliquot not found in site (not yet linked ?)
     */
    private void updateCellAsNotLinked(String position, PalletCell scanCell) {
        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.notlinked", position, scanCell.getValue()); //$NON-NLS-1$
    }

    /**
     * aliquot missing
     */
    private void updateCellAsMissing(String position, PalletCell scanCell,
        AliquotWrapper missingAliquot) {
        RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
        PalletCell movedAliquot = movedAndMissingAliquotsFromPallet.get(rcp);
        if (movedAliquot == null) {
            scanCell.setStatus(AliquotCellStatus.MISSING);
            scanCell
                .setInformation(Messages
                    .getFormattedString(
                        "ScanAssign.scanStatus.aliquot.missing", missingAliquot.getInventoryId())); //$NON-NLS-1$
            scanCell.setTitle("?"); //$NON-NLS-1$
            appendLogNLS(
                "ScanAssign.activitylog.aliquot.missing", position, missingAliquot //$NON-NLS-1$
                    .getInventoryId(), missingAliquot.getPatientVisit()
                    .getFormattedDateProcessed(), missingAliquot
                    .getPatientVisit().getPatient().getPnumber());
            movedAndMissingAliquotsFromPallet.put(rcp, scanCell);
        } else {
            movedAndMissingAliquotsFromPallet.remove(rcp);
            scanCell.setStatus(AliquotCellStatus.EMPTY);
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (saveEvenIfAliquotsMissing()) {
            currentPalletWrapper.persist();
            displayPalletPositionInfo();
            int totalNb = 0;
            StringBuffer sb = new StringBuffer("ALIQUOTS ASSIGNED:\n"); //$NON-NLS-1$
            try {
                for (RowColPos rcp : cells.keySet()) {
                    PalletCell cell = cells.get(rcp);
                    if (cell != null
                        && (cell.getStatus() == AliquotCellStatus.NEW || cell
                            .getStatus() == AliquotCellStatus.MOVED)) {
                        AliquotWrapper aliquot = cell.getAliquot();
                        if (aliquot != null) {
                            aliquot.setPosition(rcp);
                            aliquot.setParent(currentPalletWrapper);
                            aliquot.persist();
                            String posStr = aliquot.getPositionString(true,
                                false);
                            if (posStr == null) {
                                posStr = "none"; //$NON-NLS-1$
                            }
                            computeActivityLogMessage(sb, cell, aliquot, posStr);
                            totalNb++;
                        }
                    }
                }
            } catch (Exception ex) {
                setScanNotLauched();
                throw ex;
            }
            appendLog(sb.toString());
            appendLogNLS("ScanAssign.activitylog.save.summary", totalNb, //$NON-NLS-1$
                currentPalletWrapper.getLabel());
            setSaved(true);
        }
    }

    private void computeActivityLogMessage(StringBuffer sb, PalletCell cell,
        AliquotWrapper aliquot, String posStr) {
        PatientVisitWrapper visit = aliquot.getPatientVisit();
        sb.append(Messages.getFormattedString(
            "ScanAssign.activitylog.aliquot.assigned", //$NON-NLS-1$
            posStr, cell.getValue(), aliquot.getSampleType().getName(), visit
                .getPatient().getPnumber(), visit.getFormattedDateProcessed(),
            visit.getShipment().getClinic().getName()));
    }

    private boolean saveEvenIfAliquotsMissing() {
        if (currentScanState == AliquotCellStatus.MISSING
            && movedAndMissingAliquotsFromPallet.size() > 0) {
            boolean save = BioBankPlugin.openConfirm(Messages
                .getString("ScanAssign.dialog.reallySave.title"), //$NON-NLS-1$
                Messages.getString("ScanAssign.dialog.saveWithMissing.msg")); //$NON-NLS-1$
            if (save) {
                return true;
            } else {
                setDirty(true);
                return false;
            }
        }
        return true;
    }

    private void displayPalletPositionInfo() {
        String productBarcode = currentPalletWrapper.getProductBarcode();
        String containerType = currentPalletWrapper.getContainerType()
            .getName();
        String palletLabel = currentPalletWrapper.getLabel();
        if (oldPalletLabel != null) {
            appendLogNLS("ScanAssign.activitylog.pallet.moved", //$NON-NLS-1$
                productBarcode, containerType, oldPalletLabel, palletLabel);
        } else if (newPallet) {
            appendLogNLS("ScanAssign.activitylog.pallet.added", //$NON-NLS-1$
                productBarcode, containerType, palletLabel);
        }
    }

    @Override
    public void reset() throws Exception {
        reset(false);
    }

    public void reset(boolean beforeScan) throws Exception {
        String productBarcode = ""; //$NON-NLS-1$
        String label = ""; //$NON-NLS-1$
        ContainerTypeWrapper type = null;

        if (beforeScan) { // keep fields values
            productBarcode = currentPalletWrapper.getProductBarcode();
            label = currentPalletWrapper.getLabel();
            type = currentPalletWrapper.getContainerType();
            currentPalletWrapper.resetToNewObject();
            noFieldModification = true;
        } else {
            if (palletTypesViewer != null) {
                palletTypesViewer.getCombo().deselectAll();
            }
            removeRescanMode();
            freezerWidget.setSelection(null);
            hotelWidget.setSelection(null);
            palletWidget.setCells(null);
        }
        movedAndMissingAliquotsFromPallet.clear();
        setScanNotLauched();
        initPalletValues();

        if (beforeScan) { // re-apply fields values
            currentPalletWrapper.setProductBarcode(productBarcode);
            currentPalletWrapper.setLabel(label);
            currentPalletWrapper.setContainerType(type);
        } else {
            setDirty(false);
        }
    }

    private void initPalletValues() {
        try {
            currentPalletWrapper.reset();
            currentPalletWrapper.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            currentPalletWrapper.setSite(SessionManager.getInstance()
                .getCurrentSite());
        } catch (Exception e) {
            logger.error("Error while reseting pallet values", e); //$NON-NLS-1$
        }
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        super.handleStatusChanged(status);
        if (status.getSeverity() != IStatus.OK) {
            if (isPlateValid()) {
                enableScan(!palletproductBarcodeText.getText().isEmpty()
                    && !palletPositionText.getText().isEmpty());
            }
            if (palletTypesViewer != null
                && palletTypesViewer.getCombo().getSelectionIndex() == -1) {
                enableScan(false);
            }
        }
    }

    @Override
    protected String getOkMessage() {
        return Messages.getString("ScanAssign.okMessage"); //$NON-NLS-1$
    }

    /**
     * From the pallet product barcode, get existing information from database
     */
    private boolean checkPallet() throws Exception {
        boolean canContinue = true;
        oldPalletLabel = null;
        newPallet = true;
        boolean needToCheckPosition = true;
        appendLogNLS("ScanAssign.activitylog.pallet.checkingProductBarcode", //$NON-NLS-1$
            currentPalletWrapper.getProductBarcode());
        if (palletFoundWithProductBarcode != null) {
            // a pallet with this product barcode already exists in the
            // database.
            appendLogNLS("ScanAssign.activitylog.pallet.checkLabel", //$NON-NLS-1$
                currentPalletWrapper.getLabel());
            if (palletFoundWithProductBarcode.getLabel().equals(
                currentPalletWrapper.getLabel())) {
                // The position already contains this pallet. Don't need to
                // check it. Need to use exact same retrieved wrappedObject.
                currentPalletWrapper
                    .initObjectWith(palletFoundWithProductBarcode);
                currentPalletWrapper.reset();
                needToCheckPosition = false;
                newPallet = false;
            } else {
                canContinue = openDialogPalletMoved();
                if (canContinue) {
                    // Move the pallet.
                    // Need to use exact same retrieved wrappedObject.
                    oldPalletLabel = palletFoundWithProductBarcode.getLabel();
                    palletFoundWithProductBarcode.setLabel(currentPalletWrapper
                        .getLabel());
                    currentPalletWrapper
                        .initObjectWith(palletFoundWithProductBarcode);
                    appendLogNLS(
                        "ScanAssign.activitylog.pallet.moveInfo", //$NON-NLS-1$
                        currentPalletWrapper.getProductBarcode(),
                        palletFoundWithProductBarcode.getLabel(),
                        currentPalletWrapper.getLabel());
                } else {
                    return false;
                }
            }
            appendLogNLS("ScanAssign.activitylog.pallet.typeUsed", //$NON-NLS-1$
                currentPalletWrapper.getContainerType().getName());
        }
        if (needToCheckPosition) {
            canContinue = checkAndSetPosition();
        }
        return canContinue;
    }

    private boolean openDialogPalletMoved() {
        return MessageDialog.openConfirm(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), "Pallet product barcode", //$NON-NLS-1$
            Messages.getFormattedString(
                "ScanAssign.dialog.checkPallet.otherPosition", //$NON-NLS-1$
                palletFoundWithProductBarcode.getLabel(), currentPalletWrapper
                    .getLabel()));
    }

    /**
     * Check if position is available and set the ContainerPosition if it is
     * free
     * 
     * @return true if was able to create the ContainerPosition
     */
    private boolean checkAndSetPosition() throws Exception {
        appendLogNLS(
            "ScanAssign.activitylog.pallet.checkingPosition", currentPalletWrapper.getLabel()); //$NON-NLS-1$

        ContainerWrapper containerAtPosition = currentPalletWrapper
            .getContainer(currentPalletWrapper.getLabel(), currentPalletWrapper
                .getContainerType());
        if (containerAtPosition == null) {
            currentPalletWrapper
                .setPositionAndParentFromLabel(currentPalletWrapper.getLabel());
            return true;
        } else {
            openDialogPositionUsed(containerAtPosition);
            return false;
        }
    }

    private void openDialogPositionUsed(ContainerWrapper containerAtPosition) {
        String barcode = containerAtPosition.getProductBarcode();
        if (barcode == null) {
            barcode = "[none]"; //$NON-NLS-1$
        }
        BioBankPlugin.openError(Messages
            .getString("ScanAssign.dialog.positionUsed.title"), //$NON-NLS-1$
            Messages.getFormattedString(
                "ScanAssign.dialog.positionUsed.msg", barcode)); //$NON-NLS-1$

        appendLogNLS("ScanAssign.activitylog.pallet.positionUsedMsg", barcode, //$NON-NLS-1$
            currentPalletWrapper.getLabel()); //$NON-NLS-1$
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected String getActivityTitle() {
        return "Scan assign activity"; //$NON-NLS-1$
    }

}
