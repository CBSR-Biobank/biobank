package edu.ualberta.med.biobank.widgets;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class StorageContainerWidget extends Canvas {

	public static final int DEFAULT_CELL_WIDTH = 40;

	public static final int DEFAULT_CELL_HEIGHT = 40;

	protected int cellWidth;

	protected int cellHeight;

	protected int gridWidth;
	protected int gridHeight;

	protected int width;
	protected int height;

	protected int rows;

	protected int columns;

	protected Object firstRowSign = 'A';
	protected Object firstColSign = 1;

	private int[] selectedBox;

	/**
	 * max width this container will have : use to calculate cells width
	 */
	private Integer maxWidth;

	/**
	 * max height this container will have : use to calculate cells height
	 */
	private Integer maxHeight;

	public StorageContainerWidget(Composite parent) {
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
		// Object currentRowSign = firstRowSign;
		for (int indexRow = 0; indexRow < rows; indexRow++) {
			// Object currentColSign = firstColSign;
			for (int indexCol = 0; indexCol < columns; indexCol++) {
				int xPosition = cellWidth * indexCol;
				int yPosition = cellHeight * indexRow;
				Rectangle rectangle = new Rectangle(xPosition, yPosition,
					cellWidth, cellHeight);
				optionalDrawing(e, indexRow, indexCol, rectangle);
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawRectangle(rectangle);
				String text = getTextForBox(indexRow, indexCol);
				// if (text == null) {
				// text = String.valueOf(currentRowSign)
				// + String.valueOf(currentColSign);
				// }
				drawTextOnCenter(gc, text, rectangle);

				// currentColSign = next(currentColSign);
			}
			// currentRowSign = next(currentRowSign);
		}
	}

	private Object add(Object o, int addValue) {
		if (o instanceof Integer) {
			return ((Integer) o) + addValue;
		} else if (o instanceof Character) {
			return (char) (((Character) o).charValue() + addValue);
		}
		return null;
	}

	protected String getTextForBox(int indexRow, int indexCol) {
		String text = "";
		if (firstRowSign != null) {
			Object total = add(firstRowSign, indexRow);
			if (total instanceof Number) {
				DecimalFormat df1 = new DecimalFormat("00");
				text += df1.format(total);

			} else {
				text += String.valueOf(total);
			}
		}
		if (firstColSign != null) {
			text += String.valueOf(add(firstColSign, indexCol));

		}
		return text;
	}

	protected void optionalDrawing(PaintEvent e, int indexRow, int indexCol,
			Rectangle rectangle) {
		if (selectedBox != null && selectedBox[0] == indexRow
				&& selectedBox[1] == indexCol) {
			Color color = e.display.getSystemColor(SWT.COLOR_BLUE);
			e.gc.setBackground(color);
			e.gc.fillRectangle(rectangle);
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

	public void setStorageSize(int rows, int columns, int maxWidth,
			int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		setStorageSize(rows, columns, true);
	}

	public void setStorageSize(int rows, int columns,
			boolean recalculateCellSizes) {
		this.rows = rows;
		this.columns = columns;
		calculateSizes(recalculateCellSizes);
		redraw();
	}

	protected void calculateSizes(boolean recalculateCellSizes) {
		if (recalculateCellSizes) {
			cellWidth = maxWidth / columns;
			cellHeight = maxHeight / rows;
		}
		gridWidth = cellWidth * columns;
		gridHeight = cellHeight * rows;
		width = gridWidth + 10;
		height = gridHeight + 10;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setSelectedBox(int[] selectedBox) {
		this.selectedBox = selectedBox;
		redraw();
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
