package edu.ualberta.med.biobank.dialogs;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.helpers.ScanAssignHelper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ScanAssignDialog extends ScanLinkDialog
    implements ModifyListener, FocusListener {

    private static final I18n i18n = I18nFactory.getI18n(ScanAssignDialog.class);

    private static Logger log = LoggerFactory.getLogger(ScanAssignDialog.class);

    @SuppressWarnings("nls")
    private static final String SCAN_ASSIGN_DIALOG_SETTINGS =
        ScanAssignDialog.class.getSimpleName() + "_SETTINGS";

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Scan assign");

    private final org.apache.log4j.Logger activityLogger;

    private BgcBaseText palletBarcodeText;

    private BgcBaseText palletLabelText;

    private final IObservableValue palletBarcode = new WritableValue(StringUtil.EMPTY_STRING, String.class);

    private final IObservableValue palletLabel = new WritableValue(StringUtil.EMPTY_STRING, String.class);

    private NonEmptyStringValidator palletBarcodeValidator;

    private NonEmptyStringValidator palletLabelValidator;

    private boolean palletBarcodeTextModified = false;

    private boolean palletLabelTextModified = false;

    private boolean checkingPalletLabel = false;

    private boolean isNewPalletContainer;

    private ContainerWrapper palletContainer;

    private final int validPalletRows;

    private final int validPalletCols;

    private final String defaultPalletBarcode;

    private final String defaultPalletLabel;

    private final ContainerTypeWrapper defaultPalletContainerType;

    private final IObservableValue palletLabelValidObservable =
        new WritableValue(Boolean.TRUE, Boolean.class);

    private ComboViewer palletTypesViewer;

    @SuppressWarnings("nls")
    private static final String PALLET_TYPES_BINDING = "palletType-binding";

    protected final Set<ContainerType> palletContainerTypes = new HashSet<ContainerType>();

    private String userPalletBarcode;

    private String userPalletLabel;

    protected ContainerTypeWrapper selectedPalletContainerType;

    /**
     * 
     * 
     * @param parentShell
     * @param rows
     * @param cols
     * @param palletBarcode
     * @param palletLabel
     * @param activityLogger
     */
    @SuppressWarnings("nls")
    public ScanAssignDialog(
        Shell parentShell,
        int rows,
        int cols,
        String palletBarcode,
        String palletLabel,
        ContainerTypeWrapper palletContainerType,
        org.apache.log4j.Logger activityLogger) {
        super(parentShell, activityLogger);
        this.validPalletRows = rows;
        this.validPalletCols = cols;
        this.defaultPalletBarcode =
            (palletBarcode == null) ? StringUtil.EMPTY_STRING : palletBarcode;
        this.defaultPalletLabel =
            (palletLabel == null) ? StringUtil.EMPTY_STRING : palletLabel;
        this.defaultPalletContainerType = palletContainerType;
        this.activityLogger = activityLogger;
        this.palletContainer = new ContainerWrapper(SessionManager.getAppService());

        widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            palletLabelValidObservable,
            i18n.tr("Pallet label is invalid"),
            IStatus.ERROR);
    }

    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();
        IDialogSettings section = settings.getSection(SCAN_ASSIGN_DIALOG_SETTINGS);
        if (section == null) {
            section = settings.addNewSection(SCAN_ASSIGN_DIALOG_SETTINGS);
        }
        return section;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createControlWidgets(Composite contents) {
        super.createControlWidgets(contents);

        palletBarcodeValidator = new NonEmptyStringValidator(
            // TR: validation error message
            i18n.tr("Enter the pallet's barcode"));
        palletBarcodeText = (BgcBaseText) widgetCreator.createBoundWidgetWithLabel(
            contents,
            BgcBaseText.class,
            SWT.NONE,
            // TR: label
            i18n.tr("Pallet product barcode"),
            new String[0],
            palletBarcode,
            palletBarcodeValidator);

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.horizontalSpan = 2;

        palletBarcodeText.setLayoutData(gridData);

        palletLabelValidator = new NonEmptyStringValidator(
            // TR: validation error message
            i18n.tr("Enter the pallet's label"));
        palletLabelText = (BgcBaseText) widgetCreator.createBoundWidgetWithLabel(
            contents,
            BgcBaseText.class,
            SWT.NONE,
            // TR: label
            i18n.tr("Pallet label"),
            new String[0],
            palletLabel,
            palletLabelValidator);

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.horizontalSpan = 2;
        palletLabelText.setLayoutData(gridData);

        palletTypesViewer = widgetCreator.createComboViewer(
            contents,
            // TR: dialog field label
            i18n.tr("Pallet container type"),
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
                    palletContainer.setContainerType(ctype);
                    palletTypesViewer.getCombo().setFocus();

                    Set<ContainerType> ctypes = new HashSet<ContainerType>(1);
                    ctypes.add(ctype.getWrappedObject());
                }
            },
            new BiobankLabelProvider());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.horizontalSpan = 2;
        palletTypesViewer.getCombo().setLayoutData(gridData);

        palletBarcodeText.addModifyListener(this);
        palletBarcodeText.addFocusListener(this);
        palletLabelText.addModifyListener(this);
        palletLabelText.addFocusListener(this);

        if (!defaultPalletBarcode.isEmpty()) {
            palletBarcodeText.setText(defaultPalletBarcode);
            palletBarcodeText.setEnabled(false);
        }

        if (!defaultPalletLabel.isEmpty()) {
            palletLabelText.setText(defaultPalletLabel);
            palletLabelText.setEnabled(false);
        }

        if (defaultPalletContainerType != null) {
            palletTypesViewer.setInput(Arrays.asList(defaultPalletContainerType));
            palletTypesViewer.setSelection(new StructuredSelection(
                defaultPalletContainerType));
            palletTypesViewer.getCombo().setEnabled(false);
        }
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (palletBarcodeText.isEventSource(e)) {
            palletBarcodeTextModified();
        } else if (palletLabelText.isEventSource(e)) {
            palletLabelTextModified();
        }
    }

    @SuppressWarnings("nls")
    private void palletBarcodeTextModified() {
        log.info("palletBarcodeTextModified: palletBarcodeTextModified: {}, checkingPalletLabel: {}",
            palletBarcodeTextModified, checkingPalletLabel);
        if (!checkingPalletLabel) {
            palletBarcodeTextModified = true;
            palletTypesViewer.setInput(null);
            palletContainer.setContainerType(null);
            palletLabelText.setEnabled(true);
            palletLabelText.setText(StringUtil.EMPTY_STRING);
        }
    }

    @SuppressWarnings("nls")
    private void palletLabelTextModified() {
        log.info("palletLabelTextModified: palletBarcodeTextModified: {}, checkingPalletLabel: {}",
            palletLabelTextModified, checkingPalletLabel);
        palletLabelTextModified = true;
        palletTypesViewer.setInput(null);
        palletContainer.setContainerType(null);
    }

    @Override
    public void focusGained(FocusEvent e) {
        // do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (palletBarcodeText.isEventSource(e)) {
            palletBarcodeTextFocusLost();
        } else if (palletLabelText.isEventSource(e)) {
            palletLabelTextFocusLost();
        }
    }

    @SuppressWarnings("nls")
    private void palletBarcodeTextFocusLost() {
        log.info("palletBarcodeTextFocusLost: palletBarcodeTextModified: {}, checkingPalletLabel: {}",
            palletBarcodeTextModified, checkingPalletLabel);
        if (palletBarcodeTextModified) {
            String value = (String) palletBarcode.getValue();
            palletContainer.setProductBarcode(value);
            if (!value.isEmpty()) {
                boolean ok = checkPalletBarcode();
                if (!ok) {
                    BgcPlugin.focusControl(palletBarcodeText);
                }
            }
        }
        palletBarcodeTextModified = false;
    }

    @SuppressWarnings("nls")
    protected boolean checkPalletBarcode() {
        try {
            Container qryContainer = new Container();
            qryContainer.setProductBarcode((String) palletBarcode.getValue());

            List<Container> containers = SessionManager.getAppService().doAction(
                new ContainerGetInfoAction(qryContainer,
                    SessionManager.getUser().getCurrentWorkingSite().getWrappedObject())
                ).getList();

            if (containers.size() > 1) {
                throw new IllegalStateException("multiple containers found with product barcode:"
                    + qryContainer.getProductBarcode());
            } else if (containers.isEmpty()) {
                isNewPalletContainer = true;
                return true;
            }

            palletContainer = new ContainerWrapper(SessionManager.getAppService(), containers.get(0));
            isNewPalletContainer = false;
            Capacity capacity = palletContainer.getContainerType().getCapacity();

            if ((capacity.getRowCapacity() != validPalletRows)
                || (capacity.getColCapacity() != validPalletCols)) {

                // TR: dialog message
                String msg = i18n.tr(
                    "The dimensions for the container are invalid for the pallet that was scanned. "
                        + "Container with product barcode \"{0}\" has {1} rows and {2} columns.",
                    qryContainer.getProductBarcode(),
                    capacity.getRowCapacity(),
                    capacity.getColCapacity());

                BgcPlugin.openError(
                    // TR: dialog title
                    i18n.tr("Invalid container"),
                    msg);
                activityLogger.trace(NLS.bind("ERROR: {0}", msg));
                setPalletLabelValid(false);
                return false;
            }

            if (!ScanAssignHelper.isContainerValid(
                palletContainer, palletLabelText.getText())) {
                return false;
            }

            ContainerTypeWrapper containerType = palletContainer.getContainerType();

            // can't modify the position if exists already
            palletLabelText.setText(palletContainer.getLabel());
            palletLabelText.setEnabled(false);

            palletTypesViewer.getCombo().setEnabled(false);
            palletTypesViewer.setInput(Arrays.asList(containerType));
            palletTypesViewer.setSelection(new StructuredSelection(containerType));

            activityLogger.trace(MessageFormat.format(
                "Product barcode {0} already exists at position {1} of site {2} with type {3}.",
                palletContainer.getProductBarcode(),
                palletContainer.getLabel(),
                palletContainer.getSite().getNameShort(),
                containerType.getName()));
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Values validation"), ex);
            activityLogger.trace(NLS.bind("ERROR: {0}", ex.getMessage()));
            return false;
        }
        return true;
    }

    @SuppressWarnings("nls")
    private void palletLabelTextFocusLost() {
        log.info("palletLabelTextFocusLost: palletLabelTextModified: {}, checkingPalletLabel: {}",
            palletLabelTextModified, checkingPalletLabel);
        log.info("palletLabelTextFocusLost: palletContainer: {}", palletContainer);

        final String label = (String) palletLabel.getValue();

        if (palletLabelText.isEnabled() && palletLabelTextModified && !label.isEmpty()) {
            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                    checkingPalletLabel = true;
                    ContainerWrapper container =
                        ScanAssignHelper.getOrCreateContainerByLabel(label, palletContainer);

                    if (container == null) {
                        activityLogger.trace(NLS.bind("ERROR: Could not get container with label {0}", label));
                        setPalletLabelValid(false);
                        return;
                    }

                    palletContainer = container;
                    boolean ok = checkAndUpdateContainer(container, label);

                    palletContainer.setProductBarcode((String) palletBarcode.getValue());
                    palletContainer.setLabel(label);

                    if (!ok) {
                        BgcPlugin.focusControl(palletLabelText);
                    } else {
                        BgcPlugin.focusControl(palletTypesViewer.getCombo());
                    }
                    setPalletLabelValid(ok);
                    palletLabelTextModified = false;
                    checkingPalletLabel = false;
                }
            });
        }
        palletLabelTextModified = false;
    }

    private void setPalletLabelValid(final boolean labelValid) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                palletLabelValidObservable.setValue(labelValid);
            }
        });
    }

    @SuppressWarnings("nls")
    private boolean checkAndUpdateContainer(ContainerWrapper container, String palletLabel) {
        try {
            ContainerTypeWrapper typeSelection = null;
            List<ContainerTypeWrapper> possibleTypes = ScanAssignHelper.getContainerTypes(
                container, validPalletRows, validPalletCols);

            ContainerTypeWrapper currentCtype = container.getContainerType();
            if (possibleTypes.size() == 1) {
                typeSelection = possibleTypes.get(0);
            } else if (currentCtype != null) {
                typeSelection = container.getContainerType();
            }

            if (!checkValidContainer(container)) return false;

            String newBarcode = container.getProductBarcode();

            if ((newBarcode != null) && !newBarcode.isEmpty()
                && (palletContainer.getProductBarcode() != null)) {
                if (!newBarcode.equals(palletContainer.getProductBarcode())) {
                    ScanAssignHelper.containerPositionError(
                        activityLogger,
                        container,
                        newBarcode,
                        palletLabel);
                    return false;
                }
            }

            palletContainer.initObjectWith(container);
            if (!palletContainer.isNew()) {
                palletContainer.reset();
            }

            String msg = ScanAssignHelper.containerProductBarcodeUpdateLogMessage(
                palletContainer, newBarcode, palletLabel);
            if (!msg.isEmpty()) {
                activityLogger.trace(msg);
            }

            if ((newBarcode != null) && !newBarcode.isEmpty()) {
                palletBarcodeText.setText(newBarcode);
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
            activityLogger.trace(NLS.bind("ERROR: {0}", ex.getMessage()));
            return false;
        }

        return true;
    }

    @SuppressWarnings("nls")
    private boolean checkValidContainer(ContainerWrapper pallet) {
        switch (ScanAssignHelper.checkExistingContainerValid(pallet)) {
        case VALID:
        case IS_NEW:
            return true;

        case DOES_NOT_HOLD_SPECIMENS:
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Container error"),
                // TR: dialog message
                i18n.tr("The selected container can not hold specimens"));
            return false;

        default:
            throw new IllegalArgumentException("container is invalid");
        }
    }

    @Override
    protected void okPressed() {
        userPalletBarcode = palletBarcodeText.getText();
        userPalletLabel = palletLabelText.getText();
        selectedPalletContainerType = (ContainerTypeWrapper) ((IStructuredSelection)
            palletTypesViewer.getSelection()).getFirstElement();
        super.okPressed();
    }

    public String getPalletBarcode() {
        return userPalletBarcode;
    }

    public String getPalletLabel() {
        return userPalletLabel;
    }

    public ContainerWrapper getPalletContainer() {
        return palletContainer;
    }

    public ContainerTypeWrapper getPalletContainerType() {
        return selectedPalletContainerType;
    }

    public boolean isNewContainer() {
        return isNewPalletContainer;
    }
}
