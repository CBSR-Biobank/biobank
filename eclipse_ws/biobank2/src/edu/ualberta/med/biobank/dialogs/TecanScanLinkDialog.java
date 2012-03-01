package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class TecanScanLinkDialog extends BgcBaseDialog {

    private static final String TITLE = "Tecan Scan Link File";
    private String shipment;

    public TecanScanLinkDialog(Shell parentShell, String lshipment) {
        super(parentShell);
        shipment = lshipment;
        // this.shipment = shipment;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Is the following the file you wish to Process, Press Ok to continue";
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
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        BgcBaseText fileToUpload = widgetCreator.createReadOnlyField(contents,
            SWT.NONE, "CSV File", true);
        fileToUpload.setText(shipment);

    }
}
