package edu.ualberta.med.biobank.widgets.grids;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.gui.common.widgets.ImageCanvas;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionManager;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

/**
 * This class is there to give a common parent class to grid container widgets and drawers widgets
 */
public class ContainerDisplayWidget extends ImageCanvas {

    private static Logger log = LoggerFactory.getLogger(ContainerDisplayWidget.class.getName());

    protected Map<RowColPos, ? extends AbstractUIWell> cells;
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

    private final IContainerDisplayWidget tooltipCallback;

    private final boolean manageOwnSize;

    /**
     * max width this container will have : used to calculate cells width
     */
    protected int maxWidth = -1;

    /**
     * max height this container will have : used to calculate cells height
     */
    protected int maxHeight = -1;

    private final String name;

    /**
     * 
     * @param widgetParent
     * @param tooltipCallback
     * @param name
     * @param containerDisplay
     * @param cellStatus
     * @param manageOwnSize When set to true, the size of the widget depends on
     *            {@link containerDisplay}. Set this to false to have the the image displayed with
     *            scroll bars (when needed).
     */
    @SuppressWarnings("nls")
    public ContainerDisplayWidget(
        Composite widgetParent,
        IContainerDisplayWidget tooltipCallback,
        String name,
        AbstractContainerDisplay containerDisplay,
        List<UICellStatus> cellStatus,
        boolean manageOwnSize) {

        super(widgetParent, SWT.DOUBLE_BUFFERED);

        multiSelectionManager = new MultiSelectionManager(this);
        this.name = name;
        this.cells = new HashMap<RowColPos, AbstractUIWell>(0);
        this.tooltipCallback = tooltipCallback;
        this.manageOwnSize = manageOwnSize;

        if (containerDisplay == null) {
            throw new IllegalArgumentException("container display is null");
        }

        setContainerDisplay(containerDisplay, cellStatus);

        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        setLayoutData(gd);

        if (this.tooltipCallback != null) {
            addMouseTrackListener(new MouseTrackAdapter() {
                @Override
                public void mouseHover(MouseEvent e) {
                    SpecimenCell cell = (SpecimenCell) getObjectAtCoordinates(e.x, e.y);
                    if (cell != null) {
                        setToolTipText(ContainerDisplayWidget.this.tooltipCallback.getTooltipText(cell));
                    } else {
                        setToolTipText(null);
                    }
                }
            });
        }
    }

    public ContainerDisplayWidget(
        Composite widgetParent,
        IContainerDisplayWidget tooltipCallback,
        String name,
        List<UICellStatus> cellStatus,
        ContainerType containerType,
        boolean createDefaultContainer) {

        this(
            widgetParent,
            tooltipCallback,
            name,
            getContainerDisplayFromType(name, containerType, createDefaultContainer),
            cellStatus,
            true);
    }

    public ContainerDisplayWidget(Composite parent, String name) {
        this(
            parent,
            null,
            name,
            getContainerDisplayFromType(name, null, true),
            null,
            true);
    }

    @SuppressWarnings("nls")
    private static AbstractContainerDisplay getContainerDisplayFromType(
        String name,
        ContainerType containerType,
        boolean createDefaultContainer) {

        log.trace("initDisplayFromType");
        AbstractContainerDisplay display = null;

        if (containerType == null) {
            if (createDefaultContainer) {
                display = new GridContainerDisplay(name);
                display.setStorageSize(3, 5);
            }
        } else {
            display = new GridContainerDisplay(containerType.getName());
            Capacity capacity = containerType.getCapacity();
            display.setStorageSize(capacity.getRowCapacity(), capacity.getColCapacity());
        }

        if (display != null) {
            if (containerType != null) {
                display.setContainerType(containerType);
            }
        }

        return display;
    }

    @SuppressWarnings("nls")
    protected void initDisplayFromType(
        ContainerType containerType,
        boolean createDefaultContainer,
        Integer cellSize,
        List<UICellStatus> cellStatus) {

        log.trace("initDisplayFromType");
        AbstractContainerDisplay display = getContainerDisplayFromType(
            name, containerType, createDefaultContainer);

        if (display != null) {
            display.setDisplaySize(maxWidth, maxHeight);
        }
        setContainerDisplay(display, cellStatus);
        if ((display instanceof AbstractGridDisplay) && (cellSize != null)) {
            AbstractGridDisplay grid = (AbstractGridDisplay) display;
            grid.setCellWidth(cellSize);
            grid.setCellHeight(cellSize);
        }
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
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
            Map<RowColPos, ? extends AbstractUIWell> cells = getCells();
            if (cells != null) {
                return containerDisplay.getObjectAtCoordinates(cells, x, y);
            }
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
        log.trace("setStorageSize: rows: {}, cols: {}", rows, columns);
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
    private void setContainerDisplay(
        AbstractContainerDisplay display,
        List<UICellStatus> cellStatus) {
        log.trace("setContainerDisplay");
        containerDisplay = display;

        if (containerDisplay == null) {
            setSourceImage(null);
        } else {
            if (cellStatus != null) {
                containerDisplay.initLegend(cellStatus);
            }
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

    public void updateCells() {
        setSourceImage(containerDisplay.updateGridImage(this));
    }

    @SuppressWarnings("nls")
    @Override
    public Rectangle getClientArea() {
        if (manageOwnSize) {
            Rectangle clientArea = containerDisplay.getClientArea();
            log.trace("getClientArea: containerDisplay clientArea: {}", clientArea);
            return clientArea;
        }

        Rectangle clientArea = super.getClientArea();
        log.trace("getClientArea: super clientArea: {}", clientArea);
        return clientArea;
    }

}
