package edu.ualberta.med.biobank.widgets;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Draw a grid according to specific parameters : total number of rows, total
 * number of columns, width and height of the cells
 */
public abstract class AbstractGridContainerWidget extends Canvas {

	private int cellWidth = 40;

	private int cellHeight = 40;

	/**
	 * Width the grid only. Calculated via method calculateSizes()
	 */
	private int gridWidth;

	/**
	 * Height the grid only. Calculated via method calculateSizes()
	 */
	private int gridHeight;

	/**
	 * Total width of the canvas. Calculated via method calculateSizes()
	 */
	private int width;

	/**
	 * Total height of the canvas. Calculated via method calculateSizes()
	 */
	private int height;

	private int rows;

	private int columns;

	/**
	 * First character or int used for the cells row labels
	 */
	protected Object firstRowSign = 'A';

	/**
	 * First character or int used for the cells column labels
	 */
	protected Object firstColSign = 1;

	/**
	 * max width this container will have : used to calculate cells width
	 */
	private Integer maxWidth = -1;

	/**
	 * max height this container will have : used to calculate cells height
	 */
	private Integer maxHeight = -1;

	public AbstractGridContainerWidget(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				paintPalette(e);
			}
		});
	}

	protected void paintPalette(PaintEvent e) {
		setSize(width, height);
		GC gc = e.gc;
		for (int indexRow = 0; indexRow < rows; indexRow++) {
			for (int indexCol = 0; indexCol < columns; indexCol++) {
				int xPosition = cellWidth * indexCol;
				int yPosition = cellHeight * indexRow;
				Rectangle rectangle = new Rectangle(xPosition, yPosition,
					cellWidth, cellHeight);
				specificDrawing(e, indexRow, indexCol, rectangle);
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawRectangle(rectangle);
				String text = getTextForBox(indexRow, indexCol);
				if (text != null) {
					drawTextOnCenter(gc, text, rectangle);
				}
			}
		}
	}

	/**
	 * Get the text to write inside the cell. This default implementation use
	 * the row sign, the column sign and the cell position.
	 */
	protected String getTextForBox(int indexRow, int indexCol) {
		String text = getValueForCell(firstRowSign, indexRow,
			firstColSign == null);
		text += getValueForCell(firstColSign, indexCol, firstRowSign == null);
		return text;
	}

	private String getValueForCell(Object firstSign, int addValue,
			boolean format) {
		if (firstSign != null) {
			Object total = null;
			if (firstSign instanceof Integer) {
				total = ((Integer) firstSign) + addValue;
				if (format) {
					DecimalFormat df1 = new DecimalFormat("00");
					return df1.format(total);
				}
				return String.valueOf(total);
			} else if (firstSign instanceof Character) {
				total = (char) (((Character) firstSign).charValue() + addValue);
				return String.valueOf(total);
			}
		}
		return "";
	}

	protected abstract void specificDrawing(PaintEvent e, int indexRow,
			int indexCol, Rectangle rectangle);

	/**
	 * Draw the text on the middle of the rectangle
	 */
	public void drawTextOnCenter(GC gc, String text, Rectangle rectangle) {
		Point textSize = gc.textExtent(text);
		int xTextPosition = (rectangle.width - textSize.x) / 2 + rectangle.x;
		int yTextPosition = (rectangle.height - textSize.y) / 2 + rectangle.y;
		gc.drawText(text, xTextPosition, yTextPosition, true);
	}

	/**
	 * Modify dimensions of the grid. maxWidth and maxHeight are used to
	 * calculate the size of the cells
	 * 
	 * @param rows total number of rows
	 * @param columns total number of columns
	 * @param maxWidth max width the grid should have
	 * @param maxHeight max height the grid should have
	 */
	public void setGridSizes(int rows, int columns, int maxWidth, int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		setStorageSize(rows, columns);
	}

	/**
	 * Modify only the number of rows and columns of the grid. If no max width
	 * and max height has been given to the grid, the default cell width and
	 * cell height will be used
	 */
	public void setStorageSize(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		calculateSizes();
		redraw();
	}

	protected void calculateSizes() {
		if (maxWidth != -1 && maxHeight != -1) {
			cellWidth = maxWidth / columns;
			cellHeight = maxHeight / rows;
		}
		gridWidth = cellWidth * columns;
		gridHeight = cellHeight * rows;
		width = gridWidth + 10;
		height = gridHeight + 10;
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setFirstRowSign(Object rowSign) {
		this.firstRowSign = rowSign;
	}

	public void setFirstColSign(Object colSign) {
		this.firstColSign = colSign;
	}
}
