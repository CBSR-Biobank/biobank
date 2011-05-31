package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileSettings;

public class ScanPalletWidget extends ContainerDisplayWidget {

    public ScanPalletWidget(Composite parent) {
        this(parent, null);
    }

    public ScanPalletWidget(Composite parent, List<UICellStatus> cellStatus) {
        super(parent, cellStatus);
        setContainerDisplay(new ScanPalletDisplay(this));
    }

    public boolean isEverythingTyped() {
        if (cells != null) {
            for (AbstractUICell cell : cells.values()) {
                PalletCell pCell = (PalletCell) cell;
                if (PalletCell.hasValue(pCell) && pCell.getType() == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void loadProfile(String profileName) {
        ProfileSettings profile = ProfileManager.instance().getProfile(
            profileName);
        ((ScanPalletDisplay) getContainerDisplay()).setProfile(profile);
        this.redraw();
    }

    @Override
    public void initDisplayFromType(boolean createDefaultContainer,
        Integer cellSize) {
        ScanPalletDisplay display = (ScanPalletDisplay) getContainerDisplay();
        if (containerType == null)
            display.setDefaultStorageSize();
        else
            display.setContainerType(containerType);
        display.setCellWidth(cellSize);
        display.setCellHeight(cellSize);
    }
}
