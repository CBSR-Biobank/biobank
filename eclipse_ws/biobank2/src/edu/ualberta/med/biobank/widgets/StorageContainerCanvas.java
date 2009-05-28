package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class StorageContainerCanvas extends Canvas {
	protected int cellWidth;

	protected int cellHeight;

	protected int gridWidth;
	protected int gridHeight;

	protected int width;
	protected int height;

	protected int rows;

	protected int columns;

	private int[] selectedBox;

	public StorageContainerCanvas(Composite parent) {
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
		char letter = 'A';
		for (int indexRow = 0; indexRow < rows; indexRow++) {
			String currentLetter = String.valueOf(letter);
			for (int indexCol = 0; indexCol < columns; indexCol++) {
				int xPosition = cellWidth * indexCol;
				int yPosition = cellHeight * indexRow;
				Rectangle rectangle = new Rectangle(xPosition, yPosition,
					cellWidth, cellHeight);
				optionalDrawing(e, indexRow, indexCol, rectangle);
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawRectangle(rectangle);
				drawTextOnCenter(gc, currentLetter + (indexCol + 1), rectangle);
			}
			letter += 1;
		}
	}

	protected void optionalDrawing(PaintEvent e, int indexRow, int indexCol,
			Rectangle rectangle) {
		if (selectedBox != null && selectedBox[0] == indexRow
				&& selectedBox[1] == indexCol) {
			Color color = e.display.getSystemColor(SWT.COLOR_DARK_BLUE);
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

	public void setStorageSize(int rows, int columns, int cellWidth,
			int cellHeight) {
		this.rows = rows;
		this.columns = columns;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		calculateSizes();
		redraw();
	}

	public void setStorageSize(int rows, int columns) {
		setStorageSize(rows, columns, 40, 40);
	}

	protected void calculateSizes() {
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

}
