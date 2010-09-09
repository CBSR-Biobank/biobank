package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.ContainerCell;

public class GridContainerDisplay extends AbstractGridDisplay {

    private static final int HEIGHT_TWO_LINES = 40;

    /**
     * Default status when cell doesn't have any status
     */
    private CellStatus defaultStatus = CellStatus.NOT_INITIALIZED;

    @Override
    protected void paintGrid(PaintEvent e, ContainerDisplayWidget displayWidget) {
        super.paintGrid(e, displayWidget);
        if (legendStatus != null) {
            legendWidth = gridWidth / legendStatus.size();
        }
        if (hasLegend) {
            for (int i = 0; i < legendStatus.size(); i++) {
                CellStatus status = legendStatus.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    @Override
    protected void drawRectangle(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (displayWidget.getCells() != null) {
            ContainerCell cell = (ContainerCell) displayWidget.getCells().get(
                new RowColPos(indexRow, indexCol));
            if (cell == null) {
                cell = new ContainerCell();
            }
            CellStatus status = cell.getStatus();
            if (status == null)
                status = defaultStatus;
            e.gc.setBackground(status.getColor());
            e.gc.fillRectangle(rectangle);
        }
        super.drawRectangle(e, displayWidget, rectangle, indexRow, indexCol);
    }

    @Override
    protected String getDefaultTextForBox(Map<RowColPos, ? extends Cell> cells,
        int indexRow, int indexCol) {
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
    protected String getBottomTextForBox(Map<RowColPos, ? extends Cell> cells,
        int indexRow, int indexCol) {
        if (getCellHeight() > HEIGHT_TWO_LINES) {
            return getContainerTypeText(cells, indexRow, indexCol);
        }
        return "";
    }

    protected String getContainerTypeText(Map<RowColPos, ? extends Cell> cells,
        int indexRow, int indexCol) {
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

    public void setDefaultStatus(CellStatus status) {
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

}
