package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.Cell;

/**
 * This class is there to give a common parent class to grid container widgets
 * and drawers widgets
 */
public abstract class AbstractContainerDisplayWidget extends Canvas {

    protected Map<RowColPos, ? extends Cell> cells;

    protected ContainerWrapper container;

    protected ContainerTypeWrapper containerType;
    /**
     * true if we want the container to display full info in each box displayed
     */
    protected boolean displayFullInfoString = false;

    private MultiSelectionManager multiSelectionManager;

    public AbstractContainerDisplayWidget(Composite parent, int style) {
        super(parent, style);
        multiSelectionManager = new MultiSelectionManager(this);
    }

    /**
     * if we don't want to display information for cells, can specify a selected
     * box to highlight
     */
    protected RowColPos selection;

    public abstract Cell getObjectAtCoordinates(int x, int y);

    public abstract void initLegend();

    public void setCells(Map<RowColPos, ? extends Cell> cells) {
        this.cells = cells;
        redraw();
    }

    public void setSelection(RowColPos selection) {
        this.selection = selection;
        redraw();
    }

    public void displayFullInfoString(boolean display) {
        this.displayFullInfoString = display;
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

    /**
     * Get the text to write inside the cell. This default implementation use
     * the cell position and the containerType.
     */
    protected String getDefaultTextForBox(int indexRow, int indexCol) {
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

    public Map<RowColPos, ? extends Cell> getCells() {
        return cells;
    }

    public MultiSelectionManager getMultiSelectionManager() {
        return multiSelectionManager;
    }

}
