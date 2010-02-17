package edu.ualberta.med.biobank.widgets.grids;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.SampleCellStatus;

/**
 * Widget to draw a pallet for scan link screen. Can do selections inside the
 * pallet to assign a type
 */
public class ScanLinkPalletWidget extends ScanPalletWidget {

    public ScanLinkPalletWidget(Composite parent) {
        super(parent);
    }

    @Override
    public void initLegend() {
        hasLegend = true;
        statusAvailable = new ArrayList<SampleCellStatus>();
        statusAvailable.add(SampleCellStatus.EMPTY);
        statusAvailable.add(SampleCellStatus.NO_TYPE);
        statusAvailable.add(SampleCellStatus.TYPE);
        statusAvailable.add(SampleCellStatus.ERROR);
        legendWidth = PALLET_WIDTH / statusAvailable.size();
    }

    public boolean isEverythingTyped() {
        for (Cell cell : cells.values()) {
            PalletCell pCell = (PalletCell) cell;
            if (PalletCell.hasValue(pCell) && pCell.getType() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getBottomTextForBox(int indexRow, int indexCol) {
        return null;
    }
}
