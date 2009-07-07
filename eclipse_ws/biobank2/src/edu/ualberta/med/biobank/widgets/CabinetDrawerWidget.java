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

	private int selectedBin;

	public CabinetDrawerWidget(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				paintPalette(e);
			}
		});
	}

	protected void paintPalette(PaintEvent e) {
		setSize(WIDTH, HEIGHT);
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
			gc.drawRectangle(rectangle);
			if (selectedBin == boxIndex) {
				gc.setBackground(new Color(e.display, 123, 11, 25));
				gc.fillRectangle(rectangle);
			}
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

}
