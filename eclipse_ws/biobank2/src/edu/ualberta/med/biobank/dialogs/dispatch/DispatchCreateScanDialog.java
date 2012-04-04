package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.DispatchCreateProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchCreateScanDialog extends
    AbstractScanDialog<DispatchWrapper> {

    private BgcBaseText palletproductBarcodeText;
    private NonEmptyStringValidator productBarcodeValidator;
    private String currentProductBarcode;
    private boolean isPalletWithPosition;
    private boolean specimensAdded = false;
    private ContainerWrapper currentPallet;
    private List<ContainerWrapper> removedPallets =
        new ArrayList<ContainerWrapper>();

    public DispatchCreateScanDialog(Shell parentShell,
        DispatchWrapper currentShipment, CenterWrapper<?> site) {
        super(parentShell, currentShipment, site);
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Scan specimens to dispatch. If a pallet with previous position is scan, the specimens scanned\nwill be compared to those that are supposed to be in the pallet.";
    }

    /**
     * add the product barcode field and radios
     */
    @Override
    protected void createCustomDialogPreContents(final Composite parent) {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());

        // only sites have containers
        if (SessionManager.getUser().getCurrentWorkingCenter() instanceof SiteWrapper) {
            Button palletWithoutPositionRadio = new Button(parent, SWT.RADIO);
            palletWithoutPositionRadio
                .setText("Pallet without previous position");
            final Button palletWithPositionRadio =
                new Button(parent, SWT.RADIO);
            palletWithPositionRadio
                .setText("Pallet with previous position");

            palletWithPositionRadio
                .addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        isPalletWithPosition = palletWithPositionRadio
                            .getSelection();
                        showProductBarcodeField(palletWithPositionRadio
                            .getSelection());
                    }
                });

            productBarcodeValidator = new NonEmptyStringValidator(
                "Enter product barcode");
            Label palletproductBarcodeLabel = widgetCreator.createLabel(parent,
                "Pallet product barcode");
            palletproductBarcodeText =
                (BgcBaseText) createBoundWidget(parent,
                    BgcBaseText.class, SWT.NONE, palletproductBarcodeLabel,
                    new String[0], this,
                    "currentProductBarcode", productBarcodeValidator);
            palletproductBarcodeText
                .addKeyListener(new EnterKeyToNextFieldListener());
            showProductBarcodeField(false);
            palletWithoutPositionRadio.setSelection(true);
        }
    }

    private void showProductBarcodeField(boolean show) {
        resetScan();
        palletproductBarcodeText.setEnabled(show);
        if (show) {
            palletproductBarcodeText.setText("");
        } else {
            palletproductBarcodeText
                .setText("No previous position");
        }
    }

    /**
     * Add validation of product barcode
     */
    @Override
    protected boolean fieldsValid() {
        return super.fieldsValid()
            && (productBarcodeValidator == null || productBarcodeValidator
                .validate(palletproductBarcodeText.getText()).equals(
                    Status.OK_STATUS));
    }

    /**
     * check the pallet is actually found (if need one)
     */
    @Override
    protected boolean checkBeforeProcessing(CenterWrapper<?> center)
        throws Exception {
        specimensAdded = false;
        currentPallet = null;
        if (isPalletWithPosition) {
            if (center instanceof SiteWrapper)
                currentPallet = ContainerWrapper
                    .getContainerWithProductBarcodeInSite(
                        SessionManager.getAppService(), (SiteWrapper) center,
                        currentProductBarcode);
            if (currentPallet == null) {
                BgcPlugin
                    .openAsyncError(
                        "Pallet error",
                        NLS.bind(
                            "Can''t find pallet with barcode ''{0}''",
                            currentProductBarcode));
                return false;
            }
        }
        return true;
    }

    @Override
    protected Action<ProcessResult> getCellProcessAction(Integer centerId,
        CellInfo cell, Locale locale) {
        return new DispatchCreateProcessAction(getProcessData(), centerId,
            cell,
            locale);
    }

    @Override
    protected Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, boolean isRescanMode,
        Locale locale) {
        return new DispatchCreateProcessAction(getProcessData(), centerId,
            cells,
            isRescanMode, locale);
    }

    protected ShipmentProcessInfo getProcessData() {
        return new ShipmentProcessInfo(currentPallet == null ? null
            : currentPallet.getWrappedObject(),
            currentShipment, false);
    }

    @Override
    protected String getProceedButtonlabel() {
        return "Add specimens";
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
        for (PalletCell cell : getCells().values()) {
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

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        ContainerWrapper currentPallet = null;
        if (isPalletWithPosition)
            currentPallet = ContainerWrapper
                .getContainerWithProductBarcodeInSite(
                    SessionManager.getAppService(), (SiteWrapper) currentSite,
                    currentProductBarcode);
        Map<RowColPos, PalletCell> map = new HashMap<RowColPos, PalletCell>();
        if (currentPallet == null) {
            Map<RowColPos, PalletCell> cells = PalletCell
                .getRandomNonDispatchedSpecimens(
                    SessionManager.getAppService(), (currentShipment)
                        .getSenderCenter().getId());
            return cells;
        }
        for (SpecimenWrapper specimen : currentPallet.getSpecimens()
            .values()) {
            PalletCell cell = new PalletCell(new ScanCell(
                specimen.getPosition().getRow(), specimen.getPosition()
                    .getCol(),
                specimen.getInventoryId()));
            map.put(specimen.getPosition(), cell);
        }
        return map;
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
