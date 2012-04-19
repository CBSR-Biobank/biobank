package edu.ualberta.med.biobank.dialogs;

import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;

public class ScanOneTubeDialog extends BgcBaseDialog {

    private String scannedValue;
    private BgcBaseText valueText;
    private final RowColPos position;
    private final Map<RowColPos, PalletCell> cells;

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

        widgetCreator.createLabel(area,
            "Barcode");
        valueText = widgetCreator.createText(area, SWT.NONE, null, null);
    }

    @Override
    protected String getTitleAreaMessage() {
        return NLS.bind("Scan the missing tube for position {0}",
            ContainerLabelingSchemeWrapper.rowColToSbs(position));
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Pallet tube scan";
    }

    @Override
    protected String getDialogShellTitle() {
        return "Pallet tube scan";
    }

    @Override
    protected void okPressed() {
        this.scannedValue = valueText.getText();
        for (PalletCell otherCell : cells.values()) {
            if (otherCell.getValue() != null
                && otherCell.getValue().equals(scannedValue)) {
                BgcPlugin.openAsyncError(
                    "Tube Scan Error", NLS.bind(
                        "The value entered already exists in position {0}",
                        ContainerLabelingSchemeWrapper
                            .rowColToSbs(new RowColPos(otherCell.getRow(),
                                otherCell.getCol()))));
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
