package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.model.CellStatus;

public class DispatchCreateScanDialog extends AbstractDispatchScanDialog {

    private static final String TITLE = "Scanning aliquots";

    public DispatchCreateScanDialog(Shell parentShell,
        DispatchShipmentWrapper currentShipment) {
        super(parentShell, currentShipment);
        // FIXME: need to enter the product bar code or choose not to enter it ?
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("process scan result");
        // FIXME: need to check aliquots expected in the pallet are there.
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Scan aliquots to dispatch";
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected String getProceedButtonlabel() {
        return "Add aliquots";
    }

    @Override
    protected void doProceed() {
        // TODO Auto-generated method stub
        System.out.println("Add aliquots");
        // FIXME: Aliquots shoud be added to the shipment
    }

    @Override
    protected List<CellStatus> getPalletCellStatus() {
        return CellStatus.DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST;
    }

}
