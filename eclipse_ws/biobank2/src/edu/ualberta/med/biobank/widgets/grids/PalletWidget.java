package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public class PalletWidget extends ContainerDisplayWidget {

    public PalletWidget(Composite parent) {
        this(parent, null);
    }

    public PalletWidget(Composite parent, List<UICellStatus> cellStatus) {
        super(parent, PalletWidget.class.getSimpleName(), cellStatus);
        setContainerDisplay(new PalletDisplay(
            this,
            RowColPos.ROWS_DEFAULT,
            RowColPos.COLS_DEFAULT));
    }

    public PalletWidget(Composite parent, List<UICellStatus> cellStatus, int rows, int cols) {
        super(parent, PalletWidget.class.getSimpleName(), cellStatus);
        setContainerDisplay(new PalletDisplay(this, rows, cols));
    }

    public boolean isEverythingTyped() {
        if (cells == null) return false;

        for (AbstractUIWell cell : cells.values()) {
            PalletWell pCell = (PalletWell) cell;
            if (PalletWell.hasValue(pCell) && pCell.getType() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initDisplayFromType(boolean createDefaultContainer, Integer cellSize) {
        PalletDisplay display = (PalletDisplay) getContainerDisplay();
        if (containerType == null) {
            setContainerDisplay(new PalletDisplay(
                this,
                RowColPos.ROWS_DEFAULT,
                RowColPos.COLS_DEFAULT));
        } else {
            display.setContainerType(containerType);
        }
        display.setCellWidth(cellSize);
        display.setCellHeight(cellSize);
    }
}
