package edu.ualberta.med.biobank.forms.linkassign;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
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
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.scanmanually.ScanTubesManually;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.helpers.ScanAssignHelper;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * A class that allows the user to perform a specimen assign, which means that specimens are linked
 * with container positions. This functionality is only available to sites.
 * 
 * @author Delphine
 * 
 */
public class SpecimenAssignEntryForm extends AbstractLinkAssignEntryForm {

    private static final I18n i18n = I18nFactory.getI18n(SpecimenAssignEntryForm.class);

    private static BgcLogger log = BgcLogger.getLogger(SpecimenAssignEntryForm.class.getName());

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenAssignEntryForm";

    private static Mode mode = Mode.MULTIPLE;

    @SuppressWarnings("nls")
    private static final String INVENTORY_ID_BINDING = "inventoryId-binding";

    @SuppressWarnings("nls")
    private static final String NEW_SINGLE_POSITION_BINDING = "newSinglePosition-binding";

    @SuppressWarnings("nls")
    private static final String OLD_SINGLE_POSITION_BINDING = "oldSinglePosition-binding";

    @SuppressWarnings("nls")
    private static final String PRODUCT_BARCODE_BINDING = "productBarcode-binding";

    @SuppressWarnings("nls")
    private static final String PALLET_TYPES_BINDING = "palletType-binding";

    protected static boolean usingFlatbedScanner = true;

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
    private final WritableValue foundSpecNull = new WritableValue(Boolean.TRUE,
        Boolean.class);

    // for multiple specimens assign
    private ContainerWrapper currentMultipleContainer;
    protected boolean palletproductBarcodeTextModified;
    private NonEmptyStringValidator productBarcodeValidator;
    private NonEmptyStringValidator palletLabelValidator;
    private BgcBaseText palletLabelText;
    protected boolean useNewProductBarcode;
    private ComboViewer palletTypesViewer;
    protected boolean palletPositionTextModified;
    private BgcBaseText palletproductBarcodeText;
    private boolean saveEvenIfMissing;
    private Composite multipleOptionsFields;
    private Button useScannerButton;
    private Label palletproductBarcodeLabel;
    private boolean isNewMultipleContainer;
    private boolean checkingMultipleContainerPosition;

    protected SpecimenAssignResInfo res;

    public SpecimenAssignEntryForm() {
        currentMultipleContainer = new ContainerWrapper(SessionManager.getAppService());
    }

    @Override
    protected void init() throws Exception {
        super.init();
        palletScanManagement = new PalletScanManagement(this, new ScanTubesManually());
        setCanLaunchScan(false);
        initPalletValues();
        scanMultipleWithHandheldInput = true;
    }

    /**
     * Multiple. initialize pallet
     */
    @SuppressWarnings("nls")
    private void initPalletValues() {
        try {
            currentMultipleContainer.initObjectWith(
                new ContainerWrapper(SessionManager.getAppService()));
            currentMultipleContainer.reset();
            currentMultipleContainer.setActivityStatus(ActivityStatus.ACTIVE);
            currentMultipleContainer.setSite(SessionManager.getUser().getCurrentWorkingSite());
        } catch (Exception e) {
            log.error("Error while reseting pallet values", e);
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected String getActivityTitle() {
        // TR: dialog title
        return i18n.tr("Specimen Assign");
    }

    @Override
    public BgcLogger getErrorLogger() {
        return log;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getFormTitle() {
        // TR: form title
        return i18n.tr("Assign position or move specimens");
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

    @SuppressWarnings("nls")
    @Override
    protected String getOkMessage() {
        // TR: title area message
        return i18n.tr("Assign position to specimens or move specimens");
    }

    @Override
    public String getNextOpenedFormId() {
        return ID;
    }

    @Override
    protected void createCommonFields(Composite commonFieldsComposite) {
        BgcBaseText siteLabel = createReadOnlyLabelledField(
            commonFieldsComposite, SWT.NONE, Site.NAME.singular().toString());
        siteLabel.setText(SessionManager.getUser().getCurrentWorkingCenter().getNameShort());
    }

    @Override
    protected int getLeftSectionWidth() {
        return 450;
    }

    @SuppressWarnings("nls")
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
            // TR: label
            i18n.tr("Cabinet specimen"));
        cabinetCheckButton = toolkit.createButton(fieldsComposite, StringUtil.EMPTY_STRING, SWT.CHECK);
        cabinetCheckButton
            // TR: tooltip
            .setToolTipText(i18n.tr(
                "Old cabinet specimen with 4 letters were transformed to 5 letters (C+4letters)."));
        cabinetCheckButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (cabinetCheckButton.getSelection()) {
                    if (inventoryIdText.getText().length() == 4) {
                        // compatibility with old cabinet specimens imported
                        // 4 letters specimens are now C+4letters
                        inventoryIdText.setText("C" + inventoryIdText.getText());
                        BgcPlugin.focusControl(inventoryIdText);
                    }
                }
            }
        });
        // this check box is there only for cbsr : specimens imported from
        // cabinet from the old database where 4 letters. A 'C' has been added
        // to them to make them different from the freezer specimen with exactly
        // the same inventory id
        CenterWrapper<?> center = SessionManager.getUser().getCurrentWorkingCenter();
        boolean cbsrCenter = ((center != null) && center.getNameShort().equals("CBSR"));
        widgetCreator.showWidget(cabinetCheckButtonLabel, cbsrCenter);
        widgetCreator.showWidget(cabinetCheckButton, cbsrCenter);

        // inventoryID
        Label inventoryIdLabel = widgetCreator.createLabel(fieldsComposite,
            Specimen.PropertyName.INVENTORY_ID.toString());
        inventoryIdText = (BgcBaseText) createWidget(fieldsComposite, BgcBaseText.class, SWT.NONE,
            StringUtil.EMPTY_STRING);
        inventoryIdText.addKeyListener(textFieldKeyListener);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (inventoryIdModified)
                    try {
                        retrieveSingleSpecimenData();
                    } catch (Exception ex) {
                        BgcPlugin.openError(
                            // TR: dialog title
                            i18n.tr("Move - specimen error"), ex);
                        BgcPlugin.focusControl(inventoryIdText);

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
            // TR: validation error message
            i18n.tr("Enter a valid inventory ID")) {
            @Override
            public IStatus validate(Object value) {
                if ((Boolean) foundSpecNull.getValue()) {
                    showDecoration();
                    return ValidationStatus
                        // TR: validation error message
                        .error(i18n.tr("Enter a valid inventory ID"));
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

        singleTypeText = (BgcBaseText) createLabelledWidget(
            fieldsComposite,
            BgcBaseText.class,
            SWT.NONE,
            // TR: form label
            i18n.tr("Type"));
        singleTypeText.setEnabled(false);

        singleCollectionDateText = (BgcBaseText) createLabelledWidget(
            fieldsComposite, BgcBaseText.class, SWT.NONE,
            // TR: form label
            i18n.tr("Collection date"));
        singleCollectionDateText.setEnabled(false);

        createSinglePositionFields(fieldsComposite);
    }

    /**
     * 
     * Single assign. Search the specimen, if find it, display related information
     */
    @SuppressWarnings("nls")
    protected void retrieveSingleSpecimenData() throws Exception {
        String inventoryId = inventoryIdText.getText();
        singleTypeText.setText(StringUtil.EMPTY_STRING);
        singleCollectionDateText.setText(StringUtil.EMPTY_STRING);
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

        appendLog(NLS.bind("Getting informations for inventoryID {0}",
            singleSpecimen.getInventoryId()));
        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
            SessionManager.getAppService(), singleSpecimen.getInventoryId());
        foundSpecNull.setValue(false);
        if (foundSpecimen == null) {
            foundSpecNull.setValue(true);
            throw new Exception(
                // TR: exception message
                i18n.tr("No specimen found with inventoryId {0}",
                    singleSpecimen.getInventoryId()));
        }

        singleSpecimen.initObjectWith(foundSpecimen);
        if (singleSpecimen.isUsedInDispatch()) {
            foundSpecNull.setValue(true);
            throw new Exception(
                // TR: exception message
                i18n.tr("This specimen is currently in transit in a dispatch."));
        }
        if (!SessionManager.getUser().getCurrentWorkingCenter()
            .equals(singleSpecimen.getCurrentCenter())) {
            foundSpecNull.setValue(true);
            throw new Exception(i18n.tr(
                // TR: exception message
                "This specimen is currently in center ''{0}''.",
                singleSpecimen.getCurrentCenter().getNameShort()));
        }
        singleTypeText.setText(singleSpecimen.getSpecimenType().getNameShort());
        singleCollectionDateText.setText(singleSpecimen.getTopSpecimen().getFormattedCreatedAt());
        String positionString = singleSpecimen.getPositionString(true, false);
        if (positionString == null) {
            displayOldSingleFields(false);
            // TR: text box text
            positionString = i18n.trc("position string", "none");
            BgcPlugin.focusControl(newSinglePositionText);
        } else {
            displayOldSingleFields(true);
            oldSinglePositionCheckText.setText(oldSinglePositionCheckText.getText());
            BgcPlugin.focusControl(oldSinglePositionCheckText);
        }
        oldSinglePositionText.setText(positionString);
        appendLog(NLS.bind("Specimen {0}: current position={1}",
            singleSpecimen.getInventoryId(), positionString));
        canSaveSingleSpecimen.setValue(true);
    }

    /**
     * Single assign: Some fields will be displayed only if the specimen has already a position
     */
    @SuppressWarnings("nls")
    private void createSinglePositionFields(Composite fieldsComposite) {
        // for move mode: display old position retrieved from database
        oldSinglePositionLabel = widgetCreator.createLabel(fieldsComposite,
            // TR: label
            i18n.tr("Old position"));
        oldSinglePositionText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite,
            BgcBaseText.class,
            SWT.NONE,
            oldSinglePositionLabel,
            new String[0],
            null,
            null);
        oldSinglePositionText.setEnabled(false);
        oldSinglePositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for move mode: field to enter old position. Check needed to be sure
        // nothing is wrong with the specimen
        oldSinglePositionCheckLabel = widgetCreator.createLabel(
            fieldsComposite,
            // TR: label
            i18n.tr("Old position check"));
        oldSinglePositionCheckValidator = new AbstractValidator(
            // TR: validation error message
            i18n.tr("Enter correct old position")) {
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
        oldSinglePositionCheckText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite,
            BgcBaseText.class,
            SWT.NONE,
            oldSinglePositionCheckLabel,
            new String[0],
            new WritableValue(StringUtil.EMPTY_STRING, String.class),
            oldSinglePositionCheckValidator,
            OLD_SINGLE_POSITION_BINDING);
        oldSinglePositionCheckText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for all modes: position to be assigned to the specimen
        newSinglePositionLabel = widgetCreator.createLabel(fieldsComposite,
            AbstractPosition.NAME.singular().toString());
        newSinglePositionValidator = new StringLengthValidator(4,
            // TR: label
            i18n.tr("Enter a position"));
        displayOldSingleFields(false);
        newSinglePositionText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite,
            BgcBaseText.class,
            SWT.NONE,
            newSinglePositionLabel,
            new String[0],
            new WritableValue(StringUtil.EMPTY_STRING, String.class),
            newSinglePositionValidator,
            NEW_SINGLE_POSITION_BINDING);
        newSinglePositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (positionTextModified
                    && (newSinglePositionValidator.validate(newSinglePositionText.getText())
                        == Status.OK_STATUS)) {
                    BusyIndicator.showWhile(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                ScanAssignHelper.getContainerByLabel(newSinglePositionText.getText());
                                checkPositionAndSpecimen(inventoryIdText, newSinglePositionText);
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
        newSinglePositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        displayOldSingleFields(false);
    }

    /**
     * Single assign: show or hide old positions fields
     */
    @SuppressWarnings("nls")
    private void displayOldSingleFields(boolean displayOld) {
        widgetCreator.setBinding(OLD_SINGLE_POSITION_BINDING, displayOld);
        widgetCreator.showWidget(oldSinglePositionLabel, displayOld);
        widgetCreator.showWidget(oldSinglePositionText, displayOld);
        widgetCreator.showWidget(oldSinglePositionCheckLabel, displayOld);
        widgetCreator.showWidget(oldSinglePositionCheckText, displayOld);
        if (displayOld) {
            // TR: label
            newSinglePositionLabel.setText(i18n.tr("New position:"));
        } else {
            // TR: label
            newSinglePositionLabel.setText(i18n.tr("Position:"));
            oldSinglePositionCheckText.setText(oldSinglePositionText.getText());
        }
        page.layout(true, true);
    }

    @SuppressWarnings("nls")
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

        productBarcodeValidator = new NonEmptyStringValidator(
            // TR: validation error message
            i18n.tr("Enter product barcode"));
        palletLabelValidator = new NonEmptyStringValidator(
            // TR: validation error message
            i18n.tr("Enter label"));

        widgetCreator.createLabel(multipleOptionsFields,
            // TR: label
            i18n.tr("Decode pallet"));
        useScannerButton = toolkit.createButton(multipleOptionsFields,
            StringUtil.EMPTY_STRING, SWT.CHECK);
        useScannerButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setUseScanner(useScannerButton.getSelection());
            }
        });

        palletproductBarcodeLabel = widgetCreator.createLabel(multipleOptionsFields,
            // TR: label
            i18n.tr("Container product barcode"));
        palletproductBarcodeText = (BgcBaseText) createBoundWidget(
            multipleOptionsFields,
            BgcBaseText.class,
            SWT.NONE,
            palletproductBarcodeLabel,
            null,
            currentMultipleContainer,
            ContainerPeer.PRODUCT_BARCODE.getName(),
            productBarcodeValidator,
            PRODUCT_BARCODE_BINDING);
        palletproductBarcodeText.addKeyListener(textFieldKeyListener);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletproductBarcodeText.setLayoutData(gd);

        palletproductBarcodeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (!checkingMultipleContainerPosition) {
                    palletproductBarcodeTextModified = true;
                    palletTypesViewer.setInput(null);
                    // log.debug("clearing selections in palletTypesViewer");
                    currentMultipleContainer.setContainerType(null);
                    palletLabelText.setEnabled(true);
                    palletLabelText.setText(StringUtil.EMPTY_STRING);
                }
            }
        });
        palletproductBarcodeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (palletproductBarcodeTextModified
                    && productBarcodeValidator.validate(
                        currentMultipleContainer.getProductBarcode()).equals(Status.OK_STATUS)) {
                    boolean ok = checkMultipleScanBarcode();
                    setCanLaunchScan(ok);
                    if (!ok) {
                        BgcPlugin.focusControl(palletproductBarcodeText);
                    }
                }
                palletproductBarcodeTextModified = false;
            }
        });

        palletLabelText = (BgcBaseText) createLabelledWidget(
            multipleOptionsFields, BgcBaseText.class, SWT.NONE,
            // TR: label
            i18n.tr("Container label"), null);
        palletLabelText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletLabelText.setLayoutData(gd);
        palletLabelText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                final String palletLabel = palletLabelText.getText();
                final String palletBarcode = palletproductBarcodeText.getText();

                if (palletLabelText.isEnabled() && palletPositionTextModified
                    && palletLabelValidator.validate(palletLabel).equals(Status.OK_STATUS)) {
                    BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            checkingMultipleContainerPosition = true;
                            ContainerWrapper container =
                                ScanAssignHelper.getOrCreateContainerByLabel(palletLabel);

                            if (container == null) {
                                appendLog(NLS.bind(
                                    "ERROR: could not get container with label {0}", palletLabel));
                                return;
                            }

                            currentMultipleContainer = container;

                            boolean ok = checkAndUpdateContainer(container, palletLabel);
                            setCanLaunchScan(ok);
                            initCellsWithContainer(currentMultipleContainer);
                            currentMultipleContainer.setLabel(palletLabel);
                            currentMultipleContainer.setProductBarcode(palletBarcode);
                            if (!ok) {
                                BgcPlugin.focusControl(palletLabelText);
                                showOnlyPallet(true);
                            } else if (palletTypesViewer.getCombo().getEnabled()) {
                                BgcPlugin.focusControl(palletTypesViewer.getCombo());
                            }
                            palletPositionTextModified = false;
                            checkingMultipleContainerPosition = false;
                        }
                    });
                }
                palletPositionTextModified = false;
            }
        });
        palletLabelText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                palletPositionTextModified = true;
                palletTypesViewer.setInput(null);
                // log.debug("clearing selections in palletTypesViewer");
                currentMultipleContainer.setContainerType(null);
            }
        });

        createPalletTypesViewer(multipleOptionsFields);
        createScanButton(parent);
    }

    @SuppressWarnings("nls")
    private boolean checkAndUpdateContainer(ContainerWrapper container, String palletLabel) {
        try {
            ContainerTypeWrapper typeSelection;
            List<ContainerTypeWrapper> possibleTypes =
                ScanAssignHelper.getContainerTypes(container, usingFlatbedScanner);

            if (possibleTypes.size() == 1) {
                typeSelection = possibleTypes.get(0);
            } else {
                typeSelection = container.getContainerType();
            }

            if (!checkExistingContainerValid(container)) return false;

            String newBarcode = currentMultipleContainer.getProductBarcode();

            currentMultipleContainer.initObjectWith(container);
            if (!currentMultipleContainer.isNew()) {
                currentMultipleContainer.reset();
            }

            String msg = ScanAssignHelper.containerProductBarcodeUpdateLogMessage(
                container, newBarcode, palletLabel);
            if (!msg.isEmpty()) {
                appendLog(msg);
            }

            if ((newBarcode != null) && !newBarcode.isEmpty()) {
                palletproductBarcodeText.setText(newBarcode);
            }

            palletTypesViewer.getCombo().setEnabled(false);
            palletTypesViewer.setInput(possibleTypes);
            if (possibleTypes.isEmpty()) {
                BgcPlugin.openAsyncError(
                    // TR: dialog title
                    i18n.tr("Containers Error"),
                    // TR: dialog message
                    i18n.tr("No container type that can hold specimens has been found "
                        + "(if scanner is used, the container should be of size 8*12 or 10*10)"));
                typeSelection = null;
                return false;
            }
            palletTypesViewer.getCombo().setEnabled(possibleTypes.size() > 1);

            if (typeSelection == null) {
                palletTypesViewer.getCombo().deselectAll();
            } else {
                palletTypesViewer.setSelection(new StructuredSelection(typeSelection));
            }
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Values validation"), ex);
            appendLog(NLS.bind("ERROR: {0}", ex.getMessage()));
            return false;
        }

        return true;
    }

    @SuppressWarnings("nls")
    private boolean checkExistingContainerValid(ContainerWrapper container) {
        switch (ScanAssignHelper.checkExistingContainerValid(container)) {
        case VALID:
            String productBarcode = container.getProductBarcode();
            if ((productBarcode != null) && !productBarcode.isEmpty()
                && (currentMultipleContainer.getProductBarcode() != null)) {
                if (!productBarcode.equals(currentMultipleContainer.getProductBarcode())) {
                    ScanAssignHelper.containerPositionError(
                        activityLogger,
                        container,
                        productBarcode,
                        palletLabelText.getText());
                    return false;
                }
            }
            return true;

        case IS_NEW:
            return true;

        case DOES_NOT_HOLD_SPECIMENS:
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Error"),
                // TR: dialog message
                i18n.tr("Container selected can't hold specimens"));
            return false;

        default:
            throw new IllegalArgumentException("container is invalid");
        }
    }

    @SuppressWarnings("nls")
    protected boolean checkMultipleScanBarcode() {
        try {
            Container qryContainer = new Container();
            qryContainer.setProductBarcode(currentMultipleContainer.getProductBarcode());
            List<Container> containers = SessionManager.getAppService().doAction(
                new ContainerGetInfoAction(qryContainer,
                    currentMultipleContainer.getSite().getWrappedObject())).getList();

            if (containers.size() > 1) {
                throw new IllegalStateException("multiple containers found with product barcode:"
                    + qryContainer.getProductBarcode());
            } else if (containers.isEmpty()) {
                isNewMultipleContainer = true;
                return true;
            }

            ContainerWrapper palletFoundWithProductBarcode = new ContainerWrapper(
                SessionManager.getAppService(), containers.get(0));
            isNewMultipleContainer = false;

            if (!ScanAssignHelper.isContainerValid(
                palletFoundWithProductBarcode, palletLabelText.getText())) {
                return false;
            }

            currentMultipleContainer.initObjectWith(palletFoundWithProductBarcode);
            currentMultipleContainer.reset();

            palletLabelText.setText(palletFoundWithProductBarcode.getLabel());

            // display the type, which can't be modified.
            palletTypesViewer.getCombo().setEnabled(false);
            palletTypesViewer.setInput(Arrays.asList(
                palletFoundWithProductBarcode.getContainerType()));
            palletTypesViewer.setSelection(new StructuredSelection(
                palletFoundWithProductBarcode.getContainerType()));
            appendLog(MessageFormat.format(
                "Product barcode {0} already exists at position {1} of site {2} with type {3}.",
                currentMultipleContainer.getProductBarcode(),
                palletFoundWithProductBarcode.getLabel(),
                currentMultipleContainer.getSite().getNameShort(),
                palletFoundWithProductBarcode.getContainerType().getName()));
            // can't modify the position if exists already

            palletLabelText.setEnabled(false);
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Values validation"), ex);
            appendLog(NLS.bind("ERROR: {0}", ex.getMessage()));
            return false;
        }
        return true;
    }

    @Override
    protected void defaultInitialisation() {
        super.defaultInitialisation();
        boolean use = !mode.isSingleMode() && usingFlatbedScanner;
        useScannerButton.setSelection(use);
        setUseScanner(use);
    }

    @SuppressWarnings("nls")
    @Override
    protected void setUseScanner(boolean use) {
        usingFlatbedScanner = use;
        widgetCreator.showWidget(scanButton, use);
        widgetCreator.showWidget(palletproductBarcodeLabel, use);
        widgetCreator.showWidget(palletproductBarcodeText, use);
        widgetCreator.setBinding(PRODUCT_BARCODE_BINDING, use);
        if (palletTypesViewer != null) {
            palletTypesViewer.setInput(null);
            palletTypesViewer.getCombo().deselectAll();
        }
        if (use) {
            palletproductBarcodeText.setText(StringUtil.EMPTY_STRING);
            currentMultipleContainer.setContainerType(null);
            setScanHasBeenLaunched(true);
            setScanValid(true);
        }
        try {
            setValues();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.setUseScanner(use);
        page.layout(true, true);
        log.debug("setUseScanner: use: " + use);
        checkPalletContainerTypes();
    }

    /**
     * assign multiple: list of container types
     */
    @SuppressWarnings("nls")
    private void createPalletTypesViewer(Composite parent) throws ApplicationException {
        initPalletContainerTypes();
        palletTypesViewer = widgetCreator.createComboViewer(
            parent,
            ContainerType.NAME.format(1).toString(),
            null,
            null,
            // TR: validation error message
            i18n.tr("A pallet type should be selected"),
            true,
            PALLET_TYPES_BINDING,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ContainerTypeWrapper ctype = (ContainerTypeWrapper) selectedObject;
                    updateGridDimensions(ctype);
                    currentMultipleContainer.setContainerType(ctype);
                    setContainerType(ctype.getWrappedObject());
                    palletTypesViewer.getCombo().setFocus();

                    Set<ContainerType> ctypes = new HashSet<ContainerType>(1);
                    ctypes.add(ctype.getWrappedObject());
                    if (!usingFlatbedScanner) {
                        displayPalletPositions();
                    }
                }
            },
            new BiobankLabelProvider());
    }

    @Override
    @SuppressWarnings("nls")
    protected void checkPalletContainerTypes() {
        if (!isSingleMode() && usingFlatbedScanner && isPalletContainerTypesInvalid()) {
            throw new IllegalStateException("no pallets defined at this site");
        }
    }

    @Override
    protected void enableFields(boolean enable) {
        super.enableFields(enable);
        multipleOptionsFields.setEnabled(enable);
    }

    @Override
    protected boolean fieldsValid() {
        if (mode.isSingleMode()) return true;

        IStructuredSelection selection = (IStructuredSelection) palletTypesViewer.getSelection();
        return (!usingFlatbedScanner || scanMultipleWithHandheldInput)
            && (!usingFlatbedScanner || productBarcodeValidator.validate(
                palletproductBarcodeText.getText()).equals(Status.OK_STATUS))
            && palletLabelValidator.validate(palletLabelText.getText()).equals(Status.OK_STATUS)
            && !selection.isEmpty();
    }

    @SuppressWarnings("nls")
    @Override
    protected void saveForm() throws Exception {
        if (mode.isSingleMode()) {
            saveSingleSpecimen();
        } else {
            saveMultipleSpecimens();
        }
        setFinished(false);
        SessionManager.log("save", null, "SpecimenAssign");
    }

    @SuppressWarnings("nls")
    private void saveMultipleSpecimens() throws Exception {
        if (!saveEvenIfMissing) return;

        try {
            Map<RowColPos, SpecimenCell> cells = getCells();
            List<SpecimenInfo> specInfos = new ArrayList<SpecimenAssignSaveAction.SpecimenInfo>();
            for (Entry<RowColPos, SpecimenCell> entry : cells.entrySet()) {
                RowColPos rcp = entry.getKey();
                SpecimenCell cell = entry.getValue();
                if ((cell != null) && ((cell.getStatus() == UICellStatus.NEW)
                    || (cell.getStatus() == UICellStatus.MOVED))) {
                    SpecimenWrapper specimen = cell.getSpecimen();
                    if (specimen != null) {
                        SpecimenInfo specInfo = new SpecimenInfo();
                        specInfo.specimenId = specimen.getId();
                        specInfo.position = rcp;
                        specInfos.add(specInfo);
                    }
                }
            }

            // need to update container's product barcode
            ContainerSaveAction csAction = new ContainerSaveAction();
            csAction.barcode = currentMultipleContainer.getProductBarcode();
            csAction.typeId = currentMultipleContainer.getContainerType().getId();
            csAction.activityStatus = currentMultipleContainer.getActivityStatus();
            csAction.siteId = currentMultipleContainer.getSite().getId();

            if (currentMultipleContainer.getId() != null) {
                csAction.containerId = currentMultipleContainer.getId();
            }

            if (currentMultipleContainer.getParentContainer() == null) {
                csAction.label = currentMultipleContainer.getLabel();
            } else {
                csAction.parentId = currentMultipleContainer.getParentContainer().getId();
                csAction.position = currentMultipleContainer.getPositionAsRowCol();
            }

            Integer containerId = SessionManager.getAppService().doAction(csAction).getId();

            res = SessionManager.getAppService().doAction(
                new SpecimenAssignSaveAction(containerId, specInfos));

            if (isNewMultipleContainer) {
                if (res.parentContainerId != null) {
                    appendLog(MessageFormat.format(
                        "ADDED: Pallet {0} of type {1} to position {2} of site {3}",
                        res.parentBarcode, res.parentTypeName,
                        res.parentLabel, res.siteName));
                } else {
                    throw new RuntimeException(
                        // TR: exception message
                        i18n.tr("problem with parent container creation"));
                }
            }
        } catch (Exception ex) {
            setScanHasBeenLaunched(false, true);
            throw ex;
        }
        StringBuffer sb = new StringBuffer("SPECIMENS ASSIGNED:\n");
        for (SpecimenResInfo sp : res.specimens) {
            String posStr = sp.position;
            if (posStr == null) {
                posStr = "none";
            }
            sb.append(MessageFormat.format(
                "ASSIGNED position {0} (site {1}) to specimen {2} - Type: {3} - Patient: {4} - Visit#: {5}\n",
                posStr, res.siteName, sp.inventoryId,
                sp.typeName, sp.patientPNumber, sp.visitNumber));
        }
        appendLog(sb.toString());
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                appendLog(MessageFormat.format(
                    "ASSIGNING: {0} specimens added to pallet {1} of site {2}",
                    res.specimens.size(), palletLabelText.getText(),
                    currentMultipleContainer.getSite().getNameShort()));
            }
        });
        setFinished(false);
    }

    @SuppressWarnings("nls")
    private void saveSingleSpecimen() throws Exception {
        SpecimenInfo specInfo = new SpecimenInfo();
        specInfo.specimenId = singleSpecimen.getId();
        specInfo.position = singleSpecimen.getPosition();
        SpecimenAssignResInfo res = SessionManager.getAppService().doAction(
            new SpecimenAssignSaveAction(singleSpecimen.getParentContainer().getId(),
                Arrays.asList(specInfo)));

        if (res.specimens.size() != 1) {
            // TR: exception message
            throw new Exception(i18n.tr("result problem"));
        }
        SpecimenResInfo spRes = res.specimens.get(0);
        appendLog(MessageFormat
            .format("ASSIGNED position {0} (site {1}) to specimen {2} - Type: {3} - Patient: {4} - Visit#: {5}",
                spRes.position, spRes.centerName, spRes.inventoryId,
                spRes.typeName, spRes.patientPNumber, spRes.visitNumber));
    }

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        super.setValues();
        parentContainers = null;
        // resultShownValue.setValue(Boolean.FALSE);
        // the 2 following lines are needed. The validator won't update if don't
        // do that (why ?)
        inventoryIdText.setText("**");
        inventoryIdText.setText(StringUtil.EMPTY_STRING);
        singleTypeText.setText(StringUtil.EMPTY_STRING);
        singleCollectionDateText.setText(StringUtil.EMPTY_STRING);
        oldSinglePositionText.setText(StringUtil.EMPTY_STRING);
        oldSinglePositionCheckText.setText(StringUtil.EMPTY_STRING);
        newSinglePositionText.setText(StringUtil.EMPTY_STRING);
        displayOldSingleFields(false);

        showOnlyPallet(true);
        form.layout(true, true);
        if (!mode.isSingleMode()) {
            palletproductBarcodeText.setFocus();
        }
        foundSpecNull.setValue(true);
        singleSpecimen.reset(); // reset internal values
        setDirty(false);
        if (mode.isSingleMode()) {
            BgcPlugin.focusControl(inventoryIdText);
        } else if (usingFlatbedScanner) {
            BgcPlugin.focusControl(palletproductBarcodeText);
        } else {
            BgcPlugin.focusControl(palletLabelText);
        }
    }

    @SuppressWarnings("nls")
    @Override
    public void reset(boolean resetAll) {
        super.reset(resetAll);
        String productBarcode = StringUtil.EMPTY_STRING;
        String label = StringUtil.EMPTY_STRING;
        ContainerTypeWrapper type = null;

        if (!resetAll) {
            // keep fields values
            productBarcode = palletproductBarcodeText.getText();
            label = palletLabelText.getText();
            type = currentMultipleContainer.getContainerType();
        } else {
            if (palletTypesViewer != null) {
                palletTypesViewer.setInput(null);
                palletTypesViewer.getCombo().deselectAll();
            }
            freezerWidget.setSelection(null);
            hotelWidget.setSelection(null);
            palletLabel.setText(i18n.tr("Pallet"));
            palletWidget.setCells(null);
        }
        setScanHasBeenLaunched(isSingleMode() || !usingFlatbedScanner);
        initPalletValues();

        palletproductBarcodeText.setText(productBarcode);
        productBarcodeValidator.validate(productBarcode);
        palletLabelText.setText(label);
        palletLabelValidator.validate(label);
        currentMultipleContainer.setContainerType(type);
        if (resetAll) {
            setDirty(false);
            useNewProductBarcode = false;
        }
        canSaveSingleSpecimen.setValue(!isSingleMode());
    }

    @SuppressWarnings("nls")
    @Override
    protected void setBindings(boolean isSingleMode) {
        widgetCreator.setBinding(INVENTORY_ID_BINDING, isSingleMode);
        widgetCreator.setBinding(OLD_SINGLE_POSITION_BINDING, isSingleMode);
        oldSinglePositionCheckText.setText("?");
        widgetCreator.setBinding(NEW_SINGLE_POSITION_BINDING, isSingleMode);
        widgetCreator.setBinding(PRODUCT_BARCODE_BINDING, !isSingleMode && usingFlatbedScanner);
        widgetCreator.setBinding(PALLET_TYPES_BINDING, !isSingleMode);
        super.setBindings(isSingleMode);
        setScanHasBeenLaunched(isSingleMode || !usingFlatbedScanner);
        log.debug("setBindings: isSingleMode" + isSingleMode);
        checkPalletContainerTypes();
        setCanLaunchScan(true);
    }

    @Override
    protected void showModeComposite(Mode mode) {
        boolean single = mode.isSingleMode();
        if (single) {
            setFirstControl(inventoryIdText);
        } else if (usingFlatbedScanner) {
            setFirstControl(palletproductBarcodeText);
        } else {
            setFirstControl(palletLabelText);
        }

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
    public boolean canDecodeTubesManually(SpecimenCell cell) {
        return fieldsValid();
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
    public void beforeProcessingThreadStart() {
        showOnlyPallet(false, false);
        currentMultipleContainer.setSite(SessionManager.getUser().getCurrentWorkingSite());
        currentMultipleContainer.setContainerType((ContainerTypeWrapper) ((IStructuredSelection)
            palletTypesViewer.getSelection()).getFirstElement());
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
            Capacity capacity;
            ContainerWrapper hotelContainer = currentMultipleContainer.getParentContainer();
            ContainerWrapper freezerContainer = hotelContainer.getParentContainer();

            if (freezerContainer != null) {
                freezerLabel.setText(freezerContainer.getFullInfoLabel());
                freezerWidget.setSelection(hotelContainer.getPositionAsRowCol());
                capacity = freezerContainer.getContainerType().getWrappedObject().getCapacity();
                freezerWidget.setStorageSize(capacity.getRowCapacity(), capacity.getColCapacity());
                freezerWidget.redraw();
            }

            hotelLabel.setText(hotelContainer.getFullInfoLabel());
            hotelWidget.setSelection(currentMultipleContainer.getPositionAsRowCol());
            capacity = hotelContainer.getContainerType().getWrappedObject().getCapacity();
            hotelWidget.setStorageSize(capacity.getRowCapacity(), capacity.getColCapacity());
            hotelWidget.redraw();

            palletLabel.setText(palletLabelText.getText());

            setContainerType(currentMultipleContainer.getContainerType().getWrappedObject());
            palletWidget.setCells(getCells());

            showOnlyPallet(false);

            widgetCreator.showWidget(freezerLabel, freezerContainer != null);
            widgetCreator.showWidget(freezerWidget, freezerContainer != null);
            page.layout(true, true);
            book.reflow(true);
        }
    }

    @Override
    protected void doBeforeSave() throws Exception {
        if (!mode.isSingleMode()) {
            saveEvenIfMissing = true;
            if (currentScanState == UICellStatus.MISSING) {
                @SuppressWarnings("nls")
                boolean save = BgcPlugin
                    .openConfirm(
                        // TR: dialog title
                        i18n.tr("Really save?"),
                        // TR: dialog message
                        i18n.tr("Specimens are missing from this pallet, are you sure you want to save? These specimens will still be registered at their current position."));
                if (!save) {
                    setDirty(true);
                    saveEvenIfMissing = false;
                }
            }
        }
    }

    @Override
    protected Mode initialisationMode() {
        return mode;
    }

    @Override
    protected Composite getFocusedComposite(boolean single) {
        if (single) return inventoryIdText;
        if (usingFlatbedScanner) return palletproductBarcodeText;
        return palletLabelText;
    }

    @Override
    protected boolean needPlate() {
        return usingFlatbedScanner;
    }

    @Override
    protected Action<ProcessResult> getCellProcessAction(Integer centerId,
        CellInfo cell, Locale locale) {
        return new SpecimenAssignProcessAction(getProcessData(), centerId,
            cell, locale);
    }

    @Override
    protected Action<ProcessResult> getPalletProcessAction(
        Integer centerId,
        Map<RowColPos, CellInfo> cells,
        Locale locale) {
        return new SpecimenAssignProcessAction(getProcessData(), centerId, cells, locale);
    }

    protected AssignProcessInfo getProcessData() {
        return new AssignProcessInfo(currentMultipleContainer.getWrappedObject());
    }

    @Override
    protected void updateAvailableSpecimenTypes() {
        // Do nothing
    }

    protected void updateGridDimensions(ContainerTypeWrapper ctype) {
        RowColPos plateDimensions = new RowColPos(ctype.getCapacity().getRowCapacity(),
            ctype.getCapacity().getColCapacity());

        currentGridDimensions = plateDimensions;
        recreateScanPalletWidget(ctype.getCapacity().getRowCapacity(),
            ctype.getCapacity().getColCapacity());
        page.layout(true, true);
        book.reflow(true);
    }

    @Override
    public void postProcessDecodeTubesManually(Set<SpecimenCell> palletCells) throws Exception {
        super.postProcessDecodeTubesManually(palletCells);
        scanMultipleWithHandheldInput = true;
    }
}
