package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

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

    @Override
    protected Color getDefaultBackgroundColor(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol) {
        if (displayWidget.getCells() != null) {
            AbstractUIWell uiCell = displayWidget.getCells().get(
                new RowColPos(indexRow, indexCol));
            if (uiCell == null) {
                return super.getDefaultBackgroundColor(e, displayWidget,
                    rectangle, indexRow, indexCol);
            }
            UICellStatus status = uiCell.getStatus();
            if (status == null)
                status = defaultStatus;
            return status.getColor();
        }
        return super.getDefaultBackgroundColor(e, displayWidget, rectangle,
            indexRow, indexCol);
    }

    @Override
    protected String getDefaultTextForBox(Map<RowColPos, ? extends AbstractUIWell> cells,
        int indexRow, int indexCol) {
        String text = super.getDefaultTextForBox(cells, indexRow, indexCol);
        if (text.isEmpty()) {
            return StringUtil.EMPTY_STRING;
        }

        if (getCellHeight() <= HEIGHT_TWO_LINES) {
            return text + " " + getContainerTypeText(cells, indexRow, indexCol); //$NON-NLS-1$
        }
        return text;
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
    protected String getContainerTypeText(Map<RowColPos, ? extends AbstractUIWell> cells,
        int indexRow, int indexCol) {
        StringBuffer sname = new StringBuffer();
        if (cells != null) {
            ContainerCell cell = (ContainerCell) cells.get(new RowColPos(indexRow, indexCol));
            if ((cell != null)
                && (cell.getContainer() != null)
                && (cell.getContainer().getContainerType() != null)
                && (cell.getContainer().getContainerType().getNameShort() != null))
                sname
                    .append("(")
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
        return this.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

}
