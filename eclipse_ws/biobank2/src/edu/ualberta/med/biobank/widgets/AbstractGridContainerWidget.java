package edu.ualberta.med.biobank.widgets;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

	protected int gridWidth;

	protected int gridHeight;

	private int rows;

	private int columns;

	/**
	 * First character or int used for the cells row labels
	 */
	private Object firstRowSign = 'A';

	/**
	 * First character or int used for the cells column labels
	 */
	private Object firstColSign = 1;

	/**
	 * If yes, the first label in the box will be the column
	 */
	private boolean showColumnFirst = false;

	/**
	 * max width this container will have : used to calculate cells width
	 */
	private int maxWidth = -1;

	/**
	 * max height this container will have : used to calculate cells height
	 */
	private int maxHeight = -1;

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
		for (int indexRow = 0; indexRow < rows; indexRow++) {
			for (int indexCol = 0; indexCol < columns; indexCol++) {
				int xPosition = cellWidth * indexCol;
				int yPosition = cellHeight * indexRow;
				Rectangle rectangle = new Rectangle(xPosition, yPosition,
					cellWidth, cellHeight);
				drawRectangle(e, rectangle, indexRow, indexCol);
				String text = getTextForBox(indexRow, indexCol);
				if (text != null) {
					drawTextOnCenter(e, text, rectangle);
				}
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
		return new Point(gridWidth + 10, gridHeight + 10);
	}

	@SuppressWarnings("unused")
	protected void drawRectangle(PaintEvent e, Rectangle rectangle,
			int indexRow, int indexCol) {
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.drawRectangle(rectangle);
	}

	/**
	 * Get the text to write inside the cell. This default implementation use
	 * the row sign, the column sign and the cell position.
	 */
	protected String getTextForBox(int indexRow, int indexCol) {
		String row = getValueForCell(firstRowSign, indexRow,
			firstColSign == null);
		String col = getValueForCell(firstColSign, indexCol,
			firstRowSign == null);
		if (showColumnFirst) {
			return col + row;
		}
		return row + col;
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

	/**
	 * Draw the text on the middle of the rectangle
	 */
	public void drawTextOnCenter(PaintEvent e, String text, Rectangle rectangle) {
		Font oldFont = e.gc.getFont();
		Point textSize = e.gc.textExtent(text);
		if (textSize.x > rectangle.width) {
			// Try to find a smallest font to see the whole text
			FontData fd = oldFont.getFontData()[0];
			int height = fd.getHeight();
			Point currentTextSize = textSize;
			while (currentTextSize.x > rectangle.width && height > 3) {
				height--;
				FontData fd2 = new FontData(fd.getName(), height, fd.getStyle());
				e.gc.setFont(new Font(e.display, fd2));
				currentTextSize = e.gc.textExtent(text);
			}
			if (height > 3) {
				textSize = currentTextSize;
			} else {
				e.gc.setFont(oldFont);
			}
		}
		int xTextPosition = (rectangle.width - textSize.x) / 2 + rectangle.x;
		int yTextPosition = (rectangle.height - textSize.y) / 2 + rectangle.y;
		e.gc.drawText(text, xTextPosition, yTextPosition, true);
		e.gc.setFont(oldFont);
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
		redraw();
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

	public void setFirstRowSign(Object rowSign) {
		this.firstRowSign = rowSign;
	}

	public void setFirstColSign(Object colSign) {
		this.firstColSign = colSign;
	}

	public void setShowColumnFirst(boolean showColumnFirst) {
		this.showColumnFirst = showColumnFirst;
	}
}
