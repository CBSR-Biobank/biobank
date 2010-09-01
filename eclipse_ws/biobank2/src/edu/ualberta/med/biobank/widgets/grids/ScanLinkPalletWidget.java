package edu.ualberta.med.biobank.widgets.grids;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.preferences.profiles.ProfileManager;
import edu.ualberta.med.scannerconfig.preferences.profiles.TriIntC;

/**
 * Widget to draw a pallet for scan link screen. Can do selections inside the
 * pallet to assign a type
 */
public class ScanLinkPalletWidget extends ScanPalletWidget {

    public ScanLinkPalletWidget(Composite client) {
        super(client);
    }

    // @Override
    // public void initLegend() {
    // hasLegend = true;
    // statusAvailable = new ArrayList<AliquotCellStatus>();
    // statusAvailable.add(AliquotCellStatus.EMPTY);
    // statusAvailable.add(AliquotCellStatus.NO_TYPE);
    // statusAvailable.add(AliquotCellStatus.TYPE);
    // statusAvailable.add(AliquotCellStatus.ERROR);
    // legendWidth = PALLET_WIDTH / statusAvailable.size();
    // }

    public boolean isEverythingTyped() {
        if (cells != null) {
            for (Cell cell : cells.values()) {
                PalletCell pCell = (PalletCell) cell;
                if (PalletCell.hasValue(pCell) && pCell.getType() == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void loadProfile(String profile) {
        TriIntC profileData = ProfileManager.instance().getProfile(profile);

        if (cells != null) {
            int i = 0;
            for (Cell cell : cells.values()) {
                PalletCell pCell = (PalletCell) cell;
                if (profileData.isSetBit(i))
                    pCell.setStatus(AliquotCellStatus.MISSING);
                i++;
            }

            // TODO make cells change color
        }

    }
    //
    // @Override
    // protected String getBottomTextForBox(int indexRow, int indexCol) {
    // return null;
    // }
}
