package edu.ualberta.med.biobank.widgets.grids;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.util.RowColPos;

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
