package edu.ualberta.med.biobank.forms;

import java.util.Map;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.scanprocess.data.AssignProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.validators.CabinetInventoryIDValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class GenericAssignEntryForm extends AbstractPalletSpecimenAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.GenericAssignEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(GenericAssignEntryForm.class.getName());

    private ContainerWrapper currentParentContainer = new ContainerWrapper(
        appService);

    private ScanPalletWidget parentContainerWidget;

    private Composite singleAssignComposite;

    private Composite multipleAssignComposite;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Assign position"); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);

        createLeftSection();

        createContainersVisualisationSection();
        parentContainerWidget.setVisible(false);

        new CancelConfirmWidget(page, this, true);
        setCanLaunchScan(true);
    }

    private void createContainersVisualisationSection() {
        ScrolledComposite containersScroll = new ScrolledComposite(page,
            SWT.H_SCROLL);
        containersScroll.setExpandHorizontal(true);
        containersScroll.setExpandVertical(true);
        containersScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        containersScroll.setLayoutData(scrollData);
        Composite containersComposite = toolkit
            .createComposite(containersScroll);
        GridLayout layout = getNeutralGridLayout();
        layout.numColumns = 3;
        containersComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        containersComposite.setLayoutData(gd);
        toolkit.paintBordersFor(containersComposite);

        containersScroll.setContent(containersComposite);

        Composite freezerComposite = toolkit
            .createComposite(containersComposite);
        freezerComposite.setLayout(getNeutralGridLayout());
        GridData gdFreezer = new GridData();
        gdFreezer.horizontalSpan = 3;
        gdFreezer.horizontalAlignment = SWT.RIGHT;
        freezerComposite.setLayoutData(gdFreezer);
        //        freezerLabel = toolkit.createLabel(freezerComposite, "Freezer"); //$NON-NLS-1$
        // freezerLabel.setLayoutData(new GridData());
        // freezerWidget = new ContainerDisplayWidget(freezerComposite);
        // freezerWidget.initDisplayFromType(true);
        // toolkit.adapt(freezerWidget);
        // freezerWidget.setDisplaySize(ScanPalletDisplay.PALLET_WIDTH, 100);

        Composite hotelComposite = toolkit.createComposite(containersComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        //        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel"); //$NON-NLS-1$
        // hotelWidget = new ContainerDisplayWidget(hotelComposite);
        // hotelWidget.initDisplayFromType(true);
        // toolkit.adapt(hotelWidget);
        // hotelWidget.setDisplaySize(100,
        // ScanPalletDisplay.PALLET_HEIGHT_AND_LEGEND);

        Composite palletComposite = toolkit
            .createComposite(containersComposite);
        palletComposite.setLayout(getNeutralGridLayout());
        palletComposite.setLayoutData(new GridData());
        //        palletLabel = toolkit.createLabel(palletComposite, "Pallet"); //$NON-NLS-1$
        parentContainerWidget = new ScanPalletWidget(palletComposite,
            UICellStatus.DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST);
        toolkit.adapt(parentContainerWidget);
        parentContainerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // manageDoubleClick(e);
            }
        });
        // showOnlyPallet(true);

        containersScroll.setMinSize(containersComposite.computeSize(
            SWT.DEFAULT, SWT.DEFAULT));
        createScanTubeAloneButton(containersComposite);
    }

    private GridLayout getNeutralGridLayout() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    private void createLeftSection() {
        Composite leftComposite = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        leftComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.widthHint = 600;
        gd.verticalAlignment = SWT.TOP;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        leftComposite.setLayoutData(gd);
        toolkit.paintBordersFor(leftComposite);

        BiobankText siteLabel = createReadOnlyLabelledField(leftComposite,
            SWT.NONE, Messages.getString("ScanAssign.site.label")); //$NON-NLS-1$
        siteLabel.setText(SessionManager.getUser().getCurrentWorkingCenter()
            .getNameShort());
        setFirstControl(siteLabel);

        // radio button to choose single or multiple
        final Button radioSingle = toolkit.createButton(leftComposite,
            "Single", SWT.RADIO);
        final Button radioMultiple = toolkit.createButton(leftComposite,
            "Multiple", SWT.RADIO);

        // stackLayout
        final Composite selectionComp = toolkit.createComposite(leftComposite);
        final StackLayout selectionStackLayout = new StackLayout();
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        selectionComp.setLayoutData(gd);
        selectionComp.setLayout(selectionStackLayout);

        createSingleLinkComposite(selectionComp);
        createMultipleLink(selectionComp);
        radioSingle.setSelection(true);
        selectionStackLayout.topControl = singleAssignComposite;

        radioSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSingle.getSelection()) {
                    selectionStackLayout.topControl = singleAssignComposite;
                    page.layout(true, true);
                    parentContainerWidget.setVisible(false);
                }
            }
        });
        radioMultiple.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMultiple.getSelection()) {
                    selectionStackLayout.topControl = multipleAssignComposite;
                    page.layout(true, true);
                    // parentContainerWidget.setVisible(true);
                }
            }
        });

    }

    private void createSingleLinkComposite(Composite parent) {
        singleAssignComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        singleAssignComposite.setLayout(layout);
        toolkit.paintBordersFor(singleAssignComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleAssignComposite.setLayoutData(gd);

        // inventoryID
        CabinetInventoryIDValidator inventoryIDValidator = new CabinetInventoryIDValidator();
        SpecimenWrapper singleSpecimen = new SpecimenWrapper(appService);
        BiobankText inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            singleAssignComposite, BiobankText.class, SWT.NONE,
            Messages.getString("Cabinet.inventoryId.label"), new String[0], //$NON-NLS-1$
            singleSpecimen, "inventoryId", //$NON-NLS-1$
            inventoryIDValidator);
        inventoryIdText.addKeyListener(textFieldKeyListener);

        Label newCabinetPositionLabel = widgetCreator
            .createLabel(singleAssignComposite,
                Messages.getString("Cabinet.position.label"));
        widgetCreator.createBoundWidget(singleAssignComposite,
            BiobankText.class, SWT.NONE, newCabinetPositionLabel,
            new String[0], new WritableValue("", String.class), null); //$NON-NLS-1$

        Button checkButton = toolkit.createButton(singleAssignComposite,
            Messages.getString("Cabinet.checkButton.text"), //$NON-NLS-1$
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        checkButton.setLayoutData(gd);
        checkButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
    }

    private void createMultipleLink(Composite parent) {
        multipleAssignComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        multipleAssignComposite.setLayout(layout);
        toolkit.paintBordersFor(multipleAssignComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleAssignComposite.setLayoutData(gd);

        NonEmptyStringValidator productBarcodeValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.productBarcode.validationMsg"));//$NON-NLS-1$
        NonEmptyStringValidator palletLabelValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.palletLabel.validationMsg"));//$NON-NLS-1$

        BiobankText palletproductBarcodeText = (BiobankText) createBoundWidgetWithLabel(
            multipleAssignComposite, BiobankText.class,
            SWT.NONE,
            Messages.getString("ScanAssign.productBarcode.label"), //$NON-NLS-1$
            null, currentParentContainer,
            ContainerPeer.PRODUCT_BARCODE.getName(), productBarcodeValidator);
        palletproductBarcodeText.addKeyListener(textFieldKeyListener);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletproductBarcodeText.setLayoutData(gd);
        setFirstControl(palletproductBarcodeText);

        palletproductBarcodeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // if (palletproductBarcodeTextModified
                // && productBarcodeValidator.validate(
                // currentPalletWrapper.getProductBarcode()).equals(
                // Status.OK_STATUS)) {
                // validateValues();
                // }
                // palletproductBarcodeTextModified = false;
            }
        });
        palletproductBarcodeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // if (!modificationMode) {
                // palletproductBarcodeTextModified = true;
                // validationMade.setValue(false);
                // }
            }
        });

        BiobankText palletPositionText = (BiobankText) createBoundWidgetWithLabel(
            multipleAssignComposite, BiobankText.class, SWT.NONE,
            Messages.getString("ScanAssign.palletLabel.label"), null, //$NON-NLS-1$
            BeansObservables.observeValue(currentParentContainer,
                ContainerPeer.LABEL.getName()), palletLabelValidator);
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletPositionText.setLayoutData(gd);
        palletPositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // if (palletPositionTextModified) {
                // validateValues();
                // }
                // palletPositionTextModified = false;
            }
        });
        palletPositionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // if (!modificationMode) {
                // palletPositionTextModified = true;
                // validationMade.setValue(false);
                // }
            }
        });

        createPalletTypesViewer(multipleAssignComposite);

        createPlateToScanField(multipleAssignComposite);
        createScanButton(multipleAssignComposite);

    }

    private void createPalletTypesViewer(
        @SuppressWarnings("unused") Composite parent) {
        // ComboViewer palletTypesViewer = createComboViewer(
        // parent,
        //            Messages.getString("ScanAssign.palletType.label"), //$NON-NLS-1$
        // null, null,
        // Messages.getString("ScanAssign.palletType.validationMsg"),
        // new ComboSelectionUpdate() {
        // @Override
        // public void doSelection(Object selectedObject) {
        // if (!modificationMode) {
        // ContainerTypeWrapper oldContainerType =
        // currentPalletWrapper
        // .getContainerType();
        // currentPalletWrapper
        // .setContainerType((ContainerTypeWrapper) selectedObject);
        // if (oldContainerType != null) {
        // validateValues();
        // }
        // palletTypesViewer.getCombo().setFocus();
        // }
        // }
        //            }); //$NON-NLS-1$
        // if (palletContainerTypes.size() == 1) {
        // currentPalletWrapper.setContainerType(palletContainerTypes.get(0));
        // palletTypesViewer.setSelection(new StructuredSelection(
        // palletContainerTypes.get(0)));
        // }
    }

    @Override
    protected void disableFields() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean fieldsValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        Map<RowColPos, SpecimenWrapper> expectedSpecimens = currentParentContainer
            .getSpecimens();
        // currentScanState = CellStatus.EMPTY;
        for (int row = 0; row < currentParentContainer.getRowCapacity(); row++) {
            for (int col = 0; col < currentParentContainer.getColCapacity(); col++) {
                RowColPos rcp = new RowColPos(row, col);
                monitor.subTask("Processing position "
                    + ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                PalletCell cell = getCells().get(rcp);
                if (!isRescanMode() || cell == null || cell.getStatus() == null
                    || cell.getStatus() == UICellStatus.EMPTY
                    || cell.getStatus() == UICellStatus.ERROR
                    || cell.getStatus() == UICellStatus.MISSING) {
                    SpecimenWrapper expectedSpecimen = null;
                    if (expectedSpecimens != null) {
                        expectedSpecimen = expectedSpecimens.get(rcp);
                        if (expectedSpecimen != null) {
                            if (cell == null) {
                                cell = new PalletCell(new ScanCell(rcp.row,
                                    rcp.col, null));
                                getCells().put(rcp, cell);
                            }
                            cell.setExpectedSpecimen(expectedSpecimen);
                        }
                    }
                    if (cell != null) {
                        processCellStatus(cell);
                    }
                }
                // CellStatus newStatus = CellStatus.EMPTY;
                // if (cell != null) {
                // newStatus = cell.getStatus();
                // }
                // currentScanState = currentScanState.mergeWith(newStatus);
            }
        }
        setScanValid(true);
    }

    protected void processCellStatus(PalletCell scanCell) throws Exception {
        SpecimenWrapper expectedSpecimen = scanCell.getExpectedSpecimen();
        String value = scanCell.getValue();
        // String positionString = currentParentContainer.getLabel()
        // + ContainerLabelingSchemeWrapper.rowColToSbs(new RowColPos(scanCell
        // .getRow(), scanCell.getCol()));
        if (value == null) { // no aliquot scanned
            // updateCellAsMissing(positionString, scanCell, expectedAliquot);
        } else {
            // FIXME test what happen if don't have read rights on the site
            SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(
                appService, value, SessionManager.getUser());
            if (foundAliquot == null) {
                // updateCellAsNotLinked(positionString, scanCell);
            } else if (expectedSpecimen != null
                && !foundAliquot.equals(expectedSpecimen)) {
                // updateCellAsPositionAlreadyTaken(positionString, scanCell,
                // expectedAliquot, foundAliquot);
            } else {
                scanCell.setSpecimen(foundAliquot);
                if (expectedSpecimen != null) {
                    // aliquot scanned is already registered at this
                    // position (everything is ok !)
                    scanCell.setStatus(UICellStatus.FILLED);
                    scanCell.setTitle(foundAliquot.getCollectionEvent()
                        .getPatient().getPnumber());
                    scanCell.setSpecimen(expectedSpecimen);
                } else {
                    // if (currentPalletWrapper.canHoldAliquot(foundAliquot)) {
                    // if (foundAliquot.hasParent()) { // moved
                    // processCellWithPreviousPosition(scanCell,
                    // positionString, foundAliquot);
                    // } else { // new in pallet
                    // if (foundAliquot.isUsedInDispatch()) {
                    // updateCellAsDispatchedError(positionString,
                    // scanCell, foundAliquot);
                    // } else {
                    // scanCell.setStatus(CellStatus.NEW);
                    // scanCell.setTitle(foundAliquot
                    // .getCollectionEvent().getPatient()
                    // .getPnumber());
                    // }
                    // }
                    // } else {
                    // // pallet can't hold this aliquot type
                    // updateCellAsTypeError(positionString, scanCell,
                    // foundAliquot);
                    // }
                }
            }
        }
    }

    @Override
    protected String getActivityTitle() {
        return "Generic Assign";
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected void saveForm() throws Exception {
        BiobankPlugin.openInformation("TODO", "Not yet implemented");
    }

    @Override
    protected String getOkMessage() {
        return "Assign position to specimens";
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected ProcessData getProcessData() {
        return new AssignProcessData(null);
    }

}
