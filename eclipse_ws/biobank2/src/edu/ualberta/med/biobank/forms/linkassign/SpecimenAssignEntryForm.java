package edu.ualberta.med.biobank.forms.linkassign;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenAssignProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.AssignProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenAssignResInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenResInfo;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletDisplay;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenAssignEntryForm extends AbstractLinkAssignEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenAssignEntryForm";

    private static BgcLogger logger = BgcLogger
        .getLogger(SpecimenAssignEntryForm.class.getName());

    private static Mode mode = Mode.MULTIPLE;

    private static final String INVENTORY_ID_BINDING = "inventoryId-binding";

    private static final String NEW_SINGLE_POSITION_BINDING =
        "newSinglePosition-binding";

    private static final String OLD_SINGLE_POSITION_BINDING =
        "oldSinglePosition-binding";

    private static final String PRODUCT_BARCODE_BINDING =
        "productBarcode-binding";

    private static final String PALLET_TYPES_BINDING = "palletType-binding";

    protected static boolean useScanner = true;

    // for single specimen assign
    private Button cabinetCheckButton;
    private BgcBaseText inventoryIdText;
    protected boolean inventoryIdModified;
    private Label oldSinglePositionLabel;
    private BgcBaseText oldSinglePositionText;
    private Label oldSinglePositionCheckLabel;
    private AbstractValidator oldSinglePositionCheckValidator;
    private BgcBaseText oldSinglePositionCheckText;
    private Label newSinglePositionLabel;
    private StringLengthValidator newSinglePositionValidator;
    private BgcBaseText newSinglePositionText;
    protected boolean positionTextModified;
    private BgcBaseText singleTypeText;
    private BgcBaseText singleCollectionDateText;
    private WritableValue foundSpecNull = new WritableValue(Boolean.TRUE,
        Boolean.class);

    // for multiple specimens assign
    private ContainerWrapper currentMultipleContainer;
    protected boolean palletproductBarcodeTextModified;
    private NonEmptyStringValidator productBarcodeValidator;
    protected boolean isModifyingMultipleFields;
    private NonEmptyStringValidator palletLabelValidator;
    private BgcBaseText palletPositionText;
    protected boolean useNewProductBarcode;
    private ComboViewer palletTypesViewer;
    protected boolean palletPositionTextModified;
    private List<ContainerTypeWrapper> palletContainerTypes;
    private BgcBaseText palletproductBarcodeText;
    private boolean saveEvenIfMissing;
    private boolean isFakeScanLinkedOnly;
    private Button fakeScanLinkedOnlyButton;
    private Composite multipleOptionsFields;
    private Composite fakeScanComposite;
    private Button useScannerButton;
    private Label palletproductBarcodeLabel;
    private boolean isNewMultipleContainer;
    private boolean checkingMultipleContainerPosition;

    private boolean initWithProduct = false;

    public SpecimenAssignEntryForm() {
        currentMultipleContainer = new ContainerWrapper(
            SessionManager.getAppService());
    }

    @Override
    protected void init() throws Exception {
        super.init();
        setCanLaunchScan(true);
        initPalletValues();
    }

    /**
     * Multiple. initialize pallet
     */
    private void initPalletValues() {
        try {
            currentMultipleContainer.initObjectWith(new ContainerWrapper(
                SessionManager.getAppService()));
            currentMultipleContainer.reset();
            currentMultipleContainer.setActivityStatus(ActivityStatus.ACTIVE);
            currentMultipleContainer.setSite(SessionManager.getUser()
                .getCurrentWorkingSite());
        } catch (Exception e) {
            logger.error(
                "Error while reseting pallet values", e);
        }
    }

    @Override
    protected String getActivityTitle() {
        return "Specimen Assign";
    }

    @Override
    public BgcLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected String getFormTitle() {
        return "Assign position or move specimens";
    }

    @Override
    protected boolean isSingleMode() {
        return mode.isSingleMode();
    }

    @Override
    protected void setMode(Mode m) {
        mode = m;
    }

    @Override
    protected void setFirstControl(Mode mode) {
        if (mode.isSingleMode())
            setFirstControl(inventoryIdText);
        else
            setFirstControl(useScannerButton);
    }

    @Override
    protected String getOkMessage() {
        return "Assign position to specimens or move specimens";
    }

    @Override
    public String getNextOpenedFormId() {
        return ID;
    }

    @Override
    protected void createCommonFields(Composite commonFieldsComposite) {
        BgcBaseText siteLabel = createReadOnlyLabelledField(
            commonFieldsComposite, SWT.NONE,
            "Site");
        siteLabel.setText(SessionManager.getUser().getCurrentWorkingCenter()
            .getNameShort());
    }

    @Override
    protected int getLeftSectionWidth() {
        return 450;
    }

    @Override
    protected void createSingleFields(Composite parent) {
        Composite fieldsComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        fieldsComposite.setLayoutData(gd);

        // check box to say it is a cabinet specimen or not
        Label cabinetCheckButtonLabel = widgetCreator.createLabel(
            fieldsComposite,
            "Cabinet specimen");
        cabinetCheckButton =
            toolkit.createButton(fieldsComposite, "", SWT.CHECK);
        cabinetCheckButton
            .setToolTipText("Old cabinet specimen with 4 letters were transformed to 5 letters (C+4letters). ");
        cabinetCheckButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (cabinetCheckButton.getSelection()) {
                    if (inventoryIdText.getText().length() == 4) {
                        // compatibility with old cabinet specimens imported
                        // 4 letters specimens are now C+4letters
                        inventoryIdText
                            .setText("C" + inventoryIdText.getText());
                        focusControl(inventoryIdText);
                    }
                }
            }
        });
        // this check box is there only for cbsr : specimens imported from
        // cabinet from the old database where 4 letters. A 'C' has been added
        // to them to make them different from the freezer specimen with exactly
        // the same inventory id
        CenterWrapper<?> center = SessionManager.getUser()
            .getCurrentWorkingCenter();
        boolean cbsrCenter = center != null
            && center.getNameShort().equals("CBSR");
        widgetCreator.showWidget(cabinetCheckButtonLabel, cbsrCenter);
        widgetCreator.showWidget(cabinetCheckButton, cbsrCenter);

        // inventoryID
        Label inventoryIdLabel = widgetCreator.createLabel(fieldsComposite,
            "Inventory ID");
        inventoryIdText = (BgcBaseText) createWidget(fieldsComposite,
            BgcBaseText.class, SWT.NONE, "");
        inventoryIdText.addKeyListener(textFieldKeyListener);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (inventoryIdModified)
                    try {
                        retrieveSingleSpecimenData();
                    } catch (Exception ex) {
                        BgcPlugin
                            .openError(
                                "Move - specimen error",
                                ex);
                        focusControl(inventoryIdText);

                    }
                inventoryIdModified = false;
            }
        });
        inventoryIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                inventoryIdModified = true;
                displaySinglePositions(false);
                canSaveSingleSpecimen.setValue(false);
            }
        });
        AbstractValidator inventoryValidator = new AbstractValidator(
            "Enter a valid inventory ID") {
            @Override
            public IStatus validate(Object value) {
                if ((Boolean) foundSpecNull.getValue()) {
                    showDecoration();
                    return ValidationStatus
                        .error("Enter a valid inventory ID");
                }
                hideDecoration();
                return ValidationStatus.ok();
            }
        };
        inventoryValidator.setControlDecoration(BgcBaseWidget.createDecorator(
            inventoryIdLabel, inventoryValidator.getErrorMessage()));
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(inventoryValidator);
        Binding inventoryIdBinding = widgetCreator.bindValue(foundSpecNull,
            new WritableValue(Boolean.TRUE, Boolean.class), uvs, null);
        widgetCreator.addBindingToMap(INVENTORY_ID_BINDING, inventoryIdBinding);

        singleTypeText = (BgcBaseText) createLabelledWidget(fieldsComposite,
            BgcBaseText.class, SWT.NONE,
            "Type");
        singleTypeText.setEnabled(false);

        singleCollectionDateText = (BgcBaseText) createLabelledWidget(
            fieldsComposite, BgcBaseText.class, SWT.NONE,
            "Collection date");
        singleCollectionDateText.setEnabled(false);

        createSinglePositionFields(fieldsComposite);
    }

    /**
     * 
     * Single assign. Search the specimen, if find it, display related
     * information
     */
    protected void retrieveSingleSpecimenData() throws Exception {
        String inventoryId = inventoryIdText.getText();
        singleTypeText.setText("");
        singleCollectionDateText.setText("");
        if (inventoryId.isEmpty()) {
            return;
        }
        if (cabinetCheckButton.getSelection() && inventoryId.length() == 4) {
            // compatibility with old cabinet specimens imported
            // 4 letters specimens are now C+4letters
            inventoryId = "C" + inventoryId;
        }
        setValues();
        singleSpecimen.setInventoryId(inventoryId);
        inventoryIdText.setText(inventoryId);
        oldSinglePositionCheckText.setText("?");

        appendLog(NLS.bind(
            "Getting informations for inventoryID {0}",
            singleSpecimen.getInventoryId()));
        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
            SessionManager.getAppService(), singleSpecimen.getInventoryId());
        foundSpecNull.setValue(false);
        if (foundSpecimen == null) {
            foundSpecNull.setValue(true);
            throw new Exception(NLS.bind(
                "No specimen found with inventoryId {0}",
                singleSpecimen.getInventoryId()));
        }

        singleSpecimen.initObjectWith(foundSpecimen);
        if (singleSpecimen.isUsedInDispatch()) {
            foundSpecNull.setValue(true);
            throw new Exception(
                "This specimen is currently in transit in a dispatch.");
        }
        if (!SessionManager.getUser().getCurrentWorkingCenter()
            .equals(singleSpecimen.getCurrentCenter())) {
            foundSpecNull.setValue(true);
            throw new Exception(NLS.bind(
                "This specimen is currently in center ''{0}''.",
                singleSpecimen.getCurrentCenter().getNameShort()));
        }
        singleTypeText.setText(singleSpecimen.getSpecimenType().getNameShort());
        singleCollectionDateText.setText(singleSpecimen.getTopSpecimen()
            .getFormattedCreatedAt());
        String positionString = singleSpecimen.getPositionString(true, false);
        if (positionString == null) {
            displayOldSingleFields(false);
            positionString = "none";
            focusControl(newSinglePositionText);
        } else {
            displayOldSingleFields(true);
            oldSinglePositionCheckText.setText(oldSinglePositionCheckText
                .getText());
            focusControl(oldSinglePositionCheckText);
        }
        oldSinglePositionText.setText(positionString);
        appendLog(NLS.bind(
            "Specimen {0}: current position={1}",
            singleSpecimen.getInventoryId(), positionString));
        canSaveSingleSpecimen.setValue(true);
    }

    /**
     * Single assign: Some fields will be displayed only if the specimen has
     * already a position
     */
    private void createSinglePositionFields(Composite fieldsComposite) {
        // for move mode: display old position retrieved from database
        oldSinglePositionLabel = widgetCreator.createLabel(fieldsComposite,
            "Old position");
        oldSinglePositionText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite, BgcBaseText.class, SWT.NONE,
            oldSinglePositionLabel, new String[0], null, null);
        oldSinglePositionText.setEnabled(false);
        oldSinglePositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for move mode: field to enter old position. Check needed to be sure
        // nothing is wrong with the specimen
        oldSinglePositionCheckLabel = widgetCreator.createLabel(
            fieldsComposite,
            "Old position check");
        oldSinglePositionCheckValidator =
            new AbstractValidator(
                "Enter correct old position") {
                @Override
                public IStatus validate(Object value) {
                    if (value != null && !(value instanceof String)) {
                        throw new RuntimeException(
                            "Not supposed to be called for non-strings.");
                    }

                    if (value != null) {
                        String s = (String) value;
                        if (s.equals(oldSinglePositionText.getText())) {
                            hideDecoration();
                            return Status.OK_STATUS;
                        }
                    }
                    showDecoration();
                    return ValidationStatus.error(errorMessage);
                }
            };
        oldSinglePositionCheckText =
            (BgcBaseText) widgetCreator
                .createBoundWidget(
                    fieldsComposite,
                    BgcBaseText.class,
                    SWT.NONE,
                    oldSinglePositionCheckLabel,
                    new String[0],
                    new WritableValue("", String.class),
                    oldSinglePositionCheckValidator,
                    OLD_SINGLE_POSITION_BINDING);
        oldSinglePositionCheckText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for all modes: position to be assigned to the specimen
        newSinglePositionLabel = widgetCreator.createLabel(fieldsComposite,
            "Position");
        newSinglePositionValidator = new StringLengthValidator(4,
            "Enter a position");
        displayOldSingleFields(false);
        newSinglePositionText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite, BgcBaseText.class, SWT.NONE,
            newSinglePositionLabel, new String[0], new WritableValue("",
                String.class), newSinglePositionValidator,
            NEW_SINGLE_POSITION_BINDING);
        newSinglePositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (positionTextModified
                    && newSinglePositionValidator
                        .validate(newSinglePositionText.getText()) == Status.OK_STATUS) {
                    BusyIndicator.showWhile(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell().getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                initContainersFromPosition(
                                    newSinglePositionText, false, null);
                                checkPositionAndSpecimen(inventoryIdText,
                                    newSinglePositionText);
                            }
                        });
                }
                positionTextModified = false;
            }
        });
        newSinglePositionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                positionTextModified = true;
                displaySinglePositions(false);
                canSaveSingleSpecimen.setValue(false);
            }
        });
        newSinglePositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        displayOldSingleFields(false);
    }

    /**
     * Single assign: show or hide old positions fields
     */
    private void displayOldSingleFields(boolean displayOld) {
        widgetCreator.setBinding(OLD_SINGLE_POSITION_BINDING, displayOld);
        widgetCreator.showWidget(oldSinglePositionLabel, displayOld);
        widgetCreator.showWidget(oldSinglePositionText, displayOld);
        widgetCreator.showWidget(oldSinglePositionCheckLabel, displayOld);
        widgetCreator.showWidget(oldSinglePositionCheckText, displayOld);
        if (displayOld) {
            newSinglePositionLabel
                .setText("New position"
                    + ":");
        } else {
            newSinglePositionLabel
                .setText("Position"
                    + ":");
            oldSinglePositionCheckText.setText(oldSinglePositionText.getText());
        }
        page.layout(true, true);
    }

    @Override
    protected void createMultipleFields(Composite parent)
        throws ApplicationException {
        multipleOptionsFields = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        multipleOptionsFields.setLayout(layout);
        toolkit.paintBordersFor(multipleOptionsFields);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleOptionsFields.setLayoutData(gd);

        productBarcodeValidator =
            new NonEmptyStringValidator(
                "Enter product barcode");
        palletLabelValidator =
            new NonEmptyStringValidator(
                "Enter label");

        widgetCreator.createLabel(multipleOptionsFields,
            "Use flatbed scanner");
        useScannerButton =
            toolkit.createButton(multipleOptionsFields, "", SWT.CHECK);
        useScannerButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setUseScanner(useScannerButton.getSelection());
            }
        });

        palletproductBarcodeLabel = widgetCreator.createLabel(
            multipleOptionsFields,
            "Container product barcode");
        palletproductBarcodeText = (BgcBaseText) createBoundWidget(
            multipleOptionsFields, BgcBaseText.class, SWT.NONE,
            palletproductBarcodeLabel, null, currentMultipleContainer,
            ContainerPeer.PRODUCT_BARCODE.getName(), productBarcodeValidator,
            PRODUCT_BARCODE_BINDING);
        palletproductBarcodeText.addKeyListener(textFieldKeyListener);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletproductBarcodeText.setLayoutData(gd);

        palletproductBarcodeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (palletproductBarcodeTextModified
                    && productBarcodeValidator.validate(
                        currentMultipleContainer.getProductBarcode()).equals(
                        Status.OK_STATUS)) {
                    boolean ok = checkMultipleScanBarcode();
                    setCanLaunchScan(ok);
                    if (!ok)
                        focusControl(palletproductBarcodeText);
                }
                palletproductBarcodeTextModified = false;
            }
        });
        palletproductBarcodeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (!checkingMultipleContainerPosition) {
                    palletproductBarcodeTextModified = true;
                    palletTypesViewer.setInput(null);
                    currentMultipleContainer.setContainerType(null);
                    palletPositionText.setEnabled(true);
                    palletPositionText.setText("");
                }
            }
        });

        palletPositionText = (BgcBaseText) createLabelledWidget(
            multipleOptionsFields, BgcBaseText.class, SWT.NONE,
            "Container label", null);
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletPositionText.setLayoutData(gd);
        palletPositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (palletPositionText.isEnabled()
                    && palletPositionTextModified
                    && palletLabelValidator.validate(
                        palletPositionText.getText()).equals(
                        Status.OK_STATUS)) {
                    BusyIndicator.showWhile(Display.getDefault(),
                        new Runnable() {
                            @Override
                            public void run() {
                                boolean ok = initWithProduct
                                    || checkMultipleContainerPosition();
                                setCanLaunchScan(ok);
                                initCellsWithContainer(currentMultipleContainer);
                                if (!ok) {
                                    focusControl(palletPositionText);
                                    showOnlyPallet(true);
                                }
                                palletPositionTextModified = false;
                            }
                        });
                }
                palletPositionTextModified = false;
            }
        });
        palletPositionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (!isModifyingMultipleFields) {
                    palletPositionTextModified = true;
                    palletTypesViewer.setInput(null);
                    currentMultipleContainer.setContainerType(null);
                }
            }
        });

        createPalletTypesViewer(multipleOptionsFields);

        createPlateToScanField(multipleOptionsFields);

        createScanButton(parent);
    }

    private boolean checkMultipleContainerPosition() {
        checkingMultipleContainerPosition = true;
        initContainersFromPosition(palletPositionText, true, null);
        if (parentContainers == null)
            return false;
        try {
            ContainerWrapper parent = parentContainers.get(0);
            ContainerWrapper containerAtPosition = parent
                .getChildByLabel(palletPositionText.getText());
            List<ContainerTypeWrapper> possibleTypes = null;
            ContainerTypeWrapper typeSelection = null;
            boolean enableCombo = true;
            if (containerAtPosition == null) {
                // free position for the container
                parent.addChild(
                    palletPositionText.getText().replaceAll(
                        parent.getLabel(), ""), currentMultipleContainer);
                possibleTypes = getPossibleTypes(parent.getContainerType()
                    .getChildContainerTypeCollection());
                if (possibleTypes.size() == 1) {
                    typeSelection = possibleTypes.get(0);
                }
            } else {
                String barcodeAtPosition = containerAtPosition
                    .getProductBarcode();
                if (barcodeAtPosition != null && !barcodeAtPosition.isEmpty()
                    && currentMultipleContainer.getProductBarcode() != null) {
                    if (!barcodeAtPosition.equals(currentMultipleContainer
                        .getProductBarcode())) {
                        BgcPlugin
                            .openError(
                                "Position error",
                                NLS.bind(
                                    "There is already a different pallet (product barcode = {0}) in this position of site {1}",
                                    barcodeAtPosition, currentMultipleContainer
                                        .getSite().getNameShort()));
                        appendLog(MessageFormat
                            .format(
                                "Pallet with product barcode {0} is already in position {1} in site {2}",
                                barcodeAtPosition,
                                palletPositionText.getText(),
                                currentMultipleContainer
                                    .getSite().getNameShort()));
                        return false;
                    }
                } else {
                    enableCombo = false;
                    // not barcode before: use barcode entered by user
                    if (containerAtPosition.hasSpecimens()) {
                        // Position already physically used but no barcode was
                        // set (old database compatibility)
                        appendLog(MessageFormat
                            .format(
                                "Position {0} already used with no product barcode and with type {1}. Product barcode {2} will be set.",
                                palletPositionText.getText(),
                                containerAtPosition.getContainerType()
                                    .getName(), currentMultipleContainer
                                    .getProductBarcode()));
                    } else if (containerAtPosition.getContainerType()
                        .getSpecimenTypeCollection().size() > 0) {
                        // Position initialised but not physically used
                        appendLog(NLS
                            .bind(
                                "Position {0} initialised with type {1} and free to be used",
                                palletPositionText.getText(),
                                containerAtPosition.getContainerType()
                                    .getName()));
                    } else {
                        BgcPlugin
                            .openError(
                                "Error",
                                "Container found but can't hold specimens");
                        return false;
                    }
                }
                String newBarcode = currentMultipleContainer
                    .getProductBarcode();
                typeSelection = containerAtPosition.getContainerType();
                possibleTypes = getPossibleTypes(Arrays.asList(typeSelection));
                currentMultipleContainer.initObjectWith(containerAtPosition);
                currentMultipleContainer.reset();
                containerAtPosition.reload();
                if (newBarcode != null) {
                    palletproductBarcodeText.setText(newBarcode);
                }
            }
            palletTypesViewer.getCombo().setEnabled(enableCombo);
            palletTypesViewer.setInput(possibleTypes);
            if (possibleTypes.size() == 0) {
                BgcPlugin
                    .openAsyncError(
                        "Containers Error",
                        "No container type that can hold specimens has been found (if scanner is used, the container should be of size 8*12)");
                typeSelection = null;
                return false;
            }
            if (typeSelection == null)
                palletTypesViewer.getCombo().deselectAll();
            else
                palletTypesViewer.setSelection(new StructuredSelection(
                    typeSelection));
        } catch (Exception ex) {
            BgcPlugin
                .openError(
                    "Values validation",
                    ex);
            appendLog(NLS.bind(
                "ERROR: {0}",
                ex.getMessage()));
            return false;
        } finally {
            checkingMultipleContainerPosition = false;
        }
        return true;
    }

    /**
     * is use scanner, want only 8*12 pallets. Also check the container type can
     * hold specimens
     */
    private List<ContainerTypeWrapper> getPossibleTypes(
        List<ContainerTypeWrapper> childContainerTypeCollection) {
        List<ContainerTypeWrapper> palletTypes =
            new ArrayList<ContainerTypeWrapper>();
        for (ContainerTypeWrapper type : childContainerTypeCollection) {
            if (type.getSpecimenTypeCollection().size() > 0
                && (!useScanner || type.isPallet96()))
                palletTypes.add(type);
        }
        return palletTypes;
    }

    protected boolean checkMultipleScanBarcode() {
        try {
            initWithProduct = false;
            ContainerWrapper palletFoundWithProductBarcode = ContainerWrapper
                .getContainerWithProductBarcodeInSite(
                    SessionManager.getAppService(),
                    currentMultipleContainer.getSite(),
                    currentMultipleContainer.getProductBarcode());
            isNewMultipleContainer = palletFoundWithProductBarcode == null;
            if (palletFoundWithProductBarcode != null) {
                // a container with this barcode exists
                if (!palletFoundWithProductBarcode.isPallet96()) {
                    BgcPlugin
                        .openAsyncError(
                            "Values validation",
                            "A container with this barcode exists but is not a 8*12 container.");
                    return false;
                }
                if (!palletPositionText.getText().isEmpty()
                    && !palletPositionText.getText().equals(
                        palletFoundWithProductBarcode.getLabel())) {
                    // a label was entered but is different from the one set to
                    // the pallet retrieved
                    BgcPlugin
                        .openAsyncError(
                            "Values validation",
                            NLS.bind(
                                "A pallet with barcode {0} is already used in position {1}.",
                                palletFoundWithProductBarcode
                                    .getProductBarcode(),
                                palletFoundWithProductBarcode
                                    .getFullInfoLabel()));
                    return false;
                }
                currentMultipleContainer
                    .initObjectWith(palletFoundWithProductBarcode);
                currentMultipleContainer.reset();
                initWithProduct = true;

                // display the type, which can't be modified.
                palletTypesViewer.getCombo().setEnabled(false);
                palletTypesViewer.setInput(Arrays
                    .asList(palletFoundWithProductBarcode.getContainerType()));
                palletTypesViewer.setSelection(new StructuredSelection(
                    palletFoundWithProductBarcode.getContainerType()));
                appendLog(MessageFormat
                    .format(
                        "Product barcode {0} already exists at position {1} of site {2} with type {3}.",
                        currentMultipleContainer.getProductBarcode(),
                        palletFoundWithProductBarcode.getLabel(),
                        currentMultipleContainer.getSite().getNameShort(),
                        palletFoundWithProductBarcode.getContainerType()
                            .getName()));
                // can't modify the position if exists already
                palletPositionText.setEnabled(false);
                focusPlateToScan();
            }
        } catch (Exception ex) {
            BgcPlugin
                .openError(
                    "Values validation",
                    ex);
            appendLog(NLS.bind(
                "ERROR: {0}",
                ex.getMessage()));
            return false;
        }
        return true;
    }

    @Override
    protected void defaultInitialisation() {
        super.defaultInitialisation();
        boolean use = !mode.isSingleMode() && useScanner;
        useScannerButton.setSelection(use);
        setUseScanner(use);
    }

    @Override
    protected void setUseScanner(boolean use) {
        useScanner = use;
        showPlateToScanField(use && !mode.isSingleMode());
        widgetCreator.showWidget(scanButton, use);
        widgetCreator.showWidget(palletproductBarcodeLabel, use);
        widgetCreator.showWidget(palletproductBarcodeText, use);
        widgetCreator.setBinding(PRODUCT_BARCODE_BINDING, use);
        if (fakeScanComposite != null)
            widgetCreator.showWidget(fakeScanComposite, use);
        showScanTubeAloneSwitch(use);
        if (palletTypesViewer != null) {
            palletTypesViewer.setInput(null);
            palletTypesViewer.getCombo().deselectAll();
        }
        if (use) {
            if (isScanTubeAloneMode())
                // want to deactivate it at first in scan mode
                toggleScanTubeAloneMode();
        } else {
            palletproductBarcodeText.setText("");
            currentMultipleContainer.setContainerType(null);
            setScanHasBeenLaunched(true);
            setScanValid(true);
            if (!isScanTubeAloneMode())
                // want to activate tube alone mode if do not use the scanner
                toggleScanTubeAloneMode();

        }
        try {
            setValues();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.setUseScanner(use);
        page.layout(true, true);
        checkPalletContainerTypes();
    }

    /**
     * assign multiple: list of container types
     */
    private void createPalletTypesViewer(Composite parent)
        throws ApplicationException {
        initPalletContainerTypes();
        palletTypesViewer = widgetCreator.createComboViewer(parent,
            "Container Type", null,
            null,
            "A pallet type should be selected",
            true, PALLET_TYPES_BINDING, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentMultipleContainer
                        .setContainerType((ContainerTypeWrapper) selectedObject);
                    palletTypesViewer.getCombo().setFocus();
                    if (!useScanner)
                        displayPalletPositions();
                }
            }, new BiobankLabelProvider());
    }

    /**
     * Multiple assign. Initialise list of possible pallets (8*12)
     */
    private void initPalletContainerTypes() throws ApplicationException {
        palletContainerTypes = ContainerTypeWrapper.getContainerTypesPallet96(
            SessionManager.getAppService(), SessionManager.getUser()
                .getCurrentWorkingSite());
    }

    private void checkPalletContainerTypes() {
        if (!isSingleMode() && useScanner && palletContainerTypes.size() == 0) {
            BgcPlugin
                .openAsyncError(
                    "No Pallet defined?",
                    "No child container types found with 8 rows and 12 columns");
        }
    }

    @Override
    protected void enableFields(boolean enable) {
        super.enableFields(enable);
        multipleOptionsFields.setEnabled(enable);
    }

    @Override
    protected boolean fieldsValid() {
        if (mode.isSingleMode())
            return true;
        IStructuredSelection selection =
            (IStructuredSelection) palletTypesViewer
                .getSelection();
        return isPlateValid()
            && (!useScanner || productBarcodeValidator.validate(
                palletproductBarcodeText.getText()).equals(Status.OK_STATUS))
            && palletLabelValidator.validate(palletPositionText.getText())
                .equals(Status.OK_STATUS) && selection.size() > 0;
    }

    @Override
    protected void saveForm() throws Exception {
        if (mode.isSingleMode())
            saveSingleSpecimen();
        else
            saveMultipleSpecimens();
        setFinished(false);
        SessionManager.log("save", null,
            "SpecimenAssign");
    }

    private void saveMultipleSpecimens() throws Exception {
        if (saveEvenIfMissing) {
            SpecimenAssignResInfo res;
            try {
                Map<RowColPos, PalletCell> cells = getCells();
                List<SpecimenInfo> specInfos =
                    new ArrayList<SpecimenAssignSaveAction.SpecimenInfo>();
                for (Entry<RowColPos, PalletCell> entry : cells.entrySet()) {
                    RowColPos rcp = entry.getKey();
                    PalletCell cell = entry.getValue();
                    if (cell != null
                        && (cell.getStatus() == UICellStatus.NEW || cell
                            .getStatus() == UICellStatus.MOVED)) {
                        SpecimenWrapper specimen = cell.getSpecimen();
                        if (specimen != null) {
                            SpecimenInfo specInfo = new SpecimenInfo();
                            specInfo.specimenId = specimen.getId();
                            specInfo.position = rcp;
                            specInfos.add(specInfo);
                        }
                    }
                }
                if (currentMultipleContainer.getId() == null) {
                    ContainerSaveAction csAction = new ContainerSaveAction();
                    csAction.parentId =
                        currentMultipleContainer.getParentContainer()
                            .getId();
                    csAction.position = currentMultipleContainer
                        .getPositionAsRowCol();
                    csAction.barcode =
                        currentMultipleContainer.getProductBarcode();
                    csAction.typeId =
                        currentMultipleContainer.getContainerType()
                            .getId();
                    csAction.activityStatus =
                        currentMultipleContainer.getActivityStatus();
                    csAction.siteId =
                        currentMultipleContainer.getSite().getId();

                    Integer containerId =
                        SessionManager.getAppService().doAction(csAction)
                            .getId();

                    res = SessionManager.getAppService().doAction(
                        new SpecimenAssignSaveAction(containerId, specInfos));
                } else {
                    res = SessionManager.getAppService().doAction(
                        new SpecimenAssignSaveAction(currentMultipleContainer
                            .getId(), specInfos));
                }
                if (isNewMultipleContainer) {
                    if (res.parentContainerId != null) {
                        appendLog(MessageFormat
                            .format(
                                "ADDED: Pallet {0} of type {1} to position {2} of site {3}",
                                res.parentBarcode, res.parentTypeName,
                                res.parentLabel, res.siteName));
                    } else {
                        throw new RuntimeException(
                            "problem with parent container creation");
                    }
                }
            } catch (Exception ex) {
                setScanHasBeenLaunched(false, true);
                throw ex;
            }
            StringBuffer sb =
                new StringBuffer(
                    "SPECIMENS ASSIGNED:\n");
            for (SpecimenResInfo sp : res.specimens) {
                String posStr = sp.position;
                if (posStr == null) {
                    posStr =
                        "none";
                }
                sb.append(MessageFormat
                    .format(
                        "ASSIGNED position {0} (site {1}) to specimen {2} - Type: {3} - Patient: {4} - Visit#: {5}\n",
                        posStr, res.siteName, sp.inventoryId, sp.typeName,
                        sp.patientPNumber, sp.visitNumber));
            }
            appendLog(sb.toString());

            appendLog(MessageFormat
                .format(
                    "ASSIGNING: {0} specimens added to pallet {1} of site {2}",
                    res.specimens.size(), palletPositionText.getText(),
                    currentMultipleContainer.getSite().getNameShort()));
            setFinished(false);
        }
    }

    private void saveSingleSpecimen() throws Exception {
        SpecimenInfo specInfo = new SpecimenInfo();
        specInfo.specimenId = singleSpecimen.getId();
        specInfo.position = singleSpecimen.getPosition();
        SpecimenAssignResInfo res = SessionManager.getAppService().doAction(
            new SpecimenAssignSaveAction(singleSpecimen.getParentContainer()
                .getId(), Arrays.asList(specInfo)));

        if (res.specimens.size() != 1) {
            throw new Exception("result problem");
        }
        SpecimenResInfo spRes = res.specimens.get(0);
        appendLog(MessageFormat
            .format(
                "ASSIGNED position {0} (site {1}) to specimen {2} - Type: {3} - Patient: {4} - Visit#: {5}",
                spRes.position, spRes.centerName, spRes.inventoryId,
                spRes.typeName, spRes.patientPNumber, spRes.visitNumber));
    }

    @Override
    public void setValues() throws Exception {
        super.setValues();
        parentContainers = null;
        // resultShownValue.setValue(Boolean.FALSE);
        // the 2 following lines are needed. The validator won't update if don't
        // do that (why ?)
        inventoryIdText.setText("**");
        inventoryIdText.setText("");
        singleTypeText.setText("");
        singleCollectionDateText.setText("");
        oldSinglePositionText.setText("");
        oldSinglePositionCheckText.setText("");
        newSinglePositionText.setText("");
        displayOldSingleFields(false);

        showOnlyPallet(true);
        form.layout(true, true);
        if (!mode.isSingleMode()) {
            palletproductBarcodeText.setFocus();
        }
        foundSpecNull.setValue(true);
        singleSpecimen.reset(); // reset internal values
        setDirty(false);
        initWithProduct = false;
        if (mode.isSingleMode())
            focusControl(inventoryIdText);
        else if (useScanner)
            focusControl(palletproductBarcodeText);
        else
            focusControl(palletPositionText);
    }

    @Override
    public void reset(boolean resetAll) {
        super.reset(resetAll);
        String productBarcode = "";
        String label = "";
        ContainerTypeWrapper type = null;

        if (!resetAll) { // keep fields values
            productBarcode = palletproductBarcodeText.getText();
            label = palletPositionText.getText();
            type = currentMultipleContainer.getContainerType();
        } else {
            if (palletTypesViewer != null) {
                palletTypesViewer.setInput(null);
                palletTypesViewer.getCombo().deselectAll();
            }
            removeRescanMode();
            freezerWidget.setSelection(null);
            hotelWidget.setSelection(null);
            palletWidget.setCells(null);
        }
        setScanHasBeenLaunched(isSingleMode() || !useScanner);
        initPalletValues();

        palletproductBarcodeText.setText(productBarcode);
        productBarcodeValidator.validate(productBarcode);
        palletPositionText.setText(label);
        palletLabelValidator.validate(label);
        currentMultipleContainer.setContainerType(type);
        if (resetAll) {
            setDirty(false);
            useNewProductBarcode = false;
        }
        canSaveSingleSpecimen.setValue(!isSingleMode());
    }

    @Override
    protected void setBindings(boolean isSingleMode) {
        widgetCreator.setBinding(INVENTORY_ID_BINDING, isSingleMode);
        widgetCreator.setBinding(OLD_SINGLE_POSITION_BINDING, isSingleMode);
        oldSinglePositionCheckText.setText("?");
        widgetCreator.setBinding(NEW_SINGLE_POSITION_BINDING, isSingleMode);
        widgetCreator.setBinding(PRODUCT_BARCODE_BINDING, !isSingleMode
            && useScanner);
        widgetCreator.setBinding(PALLET_TYPES_BINDING, !isSingleMode);
        super.setBindings(isSingleMode);
        setScanHasBeenLaunched(isSingleMode || !useScanner);
        checkPalletContainerTypes();
        setCanLaunchScan(true);
    }

    @Override
    protected void showModeComposite(Mode mode) {
        boolean single = mode.isSingleMode();
        if (single)
            setFirstControl(inventoryIdText);
        else if (useScanner)
            setFirstControl(palletproductBarcodeText);
        else
            setFirstControl(palletPositionText);
        try {
            setValues();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.showModeComposite(mode);
    }

    /**
     * Multiple assign
     */
    @Override
    protected boolean canScanTubeAlone(PalletCell cell) {
        return super.canScanTubeAlone(cell)
            || cell.getStatus() == UICellStatus.MISSING;
    }

    /**
     * Multiple assign
     */
    @Override
    protected void launchScanAndProcessResult() {
        super.launchScanAndProcessResult();
        page.layout(true, true);
        book.reflow(true);
        cancelConfirmWidget.setFocus();
    }

    /**
     * Multiple assign
     */
    @Override
    protected void beforeScanThreadStart() {
        showOnlyPallet(false, false);
        currentMultipleContainer.setSite(SessionManager.getUser()
            .getCurrentWorkingSite());
        currentMultipleContainer
            .setContainerType((ContainerTypeWrapper) ((IStructuredSelection) palletTypesViewer
                .getSelection()).getFirstElement());
        isFakeScanLinkedOnly = fakeScanLinkedOnlyButton != null
            && fakeScanLinkedOnlyButton.getSelection();
    }

    /**
     * Multiple assign
     */
    @Override
    protected void afterScanAndProcess(Integer rowOnly) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                cancelConfirmWidget.setFocus();
                displayPalletPositions();
                palletWidget.setCells(getCells());
                setDirty(true);
                setRescanMode();
                page.layout(true, true);
                form.reflow(true);
            }
        });
    }

    /**
     * Multiple assign
     */
    protected void displayPalletPositions() {
        if (currentMultipleContainer.hasParentContainer()) {
            ContainerWrapper hotelContainer = currentMultipleContainer
                .getParentContainer();
            ContainerWrapper freezerContainer = hotelContainer
                .getParentContainer();

            if (freezerContainer != null) {
                freezerLabel.setText(freezerContainer.getFullInfoLabel());
                freezerWidget.setContainerType(freezerContainer
                    .getContainerType());
                freezerWidget
                    .setSelection(hotelContainer.getPositionAsRowCol());
                freezerWidget.redraw();
            }

            hotelLabel.setText(hotelContainer.getFullInfoLabel());
            hotelWidget.setContainerType(hotelContainer.getContainerType());
            hotelWidget.setSelection(currentMultipleContainer
                .getPositionAsRowCol());
            hotelWidget.redraw();

            palletLabel.setText(palletPositionText.getText());

            palletWidget.setContainerType(
                currentMultipleContainer.getContainerType(),
                ScanPalletDisplay.SAMPLE_WIDTH);
            setContainerType(currentMultipleContainer
                .getContainerType().getWrappedObject());
            palletWidget.setCells(getCells());

            showOnlyPallet(false);

            widgetCreator.showWidget(freezerLabel, freezerContainer != null);
            widgetCreator.showWidget(freezerWidget, freezerContainer != null);
            page.layout(true, true);
        }
    }

    /**
     * Multiple assign
     */
    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        if (currentMultipleContainer.hasSpecimens()) {
            Map<RowColPos, PalletCell> palletScanned =
                new HashMap<RowColPos, PalletCell>();
            for (RowColPos pos : currentMultipleContainer.getSpecimens()
                .keySet()) {
                if (pos.getRow() != 0 && pos.getCol() != 2) {
                    palletScanned.put(pos,
                        new PalletCell(new ScanCell(pos.getRow(), pos.getCol(),
                            currentMultipleContainer.getSpecimens().get(pos)
                                .getInventoryId())));
                }
            }
            return palletScanned;
        }
        if (isFakeScanLinkedOnly) {
            return PalletCell.getRandomSpecimensNotAssigned(SessionManager
                .getAppService(), currentMultipleContainer.getSite()
                .getId());
        }
        return PalletCell.getRandomSpecimensAlreadyAssigned(SessionManager
            .getAppService(), currentMultipleContainer.getSite().getId());
    }

    @Override
    protected void doBeforeSave() throws Exception {
        if (!mode.isSingleMode()) {
            saveEvenIfMissing = true;
            if (currentScanState == UICellStatus.MISSING) {
                boolean save =
                    BgcPlugin
                        .openConfirm(
                            "Really save?",
                            "Specimens are missing from this pallet, are you sure you want to save? These specimens will still be registered at their current position.");
                if (!save) {
                    setDirty(true);
                    saveEvenIfMissing = false;
                }
            }
        }
    }

    /**
     * Multiple assign
     */
    @Override
    protected void createFakeOptions(Composite fieldsComposite) {
        fakeScanComposite = toolkit.createComposite(fieldsComposite);
        fakeScanComposite.setLayout(new GridLayout());
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        fakeScanComposite.setLayoutData(gd);
        fakeScanLinkedOnlyButton =
            toolkit.createButton(fakeScanComposite,
                "Select linked only specimens", SWT.RADIO);
        fakeScanLinkedOnlyButton.setSelection(true);
        toolkit.createButton(fakeScanComposite,
            "Select linked and assigned specimens", SWT.RADIO);
    }

    @Override
    protected Mode initialisationMode() {
        return mode;
    }

    @Override
    protected Composite getFocusedComposite(boolean single) {
        if (single)
            return inventoryIdText;
        if (useScanner)
            return palletproductBarcodeText;
        return palletPositionText;
    }

    @Override
    protected boolean needPlate() {
        return useScanner;
    }

    @Override
    protected Action<ProcessResult> getCellProcessAction(Integer centerId,
        CellInfo cell, Locale locale) {
        return new SpecimenAssignProcessAction(getProcessData(), centerId,
            cell, locale);
    }

    @Override
    protected Action<ProcessResult> getPalletProcessAction(Integer centerId,
        Map<RowColPos, CellInfo> cells, boolean isRescanMode, Locale locale) {
        return new SpecimenAssignProcessAction(getProcessData(), centerId,
            cells,
            isRescanMode, locale);
    }

    protected AssignProcessInfo getProcessData() {
        return new AssignProcessInfo(
            currentMultipleContainer.getWrappedObject());
    }
}
