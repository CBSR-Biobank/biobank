package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;

/**
 * This class is there to give a common parent class to grid container widgets
 * and drawers widgets
 */
public abstract class AbstractContainerDisplay {

    protected ContainerWrapper container;

    protected ContainerTypeWrapper containerType;
    /**
     * true if we want the container to display full info in each box displayed
     */
    protected boolean displayFullInfoString = false;

    /**
     * max width this container will have : used to calculate cells width
     */
    protected int maxWidth = -1;

    /**
     * max height this container will have : used to calculate cells height
     */
    protected int maxHeight = -1;

    protected List<UICellStatus> legendStatus;

    public AbstractUICell getObjectAtCoordinates(
        ContainerDisplayWidget displayWidget, int x, int y) {
        if (displayWidget.getCells() == null) {
            return null;
        }
        RowColPos rcp = getPositionAtCoordinates(x, y);
        if (rcp != null) {
            return displayWidget.getCells().get(rcp);
        }
        return null;
    }

    public abstract RowColPos getPositionAtCoordinates(int x, int y);

    public void initLegend(List<UICellStatus> status) {
        this.legendStatus = status;
    }

    public void setContainer(ContainerWrapper container) {
        this.container = container;
        if (container != null) {
            setContainerType(container.getContainerType());
        }
    }

    public void setContainerType(ContainerTypeWrapper type) {
        this.containerType = type;
    }

    protected abstract void paintGrid(PaintEvent e,
        ContainerDisplayWidget displayWidget);

    protected abstract Point computeSize(int wHint, int hHint, boolean changed);

    /**
     * Get the text to write inside the cell. This default implementation use
     * the cell position and the containerType.
     */
    protected String getDefaultTextForBox(
        @SuppressWarnings("unused") Map<RowColPos, ? extends AbstractUICell> cells,
        int indexRow, int indexCol) {
        RowColPos rowcol = new RowColPos();
        rowcol.row = indexRow;
        rowcol.col = indexCol;
        String parentLabel = "";
        if (displayFullInfoString && container != null) {
            parentLabel = container.getLabel();
        }
        if (containerType != null) {
            return parentLabel + containerType.getPositionString(rowcol);
        }
        return "";
    }

    @SuppressWarnings("unused")
    public void setStorageSize(int rows, int columns) {
        //
    }

    public Point getSizeToApply() {
        return null;
    }

    /**
     * Modify dimensions of the grid. maxWidth and maxHeight are used to
     * calculate the size of the cells
     * 
     * @param maxWidth max width the grid should have
     * @param maxHeight max height the grid should have
     */
    public void setDisplaySize(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

}
