package edu.ualberta.med.biobank.widgets.grids;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.ImageCanvas;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionManager;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

/**
 * This class is there to give a common parent class to grid container widgets and drawers widgets
 */
public class ContainerDisplayWidget extends ImageCanvas {

    private static Logger log = LoggerFactory.getLogger(ContainerDisplayWidget.class.getName());

    protected Map<RowColPos, ? extends AbstractUIWell> cells;

    protected Container container;

    protected ContainerType containerType;
    /**
     * true if we want the container to display full info in each box displayed
     */
    protected boolean displayFullInfoString = false;

    /**
     * if we don't want to display information for cells, can specify a selected box to highlight
     */
    protected RowColPos selection;

    private final MultiSelectionManager multiSelectionManager;

    private AbstractContainerDisplay containerDisplay;

    /**
     * max width this container will have : used to calculate cells width
     */
    protected int maxWidth = -1;

    /**
     * max height this container will have : used to calculate cells height
     */
    protected int maxHeight = -1;

    private final List<UICellStatus> cellStatus;

    private final String name;

    public ContainerDisplayWidget(Composite parent, String name, List<UICellStatus> cellStatus) {
        super(parent, SWT.DOUBLE_BUFFERED);
        multiSelectionManager = new MultiSelectionManager(this);
        this.cellStatus = cellStatus;
        this.name = name;
        this.cells = new HashMap<RowColPos, AbstractUIWell>(0);

        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        setLayout(layout);

        Point sizeHint = getSizeHint();
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        gd.widthHint = sizeHint.x;
        gd.heightHint = sizeHint.y;
        setLayoutData(gd);
    }

    public ContainerDisplayWidget(Composite parent, String name) {
        this(parent, name, null);
    }

    @SuppressWarnings("nls")
    public void initDisplayFromType(boolean createDefaultContainer, Integer cellSize) {
        log.trace("initDisplayFromType");
        AbstractContainerDisplay display = null;

        if (containerType == null) {
            if (createDefaultContainer) {
                display = new GridContainerDisplay(this.name);
                display.setStorageSize(3, 5);
            }
        } else if (containerType.getName().equals(Drawer36Display.CONTAINER_NAME)) {
            display = new Drawer36Display(containerType.getName());
        } else {
            display = new GridContainerDisplay(containerType.getName());
        }

        if (display != null) {
            display.setDisplaySize(maxWidth, maxHeight);
            if (containerType != null) {
                display.setContainerType(containerType);
            }
        }
        setContainerDisplay(display);
        if ((display instanceof AbstractGridDisplay) && (cellSize != null)) {
            AbstractGridDisplay grid = (AbstractGridDisplay) display;
            grid.setCellWidth(cellSize);
            grid.setCellHeight(cellSize);
        }
    }

    @SuppressWarnings("nls")
    public void initDisplayFromType(boolean createDefaultContainer) {
        log.trace("initDisplayFromType");
        initDisplayFromType(createDefaultContainer, PalletDisplay.SAMPLE_WIDTH);
    }

    @SuppressWarnings("nls")
    public void setContainerType(ContainerType type, Integer cellSize,
        boolean createDefaultContainer) {
        log.trace("setContainerType");
        this.containerType = type;
        initDisplayFromType(createDefaultContainer, cellSize);
    }

    @SuppressWarnings("nls")
    public void setContainerType(ContainerType type, boolean createDefaultContainer) {
        log.trace("setContainerType");
        this.containerType = type;
        initDisplayFromType(createDefaultContainer);
    }

    @SuppressWarnings("nls")
    public void setContainer(Container container) {
        log.trace("setContainer");
        this.container = container;
        if (container != null) {
            setContainerType(container.getContainerType());
            containerDisplay.setContainer(container);
            setSourceImage(containerDisplay.updateGridImage(this));
        }
    }

    @SuppressWarnings("nls")
    public void setContainer(ContainerWrapper container) {
        log.trace("setContainer");
        setContainer(container.getWrappedObject());
    }

    @SuppressWarnings("nls")
    public void setContainerType(ContainerType type) {
        log.trace("setContainerType");
        setContainerType(type, PalletDisplay.SAMPLE_WIDTH, false);
    }

    @SuppressWarnings("nls")
    public void setContainerType(ContainerType type, Integer cellSize) {
        log.trace("setContainerType");
        setContainerType(type, cellSize, false);
    }

    @SuppressWarnings("nls")
    public void setContainerType(ContainerTypeWrapper type) {
        log.trace("setContainerType");
        setContainerType(type.getWrappedObject());
    }

    @SuppressWarnings("nls")
    public void setContainerType(ContainerTypeWrapper type, Integer cellSize) {
        log.trace("setContainerType");
        setContainerType(type.getWrappedObject(), cellSize, false);
    }

    @SuppressWarnings("nls")
    public ContainerType getContainerType() {
        log.trace("getContainerType");
        return containerType;
    }

    @SuppressWarnings("nls")
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        log.debug("computeSize");
        if (containerDisplay != null) {
            return containerDisplay.computeSize(wHint, hHint, changed);
        }
        return super.computeSize(wHint, hHint, changed);
    }

    public Point getSizeHint() {
        return containerDisplay.computeSize(0, 0, false);
    }

    @Override
    protected void paint(GC gc) {
        if (containerDisplay != null) {
            Point size = containerDisplay.getSizeToApply();
            if (size != null) {
                setSize(size);
            }
        }
        super.paint(gc);
    }

    @SuppressWarnings("nls")
    public AbstractUIWell getObjectAtCoordinates(int x, int y) {
        log.trace("getObjectAtCoordinates");
        if (containerDisplay != null) {
            return containerDisplay.getObjectAtCoordinates(this, x, y);
        }
        return null;
    }

    @SuppressWarnings("nls")
    public void setCells(Map<RowColPos, ? extends AbstractUIWell> cells) {
        log.trace("setCells");
        this.cells = cells;
        updateCells();
    }

    /**
     * Modify only the number of rows and columns of the grid. If no max width and max height has
     * been given to the grid, the default cell width and cell height will be used
     */
    @SuppressWarnings("nls")
    public void setStorageSize(int rows, int columns) {
        log.trace("setStorageSize");
        if (containerDisplay != null) {
            containerDisplay.setStorageSize(rows, columns);
            updateCells();
        }
    }

    /**
     * Modify dimensions of the grid. maxWidth and maxHeight are used to calculate the size of the
     * cells
     * 
     * @param maxWidth max width the grid should have
     * @param maxHeight max height the grid should have
     */
    @SuppressWarnings("nls")
    public void setDisplaySize(int maxWidth, int maxHeight) {
        log.trace("setDisplaySize");
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        if (containerDisplay != null) {
            containerDisplay.setDisplaySize(maxWidth, maxHeight);
            setSourceImage(containerDisplay.updateGridImage(this));
        }
    }

    @SuppressWarnings("nls")
    public void setSelection(RowColPos selection) {
        log.trace("setSelection");
        this.selection = selection;
        updateCells();
    }

    @SuppressWarnings("nls")
    public RowColPos getSelection() {
        log.trace("getSelection");
        return selection;
    }

    @SuppressWarnings("nls")
    public void displayFullInfoString(boolean display) {
        log.trace("displayFullInfoString");
        this.displayFullInfoString = display;
    }

    @SuppressWarnings("nls")
    protected void setContainerDisplay(AbstractContainerDisplay display) {
        log.trace("setContainerDisplay");
        containerDisplay = display;
        if ((cellStatus != null) && (containerDisplay != null)) {
            containerDisplay.initLegend(cellStatus);
        }
        if (containerDisplay == null) {
            setSourceImage(null);
        } else {
            setSourceImage(containerDisplay.updateGridImage(this));
        }
    }

    @SuppressWarnings("nls")
    protected AbstractContainerDisplay getContainerDisplay() {
        log.trace("getContainerDisplay");
        return containerDisplay;
    }

    @SuppressWarnings("nls")
    public Map<RowColPos, ? extends AbstractUIWell> getCells() {
        log.trace("getCells");
        return cells;
    }

    @SuppressWarnings("nls")
    public MultiSelectionManager getMultiSelectionManager() {
        log.trace("getMultiSelectionManager");
        return multiSelectionManager;
    }

    @SuppressWarnings("nls")
    public RowColPos getPositionAtCoordinates(int x, int y) {
        log.trace("getPositionAtCoordinates");
        return containerDisplay.getPositionAtCoordinates(x, y);
    }

    @SuppressWarnings("nls")
    @Override
    public Rectangle getClientArea() {
        log.trace("getClientArea");
        return containerDisplay.getClientArea();
    }

    public void updateCells() {
        setSourceImage(containerDisplay.updateGridImage(this));
    }

}
