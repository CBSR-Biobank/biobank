package edu.ualberta.med.biobank.widgets.grids;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionManager;

/**
 * This class is there to give a common parent class to grid container widgets
 * and drawers widgets
 */
public class ContainerDisplayWidget extends Canvas {

    protected Map<RowColPos, ? extends AbstractUICell> cells;

    protected Container container;

    protected ContainerType containerType;
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

    private AbstractContainerDisplay containerDisplay;

    /**
     * max width this container will have : used to calculate cells width
     */
    protected int maxWidth = -1;

    /**
     * max height this container will have : used to calculate cells height
     */
    protected int maxHeight = -1;

    private List<UICellStatus> cellStatus;

    public ContainerDisplayWidget(Composite parent) {
        this(parent, null);
    }

    public ContainerDisplayWidget(Composite parent,
        List<UICellStatus> cellStatus) {
        super(parent, SWT.DOUBLE_BUFFERED);
        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                paintGrid(e);
            }
        });
        multiSelectionManager = new MultiSelectionManager(this);
        this.cellStatus = cellStatus;
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

    public AbstractUICell getObjectAtCoordinates(int x, int y) {
        if (containerDisplay != null) {
            return containerDisplay.getObjectAtCoordinates(this, x, y);
        }
        return null;
    }

    public void setCells(Map<RowColPos, ? extends AbstractUICell> cells) {
        this.cells = cells;
        redraw();
    }

    /**
     * Modify only the number of rows and columns of the grid. If no max width
     * and max height has been given to the grid, the default cell width and
     * cell height will be used
     */
    public void setStorageSize(int rows, int columns) {
        if (containerDisplay != null) {
            containerDisplay.setStorageSize(rows, columns);
            redraw();
        }
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
        if (containerDisplay != null) {
            containerDisplay.setDisplaySize(maxWidth, maxHeight);
        }
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

    public void setContainer(Container container) {
        this.container = container;
        if (container != null) {
            setContainerType(container.getContainerType());
            containerDisplay.setContainer(container);
        }
    }

    public void setContainer(ContainerWrapper container) {
        setContainer(container.getWrappedObject());
    }

    public void setContainerType(ContainerType type) {
        setContainerType(type, null, false);
    }

    public void setContainerType(ContainerType type, Integer cellSize) {
        setContainerType(type, cellSize, false);
    }

    public void setContainerType(ContainerType type, Integer cellSize,
        boolean createDefaultContainer) {
        this.containerType = type;
        initDisplayFromType(createDefaultContainer, cellSize);
    }

    public void setContainerType(ContainerTypeWrapper type) {
        setContainerType(type.getWrappedObject());
    }

    public void setContainerType(ContainerType type,
        boolean createDefaultContainer) {
        this.containerType = type;
        initDisplayFromType(createDefaultContainer);
    }

    public void setContainerType(ContainerTypeWrapper type, Integer cellSize) {
        setContainerType(type.getWrappedObject(), cellSize, false);
    }

    public void initDisplayFromType(boolean createDefaultContainer) {
        initDisplayFromType(createDefaultContainer, null);
    }

    public void initDisplayFromType(boolean createDefaultContainer,
        Integer cellSize) {
        AbstractContainerDisplay display = null;
        if (containerType == null) {
            if (createDefaultContainer) {
                display = new GridContainerDisplay();
                display.setStorageSize(3, 5);
            }
        } else if (containerType.getName().equals(
            Drawer36Display.CONTAINER_NAME)) {
            display = new Drawer36Display();
        } else {
            display = new GridContainerDisplay();
        }
        if (display != null) {
            display.setDisplaySize(maxWidth, maxHeight);
            if (containerType != null) {
                display.setContainerType(containerType);
            }
        }
        setContainerDisplay(display);
        if (display instanceof AbstractGridDisplay && cellSize != null) {
            ((AbstractGridDisplay) display).setCellWidth(cellSize);
            ((AbstractGridDisplay) display).setCellHeight(cellSize);
        }
    }

    protected void setContainerDisplay(AbstractContainerDisplay display) {
        containerDisplay = display;
        if (cellStatus != null && containerDisplay != null) {
            containerDisplay.initLegend(cellStatus);
        }
    }

    protected AbstractContainerDisplay getContainerDisplay() {
        return containerDisplay;
    }

    public Map<RowColPos, ? extends AbstractUICell> getCells() {
        return cells;
    }

    public MultiSelectionManager getMultiSelectionManager() {
        return multiSelectionManager;
    }

    public RowColPos getPositionAtCoordinates(int x, int y) {
        return containerDisplay.getPositionAtCoordinates(x, y);
    }

}
