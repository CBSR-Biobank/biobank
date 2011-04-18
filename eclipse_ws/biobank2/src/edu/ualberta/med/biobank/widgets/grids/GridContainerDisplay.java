package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.ContainerCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;

public class GridContainerDisplay extends AbstractGridDisplay {

    private static final int HEIGHT_TWO_LINES = 40;

    /**
     * Default status when cell doesn't have any status
     */
    private UICellStatus defaultStatus = UICellStatus.NOT_INITIALIZED;

    @Override
    protected void paintGrid(PaintEvent e, ContainerDisplayWidget displayWidget) {
        super.paintGrid(e, displayWidget);
        if (legendStatus != null) {
            legendWidth = gridWidth / legendStatus.size();
        }
        if (hasLegend) {
            for (int i = 0; i < legendStatus.size(); i++) {
                UICellStatus status = legendStatus.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    @Override
    protected Color getDefaultBackgroundColor(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (displayWidget.getCells() != null) {
            ContainerCell cell = (ContainerCell) displayWidget.getCells().get(
                new RowColPos(indexRow, indexCol));
            if (cell == null) {
                cell = new ContainerCell();
            }
            UICellStatus status = cell.getStatus();
            if (status == null)
                status = defaultStatus;
            return status.getColor();
        }
        return super.getDefaultBackgroundColor(e, displayWidget, rectangle,
            indexRow, indexCol);
    }

    @Override
    protected String getDefaultTextForBox(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        String text = super.getDefaultTextForBox(cells, indexRow, indexCol);
        if (text.isEmpty()) {
            return "";
        }

        if (getCellHeight() <= HEIGHT_TWO_LINES) {
            return text + " " + getContainerTypeText(cells, indexRow, indexCol);
        }
        return text;
    }

    @Override
    protected String getBottomTextForBox(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        if (getCellHeight() > HEIGHT_TWO_LINES) {
            return getContainerTypeText(cells, indexRow, indexCol);
        }
        return "";
    }

    protected String getContainerTypeText(
        Map<RowColPos, ? extends AbstractUICell> cells, int indexRow,
        int indexCol) {
        String sname = "";
        if (cells != null) {
            ContainerCell cell = (ContainerCell) cells.get(new RowColPos(
                indexRow, indexCol));
            if ((cell != null)
                && (cell.getContainer() != null)
                && (cell.getContainer().getContainerType() != null)
                && (cell.getContainer().getContainerType().getNameShort() != null))
                sname = "("
                    + cell.getContainer().getContainerType().getNameShort()
                    + ")";
        }
        return sname;
    }

    // public void setLegend(List<CellStatus> legendStatus) {
    // hasLegend = true;
    // this.legendStatus = legendStatus;
    // }

    public void setDefaultStatus(UICellStatus status) {
        this.defaultStatus = status;
    }

    @Override
    public RowColPos getPositionAtCoordinates(int x, int y) {
        int col = x / getCellWidth();
        int row = y / getCellHeight();
        if (col >= 0 && col < getCols() && row >= 0 && row < getRows()) {
            return new RowColPos(row, col);
        }
        return null;
    }

    @Override
    public Point getSizeToApply() {
        return this.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

}
