package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.util.RowColPos;

/**
 * This class is there to give a common parent class to grid container widgets
 * and drawers widgets
 */
public class ContainerDisplayWidget extends Canvas {

    protected Map<RowColPos, ? extends Cell> cells;

    protected ContainerWrapper container;

    protected ContainerTypeWrapper containerType;
    /**
     * true if we want the container to display full info in each box displayed
     */
    protected boolean displayFullInfoString = false;

    /**
     * if we don't want to display information for cells, can specify a selected
     * box to highlight
     */
    protected RowColPos selection;

    private MultiSelectionManager multiSelectionManager;

    protected AbstractContainerDisplay containerDisplay;

    public ContainerDisplayWidget(Composite parent) {
        super(parent, SWT.DOUBLE_BUFFERED);
        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                paintGrid(e);
            }
        });
        multiSelectionManager = new MultiSelectionManager(this);
    }

    protected void paintGrid(PaintEvent e) {
        if (containerDisplay != null) {
            Point size = containerDisplay.getSizeToApply();
            if (size != null) {
                setSize(size);
            }
            containerDisplay.paintGrid(e, this);
        }
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        if (containerDisplay != null) {
            return containerDisplay.computeSize(wHint, hHint, changed);
        }
        return super.computeSize(wHint, hHint, changed);
    }

    public Cell getObjectAtCoordinates(int x, int y) {
        return containerDisplay.getObjectAtCoordinates(this, x, y);
    }

    public void initLegend() {
        // TODO ok ?
        containerDisplay.initLegend();
    }

    public void setCells(Map<RowColPos, ? extends Cell> cells) {
        this.cells = cells;
        redraw();
    }

    /**
     * Modify only the number of rows and columns of the grid. If no max width
     * and max height has been given to the grid, the default cell width and
     * cell height will be used
     */
    public void setStorageSize(int rows, int columns) {
        containerDisplay.setStorageSize(rows, columns);
        redraw();
    }

    public void setDisplaySize(int maxWidth, int maxHeight) {
        containerDisplay.setDisplaySize(maxWidth, maxHeight);
    }

    public void setSelection(RowColPos selection) {
        this.selection = selection;
        redraw();
    }

    public RowColPos getSelection() {
        return selection;
    }

    public void displayFullInfoString(boolean display) {
        this.displayFullInfoString = display;
    }

    public void setContainer(ContainerWrapper container) {
        this.container = container;
        if (container != null) {
            setContainerType(container.getContainerType());
        }
        containerDisplay.setContainer(container);
    }

    public void setContainerType(ContainerTypeWrapper type) {
        setContainerType(type, false);
    }

    public void setContainerType(ContainerTypeWrapper type,
        boolean createDefaultContainer) {
        this.containerType = type;
        initDisplayFromType(createDefaultContainer);
        containerDisplay.setContainerType(containerType);
    }

    public void initDisplayFromType(boolean createDefaultContainer) {
        if (containerType == null) {
            if (createDefaultContainer) {
                containerDisplay = new GridContainerDisplay();
                containerDisplay.setStorageSize(3, 5);
            }
        } else if (containerType.getName().equals("Drawer 36")) {
            containerDisplay = new Drawer36Display();
        } else {
            containerDisplay = new GridContainerDisplay();
        }
        if (containerType != null) {
            containerDisplay.setContainerType(containerType);
        }
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
