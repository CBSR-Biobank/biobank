package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public class PalletWidget extends ContainerDisplayWidget {

    private static final IContainerDisplayWidget tooltipCallback =
        new IContainerDisplayWidget() {

            @SuppressWarnings("nls")
            @Override
            public String getTooltipText(AbstractUIWell cell) {
                PalletWell palletCell = (PalletWell) cell;
                StringBuffer buf = new StringBuffer();
                String msg = palletCell.getValue();
                if (!msg.isEmpty()) {
                    buf.append(msg);
                    String information = palletCell.getInformation();
                    if ((information != null) && !information.isEmpty()) {
                        buf.append(": ");
                        buf.append(information);
                    }
                }
                return buf.toString();
            }
        };

    public PalletWidget(Composite parent) {
        this(parent, null);
    }

    public PalletWidget(Composite parent, List<UICellStatus> cellStatus) {
        super(parent, tooltipCallback, PalletWidget.class.getSimpleName(), cellStatus);
    }

    public PalletWidget(Composite parent, List<UICellStatus> cellStatus, int rows, int cols) {
        super(parent, tooltipCallback, PalletWidget.class.getSimpleName(), cellStatus);
        setContainerDisplay(new PalletDisplay(rows, cols));
    }

    @SuppressWarnings("nls")
    public boolean isEverythingTyped() {
        if (cells == null) {
            throw new IllegalStateException("cells is null");
        }

        if (cells.isEmpty()) return false;

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
            setContainerDisplay(new PalletDisplay(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT));
        } else {
            display.setContainerType(containerType);
        }
        display.setCellWidth(cellSize);
        display.setCellHeight(cellSize);
    }
}
