package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.DispatchCreateProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchCreateScanDialog extends AbstractScanDialog<DispatchWrapper> {
    private static final I18n i18n = I18nFactory.getI18n(DispatchCreateScanDialog.class);

    private static Logger log = LoggerFactory.getLogger(DispatchCreateScanDialog.class.getName());

    @SuppressWarnings("nls")
    private static final String TITLE_AREA_MESSAGE = i18n.tr(
        "Scan specimens to dispatch. If a pallet with a "
            + " position is scanned, the specimens scanned\n"
            + "will be compared to those that are supposed to be in the pallet.");

    private BgcBaseText palletproductBarcodeText;
    private NonEmptyStringValidator productBarcodeValidator;
    private String currentProductBarcode;
    private boolean isPalletWithPosition;
    private boolean specimensAdded = false;
    private ContainerWrapper currentPallet;
    private final List<ContainerWrapper> removedPallets = new ArrayList<ContainerWrapper>();

    public DispatchCreateScanDialog(
        Shell parentShell,
        DispatchWrapper currentShipment,
        CenterWrapper<?> site) {
        super(parentShell, currentShipment, site);
        setContainerType(null);
    }

    @Override
    protected String getTitleAreaMessage() {
        return TITLE_AREA_MESSAGE;
    }

    /**
     * add the product barcode field and radios
     */
    @SuppressWarnings("nls")
    @Override
    protected void createCustomDialogPreContents(final Composite parent) {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());

        // only sites have containers
        if (SessionManager.getUser().getCurrentWorkingCenter() instanceof SiteWrapper) {
            Button palletWithoutPositionRadio = new Button(parent, SWT.RADIO);
            palletWithoutPositionRadio.setText(i18n.tr("Pallet without previous position"));
            final Button palletWithPositionRadio = new Button(parent, SWT.RADIO);
            palletWithPositionRadio.setText(i18n.tr("Pallet with previous position"));

            palletWithPositionRadio.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    isPalletWithPosition = palletWithPositionRadio.getSelection();
                    showProductBarcodeField(palletWithPositionRadio.getSelection());
                }
            });

            productBarcodeValidator = new NonEmptyStringValidator(i18n.tr("Enter product barcode"));
            Label palletproductBarcodeLabel = widgetCreator.createLabel(
                parent, i18n.tr("Pallet product barcode"));
            palletproductBarcodeText = (BgcBaseText) createBoundWidget(
                parent,
                BgcBaseText.class,
                SWT.NONE,
                palletproductBarcodeLabel,
                new String[0],
                this,
                "currentProductBarcode",
                productBarcodeValidator);
            palletproductBarcodeText.addKeyListener(new EnterKeyToNextFieldListener());
            showProductBarcodeField(false);
            palletWithoutPositionRadio.setSelection(true);
        }
    }

    @SuppressWarnings("nls")
    private void showProductBarcodeField(boolean show) {
        resetScan();
        palletproductBarcodeText.setEnabled(show);
        if (show) {
            palletproductBarcodeText.setText(StringUtil.EMPTY_STRING);
        } else {
            palletproductBarcodeText.setText(i18n.tr("No previous position"));
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void decodeButtonSelected() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                currentPallet = null;
                if (isPalletWithPosition) {
                    if (currentCenter instanceof SiteWrapper) {
                        try {
                            SiteWrapper site = (SiteWrapper) currentCenter;
                            Container container = new Container();
                            container.setProductBarcode(currentProductBarcode);
                            List<Container> containers;
                            containers = SessionManager.getAppService().doAction(
                                new ContainerGetInfoAction(container, site.getWrappedObject()))
                                .getList();

                            if (containers.isEmpty()) {
                                BgcPlugin.openAsyncError(
                                    i18n.tr("Pallet error"),
                                    i18n.tr("Can''t find pallet with barcode \"{0}\".",
                                        currentProductBarcode));
                            } else if (containers.size() > 1) {
                                throw new IllegalStateException(
                                    "ContainerGetInfoAction returned more than one container for product barcode "
                                        + currentProductBarcode);
                            }

                            currentPallet = new ContainerWrapper(
                                SessionManager.getAppService(), containers.get(0));
                            setContainerType(currentPallet.getContainerType().getWrappedObject());
                        } catch (ApplicationException e) {
                            BgcPlugin.openError(
                                // TR: dialog title
                                i18n.tr("Values validation"), e);
                        }
                    }
                }
            }
        });

        super.decodeButtonSelected();

    }

    /**
     * Add validation of product barcode
     */
    @Override
    protected boolean fieldsValid() {
        return ((productBarcodeValidator == null)
        || productBarcodeValidator.validate(
            palletproductBarcodeText.getText()).equals(Status.OK_STATUS));
    }

    /**
     * check the pallet is actually found (if need one)
     */
    @Override
    protected boolean checkBeforeProcessing(CenterWrapper<?> center) throws Exception {
        specimensAdded = false;
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    protected Action<ProcessResult> getCellProcessAction(
        Integer centerId,
        CellInfo cell,
        Locale locale) {
        log.debug("getCellProcessAction");
        return new DispatchCreateProcessAction(getProcessData(), centerId, cell, locale);
    }

    @SuppressWarnings("nls")
    @Override
    protected Action<ProcessResult> getPalletProcessAction(
        Integer centerId,
        Map<RowColPos, CellInfo> cells,
        Locale locale) {
        log.debug("getPalletProcessAction");
        return new DispatchCreateProcessAction(getProcessData(), centerId, cells, locale);
    }

    protected ShipmentProcessInfo getProcessData() {
        return new ShipmentProcessInfo(
            (currentPallet == null) ? null : currentPallet.getWrappedObject(),
            currentShipment, false);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getProceedButtonlabel() {
        return i18n.tr("Add specimens");
    }

    @Override
    protected boolean canActivateProceedButton() {
        return !specimensAdded;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return specimensAdded;
    }

    @Override
    protected void doProceed() throws Exception {
        List<SpecimenWrapper> specimens = new ArrayList<SpecimenWrapper>();
        for (PalletWell cell : getCells().values()) {
            if (cell.getStatus() != UICellStatus.MISSING) {
                specimens.add(cell.getSpecimen());
                cell.setStatus(UICellStatus.IN_SHIPMENT_ADDED);
            }
        }
        currentShipment.addSpecimens(specimens, DispatchSpecimenState.NONE);
        if (currentPallet != null) {
            removedPallets.add(currentPallet);
        }
        redrawPallet();
        specimensAdded = true;
        setOkButtonEnabled(true);
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);
    }

    @Override
    protected void startNewPallet() {
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(true);
        super.startNewPallet();
    }

    @Override
    protected List<UICellStatus> getPalletCellStatus() {
        return UICellStatus.DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST;
    }

    public List<ContainerWrapper> getRemovedPallets() {
        return removedPallets;
    }

    public void setCurrentProductBarcode(String currentProductBarcode) {
        this.currentProductBarcode = currentProductBarcode;
    }

    public String getCurrentProductBarcode() {
        return currentProductBarcode;
    }
}
