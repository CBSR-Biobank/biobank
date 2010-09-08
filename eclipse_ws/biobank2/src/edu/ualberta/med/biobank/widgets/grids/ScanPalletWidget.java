package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.preferences.profiles.ProfileManager;
import edu.ualberta.med.scannerconfig.preferences.profiles.TriIntC;

public class ScanPalletWidget extends ContainerDisplayWidget {

    public ScanPalletWidget(Composite parent) {
        this(parent, null);
    }

    public ScanPalletWidget(Composite parent, List<CellStatus> cellStatus) {
        super(parent, cellStatus);
        setContainerDisplay(new ScanPalletDisplay(this));
    }

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
        ((ScanPalletDisplay) getContainerDisplay()).setProfile(profileData);
        this.redraw();
    }

}
