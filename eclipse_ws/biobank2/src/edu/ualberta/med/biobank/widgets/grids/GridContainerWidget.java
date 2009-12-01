package edu.ualberta.med.biobank.widgets.grids;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;

public class GridContainerWidget extends AbstractGridWidget {

    private ContainerCell[][] cells;

    private List<ContainerStatus> legendStatus;

    /**
     * Default status when cell doesn't have any status
     */
    private ContainerStatus defaultStatus = ContainerStatus.NOT_INITIALIZED;

    public GridContainerWidget(Composite parent) {
        super(parent);
    }

    @Override
    public Object getObjectAtCoordinates(int x, int y) {
        if (cells == null) {
            return null;
        }
        int col = x / getCellWidth();
        int row = y / getCellHeight();
        if (col >= 0 && col < getCols() && row >= 0 && row < getRows()) {
            return cells[row][col];
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

    public void setCellsStatus(ContainerCell[][] cells) {
        this.cells = cells;
        computeSize(-1, -1);
        if (legendStatus != null) {
            legendWidth = gridWidth / legendStatus.size();
        }
        redraw();
    }

    @Override
    public void setInput(Object object) {
        Assert.isNotNull(object);
        Assert.isTrue(object.getClass().isArray());
        ContainerCell[][] cells = (ContainerCell[][]) object;
        setCellsStatus(cells);
    }

    @Override
    protected void paintGrid(PaintEvent e) {
        super.paintGrid(e);
        if (hasLegend) {
            for (int i = 0; i < legendStatus.size(); i++) {
                ContainerStatus status = legendStatus.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    @Override
    protected void drawRectangle(PaintEvent e, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (cells != null) {
            ContainerCell cell = cells[indexRow][indexCol];
            if (cell == null) {
                cell = new ContainerCell();
            }
            ContainerStatus status = cell.getStatus();
            if (status == null)
                status = defaultStatus;
            e.gc.setBackground(status.getColor());
            e.gc.fillRectangle(rectangle);
        }
        super.drawRectangle(e, rectangle, indexRow, indexCol);
    }

    @Override
    protected String getDefaultTextForBox(int indexRow, int indexCol) {
        String sname = "";
        if (cells != null) {
            ContainerCell cell = cells[indexRow][indexCol];
            if ((cell != null)
                && (cell.getContainer() != null)
                && (cell.getContainer().getContainerType() != null)
                && (cell.getContainer().getContainerType().getNameShort() != null))
                sname += "-"
                    + cell.getContainer().getContainerType().getNameShort();
        }
        String text = super.getDefaultTextForBox(indexRow, indexCol);
        if (!text.isEmpty()) {
            return text + sname;
        }
        return "";
    }

    public void setLegend(List<ContainerStatus> legendStatus) {
        hasLegend = true;
        this.legendStatus = legendStatus;
    }

    public void setDefaultStatus(ContainerStatus status) {
        this.defaultStatus = status;
    }

}
