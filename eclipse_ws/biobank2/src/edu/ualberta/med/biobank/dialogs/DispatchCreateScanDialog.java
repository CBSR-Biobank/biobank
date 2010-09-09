package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;

public class DispatchCreateScanDialog extends AbstractDispatchScanDialog {

    private static final String TITLE = "Scanning aliquots";

    public DispatchCreateScanDialog(Shell parentShell,
        DispatchShipmentWrapper currentShipment) {
        super(parentShell, currentShipment);
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("process scan result");

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
    }

}
