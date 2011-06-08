package edu.ualberta.med.biobank.dialogs;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;

public class ScanOneTubeDialog extends BgcBaseDialog {

    private static final String PALLET_TUBE_SCAN = "Pallet tube scan";
    private String scannedValue;
    private BgcBaseText valueText;
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
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        widgetCreator.createLabel(area, "Barcode");
        valueText = widgetCreator.createText(area, SWT.NONE, null, null);
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Scan the missing tube for position "
            + ContainerLabelingSchemeWrapper.rowColToSbs(position);
    }

    @Override
    protected String getTitleAreaTitle() {
        return PALLET_TUBE_SCAN;
    }

    @Override
    protected String getDialogShellTitle() {
        return PALLET_TUBE_SCAN;
    }

    @Override
    protected void okPressed() {
        this.scannedValue = valueText.getText();
        for (PalletCell otherCell : cells.values()) {
            if (otherCell.getValue() != null
                && otherCell.getValue().equals(scannedValue)) {
                BgcPlugin.openAsyncError(
                    "Tube Scan Error",
                    "The value entered already exists in position "
                        + ContainerLabelingSchemeWrapper
                            .rowColToSbs(new RowColPos(otherCell.getRow(),
                                otherCell.getCol())));
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
