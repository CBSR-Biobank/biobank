package edu.ualberta.med.biobank.widgets.grids;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;

public class GridContainerDisplay extends AbstractGridDisplay {

    private static final int HEIGHT_TWO_LINES = 40;

    private List<ContainerStatus> legendStatus;

    /**
     * Default status when cell doesn't have any status
     */
    private ContainerStatus defaultStatus = ContainerStatus.NOT_INITIALIZED;

    @Override
    public Cell getObjectAtCoordinates(ContainerDisplayWidget displayWidget,
        int x, int y) {
        if (displayWidget.getCells() == null) {
            return null;
        }
        int col = x / getCellWidth();
        int row = y / getCellHeight();
        if (col >= 0 && col < getCols() && row >= 0 && row < getRows()) {
            return displayWidget.getCells().get(new RowColPos(row, col));
        }
        return null;
    }

    @Override
    public void initLegend() {
        List<ContainerStatus> legendStatus = new ArrayList<ContainerStatus>();
        legendStatus.add(ContainerStatus.NOT_INITIALIZED);
        legendStatus.add(ContainerStatus.INITIALIZED);
        setLegend(legendStatus);
    }

    @Override
    protected void paintGrid(PaintEvent e, ContainerDisplayWidget displayWidget) {
        super.paintGrid(e, displayWidget);
        if (legendStatus != null) {
            legendWidth = gridWidth / legendStatus.size();
        }
        if (hasLegend) {
            for (int i = 0; i < legendStatus.size(); i++) {
                ContainerStatus status = legendStatus.get(i);
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
            ContainerStatus status = cell.getStatus();
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

    public void setLegend(List<ContainerStatus> legendStatus) {
        hasLegend = true;
        this.legendStatus = legendStatus;
    }

    public void setDefaultStatus(ContainerStatus status) {
        this.defaultStatus = status;
    }

}
