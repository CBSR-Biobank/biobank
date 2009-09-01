package edu.ualberta.med.biobank.widgets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerStatus;

public class CabinetDrawerWidget extends Canvas {

    public static final int SQUARE_CELL_WIDTH = 70;

    public static final int RECTANGLE_CELL_WIDTH = SQUARE_CELL_WIDTH * 2;

    public static final int RECTANGLE_CELL_HEIGHT = 45;

    private static final int GRID_WIDTH = 8 * SQUARE_CELL_WIDTH;

    private static final int GRID_HEIGHT = 3 * SQUARE_CELL_WIDTH + 3
        * RECTANGLE_CELL_HEIGHT;

    public static final int WIDTH = GRID_WIDTH + 10;

    public static final int HEIGHT = GRID_HEIGHT + 10;

    private int boxNumber = 36;

    private int selectedBin = -1;

    private Boolean hasLegend = false;

    // single dimension format, (boxNumber slots)
    private ContainerCell[][] cells;

    private ArrayList<ContainerStatus> legendStatus;

    public static int LEGEND_WIDTH = 70;

    public static int LEGEND_HEIGHT = 20;

    public CabinetDrawerWidget(Composite parent) {
        super(parent, SWT.DOUBLE_BUFFERED);
        cells = new ContainerCell[boxNumber][1];
        for (int i = 0; i < boxNumber; i++) {
            ContainerPosition pos = new ContainerPosition();
            pos.setRow(i);
            pos.setCol(0);
            ContainerStatus stat = ContainerStatus.NOT_INITIALIZED;
            ContainerCell cell = new ContainerCell();
            cell.setPosition(pos);
            cell.setStatus(stat);
            cells[i][0] = cell;
        }
        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                paintDrawer(e);
            }
        });
    }

    public void initLegend() {
        hasLegend = true;
        legendStatus = new ArrayList<ContainerStatus>();
        legendStatus.add(ContainerStatus.NOT_INITIALIZED);
        legendStatus.add(ContainerStatus.INITIALIZED);
    }

    protected void paintDrawer(PaintEvent e) {
        setSize(WIDTH, HEIGHT + LEGEND_HEIGHT);
        GC gc = e.gc;
        int currentX = 0;
        int rectYTotal = 0;
        int squareYTotal = 0;
        int squareXTotal = 0;
        for (int boxIndex = 1; boxIndex <= boxNumber; boxIndex++) {
            int width = SQUARE_CELL_WIDTH;
            int height = SQUARE_CELL_WIDTH;
            int rectXPosition = squareXTotal * SQUARE_CELL_WIDTH;
            if (boxIndex % 3 == 0) {
                rectYTotal++;
                width = RECTANGLE_CELL_WIDTH;
                height = RECTANGLE_CELL_HEIGHT;
                currentX = 0;
            } else {
                if (currentX == 1) {
                    rectXPosition += SQUARE_CELL_WIDTH;
                } else {
                    squareYTotal++;
                }
                currentX++;
            }
            int rectYPosition = GRID_HEIGHT
                - (squareYTotal * SQUARE_CELL_WIDTH + rectYTotal
                    * RECTANGLE_CELL_HEIGHT);

            Rectangle rectangle = new Rectangle(rectXPosition, rectYPosition,
                width, height);
            gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));

            ContainerStatus status = cells[boxIndex - 1][0].getStatus();
            if (status == null)
                status = ContainerStatus.NOT_INITIALIZED;

            gc.setBackground(status.getColor());
            gc.fillRectangle(rectangle);
            if ((selectedBin + 1) == boxIndex) {
                gc.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
                gc.fillRectangle(rectangle);
            }
            gc.drawRectangle(rectangle);

            DecimalFormat df1 = new DecimalFormat("00");
            String text = df1.format(boxIndex);
            if (text != null) {
                drawTextOnCenter(gc, text, rectangle);
            }
            if (boxIndex % 9 == 0) {
                squareXTotal += 2;
                squareYTotal = 0;
                rectYTotal = 0;
            }
        }
        if (hasLegend) {
            for (int i = 0; i < legendStatus.size(); i++) {
                ContainerStatus status = legendStatus.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    /**
     * Draw the text on the middle of the rectangle
     */
    public void drawTextOnCenter(GC gc, String text, Rectangle rectangle) {
        Point textSize = gc.textExtent(text);
        int xTextPosition = (rectangle.width - textSize.x) / 2 + rectangle.x;
        int yTextPosition = (rectangle.height - textSize.y) / 2 + rectangle.y;
        gc.drawText(text, xTextPosition, yTextPosition, true);
    }

    public void setSelectedBin(int bin) {
        this.selectedBin = bin;
        redraw();
    }

    public void setContainersStatus(
        Collection<ContainerPosition> childPositionCollection) {
        for (ContainerPosition position : childPositionCollection) {
            int pos = position.getRow().intValue();
            cells[pos][0] = new ContainerCell(position);
            cells[pos][0].setStatus(ContainerStatus.INITIALIZED);
        }
        LEGEND_WIDTH = WIDTH / legendStatus.size();
        redraw();
    }

    public ContainerCell getPositionAtCoordinates(int x, int y) {
        if (cells == null) {
            return null;
        }
        int gridCellWidth = RECTANGLE_CELL_WIDTH;
        int gridCellHeight = SQUARE_CELL_WIDTH + RECTANGLE_CELL_HEIGHT;
        // get high level position
        int xGrid = x / (gridCellWidth);
        int yGrid = y / (gridCellHeight);
        int cellNum;
        // get subcell position
        if (y % gridCellHeight < SQUARE_CELL_WIDTH)
            cellNum = 9;
        else if (x % gridCellWidth > SQUARE_CELL_WIDTH)
            cellNum = 8;
        else
            cellNum = 7;
        // convert subcell to real cell
        int xGridCellNumOffset = 9;
        int yGridCellNumOffset = 3;
        return cells[cellNum + xGrid * xGridCellNumOffset - yGrid
            * yGridCellNumOffset - 1][0];

    }

    protected void drawLegend(PaintEvent e, Color color, int index, String text) {
        e.gc.setBackground(color);
        int width = LEGEND_WIDTH;
        int startx = LEGEND_WIDTH * index;
        int starty = GRID_HEIGHT + 4;

        Rectangle rectangle = new Rectangle(startx, starty, width,
            LEGEND_HEIGHT);
        e.gc.fillRectangle(rectangle);
        e.gc.drawRectangle(rectangle);
        drawTextOnCenter(e.gc, text, rectangle);
    }

}
