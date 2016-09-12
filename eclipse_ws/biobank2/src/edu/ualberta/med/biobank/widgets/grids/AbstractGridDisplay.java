package edu.ualberta.med.biobank.widgets.grids;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.gui.common.Swt2DUtil;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionManager;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

/**
 * Draw a grid according to specific parameters : total number of rows, total number of columns,
 * width and height of the cells
 */
public abstract class AbstractGridDisplay extends AbstractContainerDisplay {

    private static Logger log = LoggerFactory.getLogger(AbstractGridDisplay.class.getName());

    private static final int IMAGE_BORDER_SIZE = 5;

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

    public AbstractGridDisplay(String name) {
        super(name);
    }

    private Rectangle getCellRectangleTranslated(Rectangle2D.Double cellRect, int row, int col) {
        AffineTransform t = AffineTransform.getTranslateInstance(
            col * cellWidth, row * cellHeight);
        Rectangle2D.Double rectangle = Swt2DUtil.transformRect(t, cellRect);
        Rectangle r = new Rectangle(
            (int) rectangle.x,
            (int) rectangle.y,
            (int) rectangle.width,
            (int) rectangle.height);
        return r;
    }

    @Override
    protected Image updateGridImage(ContainerDisplayWidget displayWidget) {
        Display display = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
        Rectangle clientArea = getGridSize();
        Image image = new Image(display, clientArea.width, clientArea.height);

        Rectangle2D.Double cellRect = new Rectangle2D.Double(
            IMAGE_BORDER_SIZE, IMAGE_BORDER_SIZE, cellWidth, cellHeight);

        RowColPos widgetSelection = displayWidget.getSelection();
        Map<RowColPos, ? extends AbstractUIWell> cells = displayWidget.getCells();
        if (cells == null) {
            cells = new HashMap<RowColPos, AbstractUIWell>(0);
        }

        GC newGC = new GC(image);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                Rectangle r = getCellRectangleTranslated(cellRect, row, col);

                Color defaultColor = getDefaultBackgroundColor(display, cells, r, row, col);

                drawRectangle(
                    display,
                    newGC,
                    r,
                    row,
                    col,
                    defaultColor,
                    widgetSelection);

                String topText = getTopTextForBox(cells, row, col);
                if (topText != null) {
                    drawText(display, newGC, topText, r, SWT.TOP);
                }
                String middleText = getMiddleTextForBox(cells, row, col);
                if (middleText != null) {
                    drawText(display, newGC, middleText, r, SWT.CENTER);
                }
                String bottomText = getBottomTextForBox(cells, row, col);
                if (bottomText != null) {
                    drawText(display, newGC, bottomText, r, SWT.BOTTOM);
                }
            }
        }

        MultiSelectionManager multiSelectionManager = displayWidget.getMultiSelectionManager();
        if (multiSelectionManager.isEnabled()) {
            Collection<AbstractUIWell> selectedCells = multiSelectionManager.getSelectedCells();
            if (!selectedCells.isEmpty()) {
                drawCellSelections(display, newGC, selectedCells, cellRect);
            }
        }

        if (legendStatus != null) {
            legendWidth = gridWidth / legendStatus.size();
            for (int i = 0; i < legendStatus.size(); i++) {
                UICellStatus status = legendStatus.get(i);
                drawLegend(display, newGC, status.getColor(), i, status.getLegend());
            }
        }
        newGC.dispose();
        return image;
    }

    @SuppressWarnings("nls")
    @Override
    protected Rectangle getGridSize() {
        int width;
        int height;

        if ((maxWidth >= 0) && (maxHeight >= 0)) {
            width = maxWidth;
            height = maxHeight;

            cellWidth = maxWidth / columns;
            cellHeight = maxHeight / rows;
            log.trace("getClientArea: cellWidth and cellHeight dependent on max width and height");
        } else {
            width = cellWidth * columns;
            height = cellHeight * rows;
            log.trace("getClientArea: width and height dependent on default cell width and height");
        }

        log.trace("getClientArea: rows: {}, columns: {}", rows, columns);
        log.trace("getClientArea: cellWidth: {}, cellHeight: {}", cellWidth, cellHeight);

        gridWidth = width;
        gridHeight = height;

        width += 2 * IMAGE_BORDER_SIZE;
        height += 2 * IMAGE_BORDER_SIZE;

        if (legendStatus != null) {
            if (legendOnSide) {
                width += LEGEND_WIDTH + IMAGE_BORDER_SIZE;
            } else {
                height += LEGEND_HEIGHT + IMAGE_BORDER_SIZE;
            }
        }

        log.trace("getClientArea: width: {}, height: {}", width, height);
        return new Rectangle(0, 0, width, height);

    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Rectangle clientArea = getGridSize();
        return new Point(clientArea.width, clientArea.height);
    }

    @SuppressWarnings("nls")
    protected void drawRectangle(
        Display display,
        GC gc,
        Rectangle rectangle,
        int indexRow,
        int indexCol,
        Color defaultBackgroundColor,
        RowColPos selection) {

        Color backgroundColor = defaultBackgroundColor;
        if (selection != null) {
            Integer selectionRow = selection.getRow();
            Integer selectionCol = selection.getCol();

            if ((selectionRow == null) || (selectionCol == null)) {
                throw new IllegalArgumentException("selection row or column is null");
            }

            if ((selectionRow == indexRow) && (selectionCol == indexCol)) {
                backgroundColor = display.getSystemColor(SWT.COLOR_YELLOW);
            }
        }
        gc.setBackground(backgroundColor);
        gc.fillRectangle(rectangle);
        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.drawRectangle(rectangle);
    }

    @SuppressWarnings("nls")
    private void drawCellSelections(
        Display display,
        GC gc,
        Collection<AbstractUIWell> cells,
        Rectangle2D.Double cellRect) {

        if (cells.isEmpty()) {
            throw new IllegalStateException("cells are empty");
        }

        Rectangle2D.Double selectionRect = new Rectangle2D.Double(
            5, 5, cellRect.width - 10, cellRect.height - 10);

        for (AbstractUIWell cell : cells) {
            Integer row = cell.getRow();
            Integer col = cell.getCol();

            if (!cell.isSelected()) {
                throw new IllegalStateException("cell is not selected: " + row + ", " + col);
            }

            AffineTransform t = AffineTransform.getTranslateInstance(
                col * cellWidth + IMAGE_BORDER_SIZE, row * cellHeight + IMAGE_BORDER_SIZE);
            Rectangle2D.Double selectionRectCell = Swt2DUtil.transformRect(t, selectionRect);

            Color color = display.getSystemColor(SWT.COLOR_BLUE);
            gc.setForeground(color);
            gc.drawRectangle(
                (int) selectionRectCell.x,
                (int) selectionRectCell.y,
                (int) selectionRectCell.width,
                (int) selectionRectCell.height);
        }
        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    }

    @SuppressWarnings("unused")
    protected Color getDefaultBackgroundColor(
        Display display,
        Map<RowColPos, ? extends AbstractUIWell> cells,
        Rectangle rectangle,
        int indexRow,
        int indexCol) {
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

    private void drawText(
        Display display,
        GC gc,
        String text,
        Rectangle rectangle,
        int verticalPosition) {
        Font oldFont = gc.getFont();
        Font tmpFont = null;
        Point textSize = gc.textExtent(text);
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
                tmpFont = new Font(display, fd2);
                gc.setFont(tmpFont);
                currentTextSize = gc.textExtent(text);
            }
            if (height > 3) {
                textSize = currentTextSize;
            } else {
                gc.setFont(oldFont);
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
        gc.drawText(text, xTextPosition, yTextPosition, true);
        gc.setFont(oldFont);
        if (tmpFont != null) {
            tmpFont.dispose();
        }
    }

    protected void drawLegend(Display display, GC gc, Color color, int index, String text) {
        gc.setBackground(color);
        int width = legendWidth;
        int startx = legendWidth * index + IMAGE_BORDER_SIZE;
        int starty = gridHeight + 5;
        if (legendOnSide) {
            width = LEGEND_WIDTH;
            startx = gridWidth + 2 * IMAGE_BORDER_SIZE;
            starty = LEGEND_HEIGHT * index + IMAGE_BORDER_SIZE;
        }
        Rectangle rectangle = new Rectangle(startx, starty, width, LEGEND_HEIGHT);
        gc.fillRectangle(rectangle);
        gc.drawRectangle(rectangle);
        drawText(display, gc, text, rectangle, SWT.CENTER);
    }

    /**
     * Modify only the number of rows and columns of the grid. If no max width and max height has
     * been given to the grid, the default cell width and cell height will be used
     */
    @Override
    public void setStorageSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        if (columns <= 1) {
            // single dimension size
            setCellWidth(120);
            setCellHeight(30);
            setLegendOnSide(true);
        }
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

    @SuppressWarnings("nls")
    @Override
    public RowColPos getPositionAtCoordinates(int x, int y) {
        if ((x >= 0) && (y >= 0)) {
            int row = y / getCellHeight();
            int col = x / getCellWidth();
            log.trace("getPositionAtCoordinates: row: {}, col: {}", row, col);
            if ((row >= 0) && (row < getRows()) && (col >= 0) && (col < getCols())) {
                return new RowColPos(row, col);
            }
        }
        return null;
    }

}
