package edu.ualberta.med.biobank.widgets.grids;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

/**
 * Draw a grid according to specific parameters : total number of rows, total number of columns,
 * width and height of the cells
 */
public abstract class AbstractGridDisplay extends AbstractContainerDisplay {

    private int cellWidth = 60;

    private int cellHeight = 60;

    protected int gridWidth;

    protected int gridHeight;

    private int rows;

    private int columns;

    /**
     * Height used when legend in under the grid
     */
    public static final int LEGEND_HEIGHT = 20;

    /**
     * width calculated when legend in under the grid
     */
    protected int legendWidth;

    /**
     * Width used when legend is on the side of the grid
     */
    public static final int LEGEND_WIDTH = 70;

    public boolean legendOnSide = false;

    @Override
    protected void paintGrid(PaintEvent e, ContainerDisplayWidget displayWidget) {
        for (int indexRow = 0; indexRow < rows; indexRow++) {
            for (int indexCol = 0; indexCol < columns; indexCol++) {
                int xPosition = cellWidth * indexCol;
                int yPosition = cellHeight * indexRow;
                Rectangle rectangle = new Rectangle(xPosition, yPosition,
                    cellWidth, cellHeight);

                Color defaultColor = getDefaultBackgroundColor(e, displayWidget, rectangle,
                    indexRow, indexCol);
                drawRectangle(e, displayWidget, rectangle, indexRow, indexCol, defaultColor);
                String topText = getTopTextForBox(displayWidget.getCells(), indexRow, indexCol);
                if (topText != null) {
                    drawText(e, topText, rectangle, SWT.TOP);
                }
                String middleText = getMiddleTextForBox(displayWidget.getCells(), indexRow, indexCol);
                if (middleText != null) {
                    drawText(e, middleText, rectangle, SWT.CENTER);
                }
                String bottomText = getBottomTextForBox(
                    displayWidget.getCells(), indexRow, indexCol);
                if (bottomText != null) {
                    drawText(e, bottomText, rectangle, SWT.BOTTOM);
                }

            }
        }
        if (legendStatus != null) {
            legendWidth = gridWidth / legendStatus.size();
            for (int i = 0; i < legendStatus.size(); i++) {
                UICellStatus status = legendStatus.get(i);
                drawLegend(e, status.getColor(), i, status.getLegend());
            }
        }
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        if (maxWidth != -1 && maxHeight != -1) {
            cellWidth = maxWidth / columns;
            cellHeight = maxHeight / rows;
            gridWidth = maxWidth;
            gridHeight = maxHeight;
        } else {
            gridWidth = cellWidth * columns;
            gridHeight = cellHeight * rows;
        }
        int width = gridWidth + 10;
        int height = gridHeight + 10;
        if (legendStatus != null) {
            if (legendOnSide) {
                width = width + LEGEND_WIDTH + 4;
            } else {
                height = height + LEGEND_HEIGHT + 4;
            }
        }
        return new Point(width, height);
    }

    // @Override
    // public void initLegend(List<UICellStatus> status) {
    // super.initLegend(status);
    // hasLegend = status != null && status.size() > 0;
    // }

    protected void drawRectangle(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol, Color defaultBackgroundColor) {
        Color backgroundColor = defaultBackgroundColor;
        if (displayWidget.getSelection() != null
            && displayWidget.getSelection().getRow() != null
            && displayWidget.getSelection().getRow() == indexRow
            && displayWidget.getSelection().getCol() != null
            && displayWidget.getSelection().getCol() == indexCol) {
            backgroundColor = e.display.getSystemColor(SWT.COLOR_YELLOW);
        }
        e.gc.setBackground(backgroundColor);
        e.gc.fillRectangle(rectangle);
        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
        e.gc.drawRectangle(rectangle);
        if (displayWidget.getCells() != null) {
            if (displayWidget.getMultiSelectionManager().isEnabled()) {
                AbstractUIWell cell = displayWidget.getCells().get(
                    new RowColPos(indexRow, indexCol));
                if (cell != null && cell.isSelected()) {
                    Rectangle rect = new Rectangle(rectangle.x + 5,
                        rectangle.y + 5, rectangle.width - 10,
                        rectangle.height - 10);
                    Color color = e.display.getSystemColor(SWT.COLOR_BLUE);
                    e.gc.setForeground(color);
                    e.gc.drawRectangle(rect);
                }
            }
        }
        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));

    }

    @SuppressWarnings("unused")
    protected Color getDefaultBackgroundColor(PaintEvent e,
        ContainerDisplayWidget displayWidget, Rectangle rectangle,
        int indexRow, int indexCol) {
        return UICellStatus.EMPTY.getColor();
    }

    @SuppressWarnings("unused")
    protected String getTopTextForBox(
        Map<RowColPos, ? extends AbstractUIWell> cells, int indexRow,
        int indexCol) {
        return null;
    }

    protected String getMiddleTextForBox(
        Map<RowColPos, ? extends AbstractUIWell> cells, int indexRow,
        int indexCol) {
        return getDefaultTextForBox(cells, indexRow, indexCol);
    }

    @SuppressWarnings("unused")
    protected String getBottomTextForBox(
        Map<RowColPos, ? extends AbstractUIWell> cells, int indexRow,
        int indexCol) {
        return null;
    }

    @Override
    public void setContainerType(ContainerType type) {
        super.setContainerType(type);
        Integer rowCap = containerType.getRowCapacity();
        Integer colCap = containerType.getColCapacity();
        Assert.isNotNull(rowCap, "row capacity is null"); //$NON-NLS-1$
        Assert.isNotNull(colCap, "column capacity is null"); //$NON-NLS-1$
        setStorageSize(rowCap, colCap);
        if (colCap <= 1) {
            // single dimension size
            setCellWidth(120);
            setCellHeight(20);
            setLegendOnSide(true);
        }
    }

    /**
     * Draw the text on the horizontal middle of the rectangle. Vertical alignment depend on the
     * verticalPosition parameter.
     */
    private void drawText(PaintEvent e, String text, Rectangle rectangle,
        int verticalPosition) {
        Font oldFont = e.gc.getFont();
        Font tmpFont = null;
        Point textSize = e.gc.textExtent(text);
        if (textSize.x > rectangle.width) {
            // Try to find a smallest font to see the whole text
            FontData fd = oldFont.getFontData()[0];
            int height = fd.getHeight();
            Point currentTextSize = textSize;
            while (currentTextSize.x > rectangle.width && height > 3) {
                if (tmpFont != null) {
                    tmpFont.dispose();
                }
                height--;
                FontData fd2 =
                    new FontData(fd.getName(), height, fd.getStyle());
                tmpFont = new Font(e.display, fd2);
                e.gc.setFont(tmpFont);
                currentTextSize = e.gc.textExtent(text);
            }
            if (height > 3) {
                textSize = currentTextSize;
            } else {
                e.gc.setFont(oldFont);
            }
        }
        int xTextPosition = (rectangle.width - textSize.x) / 2 + rectangle.x;
        int yTextPosition = 0;
        switch (verticalPosition) {
        case SWT.CENTER:
            yTextPosition = (rectangle.height - textSize.y) / 2 + rectangle.y;
            break;
        case SWT.TOP:
            yTextPosition = rectangle.y + 3;
            break;
        case SWT.BOTTOM:
            yTextPosition = rectangle.y + rectangle.height - textSize.y - 3;
        }
        e.gc.drawText(text, xTextPosition, yTextPosition, true);
        e.gc.setFont(oldFont);
        if (tmpFont != null) {
            tmpFont.dispose();
        }
    }

    protected void drawLegend(PaintEvent e, Color color, int index, String text) {
        e.gc.setBackground(color);
        int width = legendWidth;
        int startx = legendWidth * index;
        int starty = gridHeight + 4;
        if (legendOnSide) {
            width = LEGEND_WIDTH;
            startx = gridWidth + 4;
            starty = LEGEND_HEIGHT * index;
        }
        Rectangle rectangle = new Rectangle(startx, starty, width,
            LEGEND_HEIGHT);
        e.gc.fillRectangle(rectangle);
        e.gc.drawRectangle(rectangle);
        drawText(e, text, rectangle, SWT.CENTER);
    }

    /**
     * Modify only the number of rows and columns of the grid. If no max width and max height has
     * been given to the grid, the default cell width and cell height will be used
     */
    @Override
    public void setStorageSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public void setLegendOnSide(boolean onSide) {
        this.legendOnSide = onSide;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return columns;
    }

    @Override
    public RowColPos getPositionAtCoordinates(int x, int y) {
        int col = x / getCellWidth();
        int row = y / getCellHeight();
        if (col >= 0 && col < getCols() && row >= 0 && row < getRows()) {
            return new RowColPos(row, col);
        }
        return null;
    }

}
