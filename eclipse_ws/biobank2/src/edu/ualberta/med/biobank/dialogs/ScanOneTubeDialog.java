package edu.ualberta.med.biobank.dialogs;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.PalletCell;

public class ScanOneTubeDialog extends BiobankDialog {

    private String scannedValue;
    private Text valueText;
    private RowColPos position;
    private Map<RowColPos, PalletCell> cells;

    public ScanOneTubeDialog(Shell parentShell,
        Map<RowColPos, PalletCell> cells, RowColPos rcp) {
        super(parentShell);
        this.cells = cells;
        this.position = rcp;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        setTitle("Pallet tube scan");
        setMessage("Scan the missing tube for position "
            + LabelingScheme.rowColToSbs(position));
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        widgetCreator.createLabel(area, "Barcode");
        valueText = widgetCreator.createText(area, SWT.NONE, null, null);
    }

    @Override
    protected void okPressed() {
        this.scannedValue = valueText.getText();
        for (PalletCell otherCell : cells.values()) {
            if (otherCell.getValue() != null
                && otherCell.getValue().equals(scannedValue)) {
                BioBankPlugin.openAsyncError("Tube Scan Error",
                    "The value entered already exists in position "
                        + LabelingScheme.rowColToSbs(new RowColPos(otherCell
                            .getRow(), otherCell.getCol())));
                valueText.setFocus();
                valueText.setSelection(0, scannedValue.length());
                return;
            }
        }
        super.okPressed();
    }

    public String getScannedValue() {
        return scannedValue;
    }
}
