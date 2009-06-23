package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.ScanCell;
import edu.ualberta.med.biobank.widgets.listener.ScanPaletteModificationEvent;
import edu.ualberta.med.biobank.widgets.listener.ScanPaletteModificationListener;

/**
 * Specific widget to draw a palette
 */
public class ScanPaletteWidget extends AbstractGridContainerWidget {

	public static final int SAMPLE_WIDTH = 50;

	/**
	 * Palettes are always 8*12 = fix size
	 */
	public static final int PALETTE_WIDTH = SAMPLE_WIDTH * ScanCell.COL_MAX;
	public static final int PALETTE_HEIGHT = SAMPLE_WIDTH * ScanCell.ROW_MAX;

	protected List<SampleCellStatus> statusAvailable;

	public static final int PALETTE_HEIGHT_AND_LEGEND = PALETTE_HEIGHT
			+ LEGEND_HEIGHT + 4;

	protected ScanCell[][] scannedElements;

	List<ScanPaletteModificationListener> listeners;

	public ScanPaletteWidget(Composite parent) {
		super(parent);
		listeners = new ArrayList<ScanPaletteModificationListener>();
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
		initLegend();
	}

	protected void initLegend() {
		hasLegend = true;
		statusAvailable = new ArrayList<SampleCellStatus>();
		statusAvailable.add(SampleCellStatus.EMPTY);
		statusAvailable.add(SampleCellStatus.NEW);
		statusAvailable.add(SampleCellStatus.FILLED);
		statusAvailable.add(SampleCellStatus.MISSING);
		statusAvailable.add(SampleCellStatus.ERROR);
		legendWidth = PALETTE_WIDTH / statusAvailable.size();
	}

	@Override
	protected void paintPalette(PaintEvent e) {
		FontData fd = e.gc.getFont().getFontData()[0];
		FontData fd2 = new FontData(fd.getName(), 8, fd.getStyle());
		e.gc.setFont(new Font(e.display, fd2));
		super.paintPalette(e);
		for (int i = 0; i < statusAvailable.size(); i++) {
			SampleCellStatus status = statusAvailable.get(i);
			drawLegend(e, status.getColor(), i, status.getLegend());
		}
	}

	@Override
	protected String getTextForBox(int indexRow, int indexCol) {
		if (scannedElements != null
				&& scannedElements[indexRow][indexCol] != null) {
			String title = scannedElements[indexRow][indexCol].getTitle();
			if (title != null) {
				return title;
			}
		}
		return super.getTextForBox(indexRow, indexCol);
	}

	@Override
	protected void drawRectangle(PaintEvent e, Rectangle rectangle,
			int indexRow, int indexCol) {
		if (scannedElements != null
				&& scannedElements[indexRow][indexCol] != null
				&& scannedElements[indexRow][indexCol].getStatus() != null) {
			Color color = scannedElements[indexRow][indexCol].getStatus()
					.getColor();
			e.gc.setBackground(color);
			e.gc.fillRectangle(rectangle);
		}
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.drawRectangle(rectangle);
	}

	public void setScannedElements(ScanCell[][] randomScan) {
		this.scannedElements = randomScan;
		redraw();
	}

	public ScanCell[][] getScannedElements() {
		return scannedElements;
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

	public void addModificationListener(ScanPaletteModificationListener listener) {
		listeners.add(listener);
	}

	public void removeModificationListener(
			ScanPaletteModificationListener listener) {
		listeners.remove(listener);
	}

	public void notifyListeners(ScanPaletteModificationEvent event) {
		for (ScanPaletteModificationListener listener : listeners) {
			listener.modification(event);
		}
	}
}
