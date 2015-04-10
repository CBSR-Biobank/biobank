package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public class PalletWidget extends ContainerDisplayWidget {

    private static final IContainerDisplayWidget TOOLTIP_CALLBACK =
        new IContainerDisplayWidget() {

            @SuppressWarnings("nls")
            @Override
            public String getTooltipText(AbstractUIWell cell) {
                SpecimenCell palletCell = (SpecimenCell) cell;
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

    /**
     * Use this constructor to provide your own tooltip callback and customize what is displayed
     * when the user hovers over a cell in the pallet visualization.
     *
     * Uses the default tooltip message to display when the user hovers over a cell in the pallet
     * visualization.
     *
     * @param parent The parent widget
     *
     * @param cellStatus the cell status to give to each cell on startup. See {@link UICellStatus}.
     *
     * @param rows the number of rows in the pallet
     *
     * @param cols the number of columns in the pallet
     */
    public PalletWidget(Composite parent, List<UICellStatus> cellStatus, int rows, int cols) {
        super(
            parent,
            TOOLTIP_CALLBACK,
            PalletWidget.class.getSimpleName(),
            new PalletDisplay(rows, cols),
            cellStatus);
    }

    /**
     * Use this constructor to provide your own tooltip callback and customize what is displayed
     * when the user hovers over a cell in the pallet visualization.
     *
     * @param parent The parent widget
     *
     * @param cellStatus the cell status to give to each cell on startup. See {@link UICellStatus}.
     *
     * @param rows the number of rows in the pallet
     *
     * @param cols the number of columns in the pallet
     *
     * @param tooltipCallback the method that is called to get the contents of what to display in
     *            the tooltip when the user hovers over a cell.
     */
    public PalletWidget(
        Composite parent,
        List<UICellStatus> cellStatus,
        int rows,
        int cols,
        IContainerDisplayWidget tooltipCallback) {
        super(
            parent,
            tooltipCallback,
            PalletWidget.class.getSimpleName(),
            new PalletDisplay(rows, cols),
            cellStatus);
    }

    @SuppressWarnings("nls")
    public boolean isEverythingTyped() {
        if (cells == null) {
            throw new IllegalStateException("cells is null");
        }

        if (cells.isEmpty()) return false;

        for (AbstractUIWell cell : cells.values()) {
            SpecimenCell pCell = (SpecimenCell) cell;
            if (SpecimenCell.hasValue(pCell) && pCell.getType() == null) {
                return false;
            }
        }
        return true;
    }
}
