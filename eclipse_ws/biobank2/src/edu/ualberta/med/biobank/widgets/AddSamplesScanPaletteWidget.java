package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.PaletteCell;
import edu.ualberta.med.biobank.widgets.listener.ScanPaletteModificationEvent;

/**
 * Widget to draw a palette for add palette samples screen. Can do selections
 * inside the palette to assign a type
 */
public class AddSamplesScanPaletteWidget extends ScanPaletteWidget {

	private List<PaletteCell> selectedCells;
	private PaletteCell lastSelectedCell;
	private boolean selectionTrackOn = false;
	private SelectionMode selectionMode = SelectionMode.NONE;

	private MouseListener selectionMouseListener;
	private MouseTrackListener selectionMouseTrackListener;

	private enum SelectionMode {
		NONE, MULTI, RANGE;
	}

	public AddSamplesScanPaletteWidget(Composite parent) {
		super(parent);
		selectedCells = new ArrayList<PaletteCell>();
		initListeners();
	}

	@Override
	protected void initLegend() {
		hasLegend = true;
		statusAvailable = new ArrayList<SampleCellStatus>();
		statusAvailable.add(SampleCellStatus.EMPTY);
		statusAvailable.add(SampleCellStatus.NO_TYPE);
		statusAvailable.add(SampleCellStatus.TYPE);
		legendWidth = PALETTE_WIDTH / statusAvailable.size();
	}

	public void clearSelection() {
		for (PaletteCell cell : selectedCells) {
			cell.setSelected(false);
		}
		notifyListeners();
		selectedCells.clear();
	}

	private void notifyListeners() {
		notifyListeners(new ScanPaletteModificationEvent(this, selectedCells
			.size()));
	}

	private void addAllCellsInRange(PaletteCell cell) {
		PaletteCell lastSelected = selectedCells.get(selectedCells.size() - 1);
		int startRow = lastSelected.getRow();
		int endRow = cell.getRow();
		if (startRow > endRow) {
			startRow = cell.getRow();
			endRow = lastSelected.getRow();
		}
		for (int indexRow = startRow; indexRow <= endRow; indexRow++) {
			int startCol = lastSelected.getColumn();
			int endCol = cell.getColumn();
			if (startCol > endCol) {
				startCol = cell.getColumn();
				endCol = lastSelected.getColumn();
			}
			for (int indexCol = startCol; indexCol <= endCol; indexCol++) {
				PaletteCell cellToAdd = scannedElements[indexRow][indexCol];
				if (cellToAdd != null) {
					if (!selectedCells.contains(cellToAdd)) {
						cellToAdd.setSelected(true);
						selectedCells.add(cellToAdd);
					}
				}
			}
		}
	}

	@Override
	protected void drawRectangle(PaintEvent e, Rectangle rectangle,
			int indexRow, int indexCol) {
		super.drawRectangle(e, rectangle, indexRow, indexCol);
		if (scannedElements != null
				&& scannedElements[indexRow][indexCol] != null
				&& scannedElements[indexRow][indexCol].isSelected()) {
			Rectangle rect = new Rectangle(rectangle.x + 5, rectangle.y + 5,
				rectangle.width - 10, rectangle.height - 10);
			Color color = e.display.getSystemColor(SWT.COLOR_WHITE);
			e.gc.setForeground(color);
			e.gc.drawRectangle(rect);
		}
	}

	public List<PaletteCell> getSelectedCells() {
		return selectedCells;
	}

	public void enableSelection() {
		addMouseListener(selectionMouseListener);
		addMouseTrackListener(selectionMouseTrackListener);
	}

	public void disableSelection() {
		removeMouseListener(selectionMouseListener);
		removeMouseTrackListener(selectionMouseTrackListener);
		clearSelection();
		for (PaletteCell[] rowCells : scannedElements) {
			for (PaletteCell cell : rowCells) {
				if (cell != null) {
					cell.setType(null);
					cell.setStatus(SampleCellStatus.NEW);
				}
			}
		}
	}

	private void initListeners() {
		selectionMouseListener = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				selectionTrackOn = true;
				if (scannedElements != null) {
					PaletteCell cell = getCellAtCoordinates(e.x, e.y);
					if (cell != null) {
						switch (selectionMode) {
						case MULTI:
							if (selectedCells.contains(cell)) {
								selectedCells.remove(cell);
								cell.setSelected(false);
							} else {
								selectedCells.add(cell);
								cell.setSelected(true);
							}
							break;
						case RANGE:
							if (selectedCells.size() > 0) {
								addAllCellsInRange(cell);
							} else {
								selectedCells.add(cell);
								cell.setSelected(true);
							}
							break;
						default:
							clearSelection();
							selectedCells.add(cell);
							cell.setSelected(true);
							break;
						}
						notifyListeners();
						redraw();
					}
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				selectionTrackOn = false;
			}
		};

		selectionMouseTrackListener = new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				if (selectionTrackOn) {
					PaletteCell cell = getCellAtCoordinates(e.x, e.y);
					if (!cell.equals(lastSelectedCell)) {
						selectedCells.add(cell);
						cell.setSelected(true);
						notifyListeners();
						redraw();
					}
				}
			}
		};

		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.SHIFT) {
					selectionMode = SelectionMode.RANGE;
				} else if (e.keyCode == SWT.CTRL) {
					selectionMode = SelectionMode.MULTI;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.SHIFT || e.keyCode == SWT.CTRL) {
					selectionMode = SelectionMode.NONE;
				}
			}
		});
	}

	public boolean isEverythingTyped() {
		for (PaletteCell[] rowCells : scannedElements) {
			for (PaletteCell cell : rowCells) {
				if (cell != null && cell.getType() == null) {
					return false;
				}
			}
		}
		return true;
	}
}
