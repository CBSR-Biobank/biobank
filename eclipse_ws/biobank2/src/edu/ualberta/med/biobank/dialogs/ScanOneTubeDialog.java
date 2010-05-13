package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ScanOneTubeDialog extends BiobankDialog {

    private String scannedValue;
    private Text valueText;

    public ScanOneTubeDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        setTitle("Pallet tube scan");
        setMessage("Scan the missing tube");
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        widgetCreator.createLabel(area, "Value scanned");
        valueText = widgetCreator.createText(area, SWT.NONE, null, null);
    }

    @Override
    protected void okPressed() {
        this.scannedValue = valueText.getText();
        super.okPressed();
    }

    public String getScannedValue() {
        return scannedValue;
    }
}
