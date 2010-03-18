package edu.ualberta.med.biobank.forms;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.PalletBarCodeValidator;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.GridContainerWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class ScanAssignEntryForm extends AbstractAliquotAdminForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ScanAssignEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ScanAssignEntryForm"; //$NON-NLS-1$

    private ComboViewer palletTypesViewer;
    private Text plateToScanText;
    private Text palletCodeText;
    private Text palletPositionText;
    private Button scanButton;

    private Label freezerLabel;
    private GridContainerWidget freezerWidget;
    private Label palletLabel;
    private ScanPalletWidget palletWidget;
    private Label hotelLabel;
    private GridContainerWidget hotelWidget;

    private IObservableValue plateToScanValue = new WritableValue("", //$NON-NLS-1$
        String.class);
    private IObservableValue scanLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private Map<RowColPos, PalletCell> cells;

    protected ContainerWrapper currentPalletWrapper;

    private ContainerTypeWrapper onlyTypePossible;

    // for debugging only (fake scan) :
    private Button linkedOnlyButton;
    private Button linkedAssignButton;

    private String palletNameContains = ""; //$NON-NLS-1$

    private CancelConfirmWidget cancelConfirmWidget;

    protected ContainerWrapper palletFoundWithProductBarcode;

    private boolean newPallet;

    private String oldPalletPosition;

    private AliquotCellStatus currentScanState;

    @Override
    protected void init() {
        super.init();
        setPartName(Messages.getString("ScanAssign.tabTitle")); //$NON-NLS-1$
        currentPalletWrapper = new ContainerWrapper(appService);
        initPalletValues();
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        palletNameContains = store
            .getString(PreferenceConstants.PALLET_SCAN_CONTAINER_NAME_CONTAINS);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ScanAssign.formTitle")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();

        createContainersSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanLaunchedValue, Messages
                .getString("linkAssign.scanLaunchValidationMsg")); //$NON-NLS-1$
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

        palletCodeText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, Messages
                .getString("ScanAssign.productBarcode.label"), //$NON-NLS-1$
            null, BeansObservables.observeValue(currentPalletWrapper,
                "productBarcode"), new NonEmptyStringValidator( //$NON-NLS-1$
                Messages.getString("ScanAssign.productBarcode.validationMsg"))); //$NON-NLS-1$
        palletCodeText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletCodeText.setLayoutData(gd);
        firstControl = palletCodeText;
        palletCodeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    palletFoundWithProductBarcode = ContainerWrapper
                        .getContainerWithProductBarcodeInSite(appService,
                            SessionManager.getInstance().getCurrentSite(),
                            currentPalletWrapper.getProductBarcode());
                    if (palletFoundWithProductBarcode == null) {
                        palletTypesViewer.getCombo().setEnabled(true);
                    } else {
                        palletPositionText
                            .setText(palletFoundWithProductBarcode.getLabel());
                        palletPositionText.selectAll();
                        palletTypesViewer.getCombo().setEnabled(false);
                        palletTypesViewer.setSelection(new StructuredSelection(
                            palletFoundWithProductBarcode.getContainerType()));
                    }
                } catch (Exception ex) {
                    BioBankPlugin.openError("Product barcode", ex); //$NON-NLS-1$
                }
            }
        });

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

        createContainerTypeSection(fieldsComposite);

        plateToScanText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, Messages
                .getString("linkAssign.plateToScan.label"), //$NON-NLS-1$
            new String[0], plateToScanValue, new ScannerBarcodeValidator(
                Messages.getString("linkAssign.plateToScan.validationMsg"))); //$NON-NLS-1$
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    scan();
                }
            }
        });
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        plateToScanText.setLayoutData(gd);

        String scanButtonTitle = Messages
            .getString("linkAssign.scanButton.text"); //$NON-NLS-1$
        if (!BioBankPlugin.isRealScanEnabled()) {
            gd.widthHint = 200;
            Composite comp = toolkit.createComposite(fieldsComposite);
            comp.setLayout(new GridLayout());
            gd = new GridData();
            gd.horizontalSpan = 2;
            comp.setLayoutData(gd);
            linkedAssignButton = toolkit.createButton(comp,
                "Select linked only aliquots", SWT.RADIO); //$NON-NLS-1$
            linkedAssignButton.setSelection(true);
            linkedOnlyButton = toolkit.createButton(comp,
                "Select linked and assigned aliquots", SWT.RADIO); //$NON-NLS-1$
            scanButtonTitle = "Fake scan"; //$NON-NLS-1$
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
                            BioBankPlugin
                                .openError(
                                    Messages
                                        .getString("ScanAssign.dialog.scanError.title"), //$NON-NLS-1$
                                    e);
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

    /**
     * If can't know which pallet type we need, add a combo
     */
    private void createContainerTypeSection(Composite parent) throws Exception {
        List<ContainerTypeWrapper> palletContainerTypes = ContainerTypeWrapper
            .getContainerTypesInSite(appService,
                currentPalletWrapper.getSite(), palletNameContains, false);
        if (palletContainerTypes.size() == 0) {
            BioBankPlugin.openAsyncError(Messages
                .getString("ScanAssign.dialog.noPalletFoundError.title"), //$NON-NLS-1$
                Messages.getString("ScanAssign.dialog.noPalletFoundError.msg") //$NON-NLS-1$
                    + palletNameContains);
        }
        palletTypesViewer = createComboViewerWithNoSelectionValidator(parent,
            Messages.getString("ScanAssign.palletType.label"), //$NON-NLS-1$
            palletContainerTypes, null, Messages
                .getString("ScanAssign.palletType.validationMsg")); //$NON-NLS-1$
        palletTypesViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    setContainerType();
                    scanLaunchedValue.setValue(false);
                }
            });
        if (palletContainerTypes.size() == 1) {
            currentPalletWrapper.setContainerType(palletContainerTypes.get(0));
            palletTypesViewer.setSelection(new StructuredSelection(
                palletContainerTypes.get(0)));
        }
    }

    protected void scan() {
        try {
            // if another scan has been done on this same form, need to reset
            // values set before
            reset(true);
            boolean showResult = checkPallet();
            if (showResult) {
                appendLogNLS("linkAssign.activitylog.scanning", //$NON-NLS-1$
                    plateToScanValue.getValue().toString());
                showOnlyPallet(false);
                if (BioBankPlugin.isRealScanEnabled()) {
                    int plateNum = BioBankPlugin.getDefault().getPlateNumber(
                        plateToScanValue.getValue().toString());
                    cells = PalletCell.convertArray(ScannerConfigPlugin
                        .scan(plateNum));
                } else {
                    if (linkedAssignButton.getSelection()) {
                        cells = PalletCell.getRandomAliquotsNotAssigned(
                            appService, SessionManager.getInstance()
                                .getCurrentSite().getId());
                    } else if (linkedOnlyButton.getSelection()) {
                        cells = PalletCell.getRandomAliquotsAlreadyAssigned(
                            appService, SessionManager.getInstance()
                                .getCurrentSite().getId());
                    }
                }
                appendLogNLS("ScanAssign.activitylog.scanRes.total", //$NON-NLS-1$
                    cells.keySet().size());
                currentScanState = AliquotCellStatus.EMPTY;
                Map<RowColPos, AliquotWrapper> aliquots = currentPalletWrapper
                    .getAliquots();
                for (RowColPos rcp : cells.keySet()) {
                    AliquotWrapper expectedAliquot = null;
                    if (aliquots != null) {
                        expectedAliquot = aliquots.get(rcp);
                    }
                    PalletCell cell = cells.get(rcp);
                    cell.setExpectedAliquot(expectedAliquot);
                    AliquotCellStatus newStatus = setStatus(cell);
                    currentScanState = currentScanState.mergeWith(newStatus);
                }
                scanValidValue
                    .setValue(currentScanState != AliquotCellStatus.ERROR);
                palletWidget.setCells(cells);
                scanLaunchedValue.setValue(true);
                setDirty(true);
            } else {
                palletWidget.setCells(new TreeMap<RowColPos, PalletCell>());
                showOnlyPallet(true);
                scanValidValue.setValue(false);
            }
            showPalletPosition();
            cancelConfirmWidget.setFocus();
            form.layout(true, true);
        } catch (RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            BioBankPlugin.openError("Error while scanning", e); //$NON-NLS-1$
            String msg = e.getMessage();
            if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
                msg = e.getCause().getMessage();
            }
            appendLog("ERROR: " + msg); //$NON-NLS-1$
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
            freezerWidget.setSelection(hotelContainer.getPosition());

            hotelLabel.setText(hotelContainer.getFullInfoLabel());
            hotelWidget.setContainerType(hotelContainer.getContainerType());
            hotelWidget.setSelection(currentPalletWrapper.getPosition());

            palletLabel.setText(currentPalletWrapper.getLabel());
        }
    }

    protected AliquotCellStatus setStatus(PalletCell scanCell) throws Exception {
        AliquotWrapper expectedAliquot = scanCell.getExpectedAliquot();
        String value = scanCell.getValue();
        String positionString = currentPalletWrapper.getLabel()
            + LabelingScheme.rowColToSbs(new RowColPos(scanCell.getRow(),
                scanCell.getCol()));
        if (value == null) {
            // no aliquot scanned
            if (expectedAliquot == null) {
                // no existing aliquot should be there
                return AliquotCellStatus.EMPTY;
            }
            // aliquot missing
            return updateCellAsMissing(positionString, scanCell,
                expectedAliquot);
        }
        List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
            appService, value, SessionManager.getInstance().getCurrentSite());
        if (aliquots.size() == 0) {
            // aliquot not found in site (not yet linked ?)
            return updateCellAsNotLinked(positionString, scanCell);
        } else if (aliquots.size() == 1) {
            AliquotWrapper foundAliquot = aliquots.get(0);
            if (expectedAliquot != null
                && !foundAliquot.equals(expectedAliquot)) {
                // aliquot found but another aliquot already at this position
                return updateCellAsPositionAlreadyTaken(positionString,
                    scanCell, expectedAliquot, foundAliquot);
            }
            scanCell.setAliquot(foundAliquot);
            AliquotCellStatus status = AliquotCellStatus.EMPTY;
            if (expectedAliquot != null) {
                // aliquot scanned is already registered at this position
                // (everything is ok !)
                scanCell.setStatus(AliquotCellStatus.FILLED);
                status = AliquotCellStatus.FILLED;
                scanCell.setTitle(foundAliquot.getPatientVisit().getPatient()
                    .getPnumber());
                scanCell.setAliquot(expectedAliquot);
            } else {
                scanCell.setStatus(AliquotCellStatus.NEW);
                status = AliquotCellStatus.NEW;
                scanCell.setTitle(foundAliquot.getPatientVisit().getPatient()
                    .getPnumber());
                if (foundAliquot.hasParent()
                    && !foundAliquot.getParent().getId().equals(
                        currentPalletWrapper.getId())) {
                    // the scanned aliquot has already a position but a
                    // different one - ie MOVED
                    status = updateCellAsMoved(positionString, scanCell,
                        foundAliquot);
                }
                if (!currentPalletWrapper.canHoldAliquot(foundAliquot)) {
                    // pallet can't hold this aliquot type
                    return updateCellAsTypeError(positionString, scanCell,
                        foundAliquot);
                }
            }
            return status;
        } else {
            Assert
                .isTrue(false, "InventoryId " + value + " should be unique !"); //$NON-NLS-1$ //$NON-NLS-2$
            return updateCellAsInventoryIdError(positionString, scanCell);
        }
    }

    private AliquotCellStatus updateCellAsInventoryIdError(String position,
        PalletCell scanCell) {
        String cellValue = scanCell.getValue();

        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages.getFormattedString(
            "ScanAssign.scanStatus.aliquot.inventoryIdError", cellValue)); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.inventoryIdError", position, cellValue); //$NON-NLS-1$
        return AliquotCellStatus.ERROR;
    }

    private AliquotCellStatus updateCellAsTypeError(String position,
        PalletCell scanCell, AliquotWrapper foundAliquot) {
        String palletType = currentPalletWrapper.getContainerType().getName();
        String sampleType = foundAliquot.getSampleType().getName();

        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages.getFormattedString(
            "ScanAssign.scanStatus.aliquot.typeError", palletType, sampleType)); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.typeError", position, palletType, //$NON-NLS-1$
            sampleType);
        return AliquotCellStatus.ERROR;
    }

    private AliquotCellStatus updateCellAsMoved(String position,
        PalletCell scanCell, AliquotWrapper foundAliquot) {
        String expectedPosition = foundAliquot.getPositionString();
        if (expectedPosition == null) {
            expectedPosition = "none"; //$NON-NLS-1$
        }

        scanCell.setStatus(AliquotCellStatus.MOVED);
        scanCell.setInformation(Messages.getFormattedString(
            "ScanAssign.scanStatus.aliquot.moved", expectedPosition)); //$NON-NLS-1$

        appendLogNLS(
            "ScanAssign.activitylog.aliquot.moved", position, scanCell.getValue(), //$NON-NLS-1$
            expectedPosition);
        return AliquotCellStatus.MOVED;
    }

    private AliquotCellStatus updateCellAsPositionAlreadyTaken(String position,
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
        return AliquotCellStatus.ERROR;
    }

    private AliquotCellStatus updateCellAsNotLinked(String position,
        PalletCell scanCell) {
        scanCell.setStatus(AliquotCellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.notlinked", position, scanCell.getValue()); //$NON-NLS-1$
        return AliquotCellStatus.ERROR;
    }

    private AliquotCellStatus updateCellAsMissing(String position,
        PalletCell scanCell, AliquotWrapper missingAliquot) {
        scanCell.setStatus(AliquotCellStatus.MISSING);
        scanCell
            .setInformation(Messages
                .getFormattedString(
                    "ScanAssign.scanStatus.aliquot.missing", missingAliquot.getInventoryId())); //$NON-NLS-1$
        scanCell.setTitle("?"); //$NON-NLS-1$
        appendLogNLS(
            "ScanAssign.activitylog.aliquot.missing", position, missingAliquot //$NON-NLS-1$
                .getInventoryId(), missingAliquot.getPatientVisit()
                .getFormattedDateProcessed(), missingAliquot.getPatientVisit()
                .getPatient().getPnumber());
        return AliquotCellStatus.MISSING;
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
                scanLaunchedValue.setValue(false);
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
        if (currentScanState == AliquotCellStatus.MISSING) {
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
        if (oldPalletPosition != null) {
            appendLogNLS("ScanAssign.activitylog.pallet.moved", //$NON-NLS-1$
                productBarcode, containerType, oldPalletPosition, palletLabel);
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

        if (beforeScan) {
            productBarcode = currentPalletWrapper.getProductBarcode();
            label = currentPalletWrapper.getLabel();
            currentPalletWrapper.resetToNewObject();
        } else {
            if (palletTypesViewer != null) {
                palletTypesViewer.getCombo().deselectAll();
            }
        }
        freezerWidget.setSelection(null);
        hotelWidget.setSelection(null);
        palletWidget.setCells(null);
        cells = null;
        scanLaunchedValue.setValue(false);
        initPalletValues();
        if (onlyTypePossible != null) {
            currentPalletWrapper.setContainerType(onlyTypePossible);
        } else {
            setContainerType();
        }

        if (beforeScan) {
            currentPalletWrapper.setProductBarcode(productBarcode);
            currentPalletWrapper.setLabel(label);
        } else {
            setDirty(false);
        }
    }

    private void initPalletValues() {
        try {
            currentPalletWrapper.reset();
            currentPalletWrapper.setActivityStatus(ActivityStatusWrapper
                .getActivityStatus(appService, "Active")); //$NON-NLS-1$
            currentPalletWrapper.setSite(SessionManager.getInstance()
                .getCurrentSite());
        } catch (Exception e) {
            logger.error("Error while reseting pallet values", e); //$NON-NLS-1$
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
                && palletTypesViewer.getCombo().getSelectionIndex() == -1) {
                scanButton.setEnabled(false);
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
        oldPalletPosition = null;
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
                    oldPalletPosition = palletFoundWithProductBarcode
                        .getLabel();
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
