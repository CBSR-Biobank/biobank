package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;

public class ChooseContainerWidget extends AbstractGridContainerWidget {

    private ContainerCell[][] cells;

    private List<ContainerStatus> legendStatus;

    /**
     * Default status when cell doesn't have any status
     */
    private ContainerStatus defaultStatus = ContainerStatus.NOT_INITIALIZED;

    @SuppressWarnings("unused")
    private boolean showNullStatusAsEmpty = false;

    public ChooseContainerWidget(Composite parent) {
        super(parent);
    }

    public ContainerCell getPositionAtCoordinates(int x, int y) {
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

    public void initDefaultLegend() {
        List<ContainerStatus> legendStatus = new ArrayList<ContainerStatus>();
        legendStatus.add(ContainerStatus.NOT_INITIALIZED);
        legendStatus.add(ContainerStatus.INITIALIZED);
        setLegend(legendStatus);
    }

    public void setContainersStatus(ContainerCell[][] cells) {
        this.cells = cells;
        computeSize(-1, -1);
        legendWidth = gridWidth / legendStatus.size();
        redraw();
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
    protected String getTextForBox(int indexRow, int indexCol) {
        String sname = "";
        if (cells != null) {
            ContainerCell cell = cells[indexRow][indexCol];

            if ((cell.getPosition().getContainer() != null)
                && (cell.getPosition().getContainer().getContainerType()
                    .getNameShort() != null))
                sname += "-"
                    + cell.getPosition().getContainer().getContainerType()
                        .getNameShort();
        }
        RowColPos rowcol = new RowColPos();
        rowcol.row = indexRow;
        rowcol.col = indexCol;
        if (super.getContainerType() != null) {
            return parentLabel
                + LabelingScheme.getPositionString(rowcol, super
                    .getContainerType()) + sname;
        }

        String row = getValueForCell(firstRowSign, indexRow,
            firstColSign == null);
        String col = getValueForCell(firstColSign, indexCol,
            firstRowSign == null);
        if (showColumnFirst) {
            return col + row;
        }
        return row + col;
    }

    public void setShowNullStatusAsEmpty(boolean showNullStatusAsEmpty) {
        this.showNullStatusAsEmpty = showNullStatusAsEmpty;
    }

    public void setLegend(List<ContainerStatus> legendStatus) {
        hasLegend = true;
        this.legendStatus = legendStatus;
    }

    public void setDefaultStatus(ContainerStatus status) {
        this.defaultStatus = status;
    }
}
