package edu.ualberta.med.biobank.widgets.grids;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.RowColPos;

//    public static final int SAMPLE_WIDTH = 50;
//
//    /**
//     * Pallets are always 8*12 = fixed size
//     */
//    public static final int PALLET_WIDTH = SAMPLE_WIDTH * ScanCell.COL_MAX;
//    public static final int PALLET_HEIGHT = SAMPLE_WIDTH * ScanCell.ROW_MAX;
//
//    protected List<AliquotCellStatus> statusAvailable;

public class ScanPalletWidget extends ContainerDisplayWidget {

    public ScanPalletWidget(Composite parent) {
        this(parent, true);
    }

    public ScanPalletWidget(Composite parent, boolean hasLegend) {
        super(parent);
        containerDisplay = new ScanPalletDisplay(this, hasLegend);
    }

    public RowColPos getPositionAtCoordinates(int x, int y) {
        return ((ScanPalletDisplay) containerDisplay).getPositionAtCoordinates(
            x, y);
    }

}
