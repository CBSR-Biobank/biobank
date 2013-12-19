package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.ContainerCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public class GridContainerDisplay extends AbstractGridDisplay {

    private static final int HEIGHT_TWO_LINES = 40;

    /**
     * Default status when cell doesn't have any status
     */
    private UICellStatus defaultStatus = UICellStatus.NOT_INITIALIZED;

    public GridContainerDisplay(String name) {
        super(name);
    }

    @SuppressWarnings("nls")
    @Override
    protected Color getDefaultBackgroundColor(
        Display display,
        Map<RowColPos, ? extends AbstractUIWell> cells,
        Rectangle rectangle,
        int indexRow,
        int indexCol) {
        if (cells == null) {
            throw new IllegalArgumentException("cells is null");
        }

        if (cells.isEmpty()) {
            return super.getDefaultBackgroundColor(display, cells, rectangle, indexRow, indexCol);
        }

        AbstractUIWell uiCell = cells.get(new RowColPos(indexRow, indexCol));
        if (uiCell == null) {
            return super.getDefaultBackgroundColor(display, cells, rectangle, indexRow, indexCol);
        }
        UICellStatus status = uiCell.getStatus();
        if (status == null) {
            status = defaultStatus;
        }
        return status.getColor();
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDefaultTextForBox(
        Map<RowColPos, ? extends AbstractUIWell> cells,
        int indexRow,
        int indexCol) {
        String text = super.getDefaultTextForBox(cells, indexRow, indexCol);
        if (text.isEmpty()) {
            return text;
        }

        StringBuffer buf = new StringBuffer();
        buf.append(text);

        if (getCellHeight() <= HEIGHT_TWO_LINES) {
            buf.append(" ");
            buf.append(getContainerTypeText(cells, indexRow, indexCol));
        }
        return buf.toString();
    }

    @Override
    protected String getBottomTextForBox(Map<RowColPos, ? extends AbstractUIWell> cells,
        int indexRow, int indexCol) {
        if (getCellHeight() > HEIGHT_TWO_LINES) {
            return getContainerTypeText(cells, indexRow, indexCol);
        }
        return StringUtil.EMPTY_STRING;
    }

    @SuppressWarnings("nls")
    protected String getContainerTypeText(
        Map<RowColPos, ? extends AbstractUIWell> cells,
        int indexRow,
        int indexCol) {
        StringBuffer sname = new StringBuffer();
        if (cells != null) {
            ContainerCell cell = (ContainerCell) cells.get(new RowColPos(indexRow, indexCol));
            if ((cell != null)
                && (cell.getContainer() != null)
                && (cell.getContainer().getContainerType() != null)
                && (cell.getContainer().getContainerType().getNameShort() != null))
                sname.append("(")
                    .append(cell.getContainer().getContainerType().getNameShort())
                    .append(")");
        }
        return sname.toString();
    }

    public void setDefaultStatus(UICellStatus status) {
        this.defaultStatus = status;
    }

    @Override
    public Point getSizeToApply() {
        return computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

}
