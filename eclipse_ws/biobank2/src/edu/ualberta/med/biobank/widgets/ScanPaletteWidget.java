package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ScanCell;

public class ScanPaletteWidget extends Canvas {
	
	public static final int SAMPLE_WIDTH = 40;
	
	public static final int LEGEND_HEIGHT = 20;
	public static final int LEGEND_WIDTH = 70;
	
	public static final int GRID_WIDTH = SAMPLE_WIDTH * ScanCell.COL_MAX;	
	public static final int GRID_HEIGHT = SAMPLE_WIDTH * ScanCell.ROW_MAX;
	
	public static final int WIDTH = GRID_WIDTH + LEGEND_WIDTH + 10;
	public static final int HEIGHT = GRID_HEIGHT + 15;
//	public static final int WIDTH = GRID_WIDTH + 10;
//	public static final int HEIGHT = GRID_HEIGHT + LEGEND_HEIGHT + 15;
	
	private ScanCell[][] scannedElements;
	
	private static final int EMPTY_COLOR = SWT.COLOR_WHITE;
	private static final int FILLED_COLOR = SWT.COLOR_DARK_GRAY;
	private static final int NEW_COLOR = SWT.COLOR_DARK_GREEN;
	private static final int MISSING_COLOR = SWT.COLOR_CYAN;
	private static final int ERROR_COLOR = SWT.COLOR_YELLOW;	
	
	public ScanPaletteWidget(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);		
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {				
				paintPalette(e);
			}
		});
		
		addMouseListener(new MouseListener(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {				
				System.out.println("mouse down: " + e.x + "," + e.y);
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
			
		});
	}


	private void paintPalette(PaintEvent e) {
		setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		Point parentSize = getParent().getSize();
		setSize(parentSize.x, parentSize.y);		
		GC gc = e.gc;
		char letter = 'A';
		for (int indexRow = 0; indexRow < ScanCell.ROW_MAX; indexRow++) {
			String currentLetter = String.valueOf(letter);
			for (int indexCol = 0; indexCol < ScanCell.COL_MAX; indexCol++) {
				int xPosition = SAMPLE_WIDTH * indexCol;
				int yPosition = SAMPLE_WIDTH * indexRow;
				Rectangle rectangle = new Rectangle(xPosition, yPosition, SAMPLE_WIDTH, SAMPLE_WIDTH);
				if (scannedElements != null && scannedElements[indexRow][indexCol] != null) {
					Color color;
					switch (scannedElements[indexRow][indexCol].getStatus()) {
					case ERROR: color = e.display.getSystemColor(ERROR_COLOR); break;
					case FILLED: color = e.display.getSystemColor(FILLED_COLOR); break;
					case MISSING: color = e.display.getSystemColor(MISSING_COLOR); break;
					case NEW: color = e.display.getSystemColor(NEW_COLOR); break;
					default: color = e.display.getSystemColor(EMPTY_COLOR);
					}
					gc.setBackground(color);
					gc.fillRectangle(rectangle);
				}
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawRectangle(rectangle);
				drawTextOnCenter(gc, currentLetter + (indexCol + 1), rectangle);
			}
			letter += 1;
		}
		addLegend(e, gc, EMPTY_COLOR, 0, "Empty");
		addLegend(e, gc, NEW_COLOR, 1, "New");
		addLegend(e, gc, FILLED_COLOR, 2, "Filled");
		addLegend(e, gc, MISSING_COLOR, 3, "Missing");
		addLegend(e, gc, ERROR_COLOR, 4, "Error");
	}


	private void addLegend(PaintEvent e, GC gc, int color, int index, String text) {
		gc.setBackground(e.display.getSystemColor(color));
//		Rectangle rectangle = new Rectangle(LEGEND_WIDTH * index, GRID_HEIGHT + 4, LEGEND_WIDTH, LEGEND_HEIGHT);
		Rectangle rectangle = new Rectangle(GRID_WIDTH + 4, LEGEND_HEIGHT * index, LEGEND_WIDTH, LEGEND_HEIGHT);
		gc.fillRectangle(rectangle);		
		gc.drawRectangle(rectangle);
		drawTextOnCenter(gc, text, rectangle);
	}


	public void setScannedElements(ScanCell[][] randomScan) {
		this.scannedElements = randomScan;		
		redraw();
	}
	
	/**
	 * Draw the text on the middle of the rectangle  
	 */
	public void drawTextOnCenter(GC gc, String text, Rectangle rectangle) {
		Point textSize = gc.textExtent(text);
		int xTextPosition = (rectangle.width - textSize.x)/2 + rectangle.x;
		int yTextPosition = (rectangle.height - textSize.y)/2 + rectangle.y;
		gc.drawText(text, xTextPosition, yTextPosition, true);		
	}

}
