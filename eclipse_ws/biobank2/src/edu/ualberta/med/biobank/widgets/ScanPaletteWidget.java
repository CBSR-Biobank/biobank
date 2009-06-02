package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ScanCell;

/**
 * Specific widget to draw a palette
 */
public class ScanPaletteWidget extends AbstractGridContainerWidget {

	public static final int SAMPLE_WIDTH = 40;

	/**
	 * Palettes are always 8*12 = fix size
	 */
	public static final int PALETTE_WIDTH = SAMPLE_WIDTH * ScanCell.COL_MAX;
	public static final int PALETTE_HEIGHT = SAMPLE_WIDTH * ScanCell.ROW_MAX;

	public static final int LEGEND_TOTAL = 5;
	public static final int LEGEND_HEIGHT = 20;
	public static final int LEGEND_WIDTH = PALETTE_WIDTH / LEGEND_TOTAL;

	public static final int PALETTE_HEIGHT_AND_LEGEND = PALETTE_HEIGHT
			+ LEGEND_HEIGHT + 4;

	private ScanCell[][] scannedElements;

	private boolean showLegend;

	private static final int EMPTY_COLOR = SWT.COLOR_WHITE;
	private static final int FILLED_COLOR = SWT.COLOR_DARK_GRAY;
	private static final int NEW_COLOR = SWT.COLOR_DARK_GREEN;
	private static final int MISSING_COLOR = SWT.COLOR_CYAN;
	private static final int ERROR_COLOR = SWT.COLOR_YELLOW;

	public ScanPaletteWidget(Composite parent, boolean showLegend) {
		super(parent);
		this.showLegend = showLegend;
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				ScanCell cell = getCellAtCoordinates(e.x, e.y);
				if (cell != null) {
					String msg = cell.getValue();
					if (cell.getInformation() != null) {
						msg += " : " + cell.getInformation();
					}
					setToolTipText(msg);
				} else {
					setToolTipText(null);
				}
			}
		});
		setCellWidth(SAMPLE_WIDTH);
		setCellHeight(SAMPLE_WIDTH);
		setStorageSize(ScanCell.ROW_MAX, ScanCell.COL_MAX);
	}

	public ScanPaletteWidget(Composite parent) {
		this(parent, true);
	}

	@Override
	protected void paintPalette(PaintEvent e) {
		Font font = new Font(e.display, "Sans", 8, SWT.NORMAL);
		e.gc.setFont(font);
		super.paintPalette(e);
		if (showLegend) {
			drawLegend(e, EMPTY_COLOR, 0, "Empty");
			drawLegend(e, NEW_COLOR, 1, "New");
			drawLegend(e, FILLED_COLOR, 2, "Filled");
			drawLegend(e, MISSING_COLOR, 3, "Missing");
			drawLegend(e, ERROR_COLOR, 4, "Error");
			// Should Modify LEGEND_TOTAL if add a new legend
		}
	}

	@Override
	protected String getTextForBox(int indexRow, int indexCol) {
		if (scannedElements != null
				&& scannedElements[indexRow][indexCol] != null) {
			String title = scannedElements[indexRow][indexCol].getTitle();
			if (title != null) {
				return scannedElements[indexRow][indexCol].getTitle();
			}
		}
		return super.getTextForBox(indexRow, indexCol);
	}

	@Override
	protected void specificDrawing(PaintEvent e, int indexRow, int indexCol,
			Rectangle rectangle) {
		if (scannedElements != null
				&& scannedElements[indexRow][indexCol] != null
				&& scannedElements[indexRow][indexCol].getStatus() != null) {
			Color color;
			switch (scannedElements[indexRow][indexCol].getStatus()) {
			case ERROR:
				color = e.display.getSystemColor(ERROR_COLOR);
				break;
			case FILLED:
				color = e.display.getSystemColor(FILLED_COLOR);
				break;
			case MISSING:
				color = e.display.getSystemColor(MISSING_COLOR);
				break;
			case NEW:
				color = e.display.getSystemColor(NEW_COLOR);
				break;
			default:
				color = e.display.getSystemColor(EMPTY_COLOR);
			}
			e.gc.setBackground(color);
			e.gc.fillRectangle(rectangle);
		}
	}

	private void drawLegend(PaintEvent e, int color, int index, String text) {
		e.gc.setBackground(e.display.getSystemColor(color));
		Rectangle rectangle = new Rectangle(LEGEND_WIDTH * index,
			getGridHeight() + 4, LEGEND_WIDTH, LEGEND_HEIGHT);
		e.gc.fillRectangle(rectangle);
		e.gc.drawRectangle(rectangle);
		drawTextOnCenter(e.gc, text, rectangle);
	}

	public void setScannedElements(ScanCell[][] randomScan) {
		this.scannedElements = randomScan;
		redraw();
	}

	public ScanCell getCellAtCoordinates(int xPosition, int yPosition) {
		if (scannedElements == null) {
			return null;
		}
		int col = xPosition / getCellWidth();
		int row = yPosition / getCellHeight();
		if (col >= 0 && col < ScanCell.COL_MAX && row >= 0
				&& row < ScanCell.ROW_MAX) {
			return scannedElements[row][col];
		}
		return null;
	}

	@Override
	protected void calculateSizes() {
		super.calculateSizes();
		setHeight(getHeight() + LEGEND_HEIGHT + 4);
	}
}
