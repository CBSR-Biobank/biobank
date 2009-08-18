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

import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationEvent;

/**
 * Widget to draw a pallet for add pallet samples screen. Can do selections
 * inside the pallet to assign a type
 */
public class AddSamplesScanPalletWidget extends ScanPalletWidget {

    private List<PalletCell> selectedCells;
    private PalletCell lastSelectedCell;
    private boolean selectionTrackOn = false;
    private SelectionMode selectionMode = SelectionMode.NONE;

    private MouseListener selectionMouseListener;
    private MouseTrackListener selectionMouseTrackListener;

    private enum SelectionMode {
        NONE, MULTI, RANGE;
    }

    public AddSamplesScanPalletWidget(Composite parent) {
        super(parent);
        selectedCells = new ArrayList<PalletCell>();
        initListeners();
    }

    @Override
    protected void initLegend() {
        hasLegend = true;
        statusAvailable = new ArrayList<SampleCellStatus>();
        statusAvailable.add(SampleCellStatus.EMPTY);
        statusAvailable.add(SampleCellStatus.NO_TYPE);
        statusAvailable.add(SampleCellStatus.TYPE);
        legendWidth = PALLET_WIDTH / statusAvailable.size();
    }

    public void clearSelection() {
        for (PalletCell cell : selectedCells) {
            cell.setSelected(false);
        }
        notifyListeners();
        selectedCells.clear();
    }

    private void notifyListeners() {
        notifyListeners(new ScanPalletModificationEvent(this, selectedCells
            .size()));
    }

    private void addAllCellsInRange(PalletCell cell) {
        PalletCell lastSelected = selectedCells.get(selectedCells.size() - 1);
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
                PalletCell cellToAdd = scannedElements[indexRow][indexCol];
                if (cellToAdd != null && cellToAdd.getValue() != null) {
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

    public List<PalletCell> getSelectedCells() {
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
        for (PalletCell[] rowCells : scannedElements) {
            for (PalletCell cell : rowCells) {
                if (PalletCell.hasValue(cell)) {
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
                    PalletCell cell = getCellAtCoordinates(e.x, e.y);
                    if (cell != null && cell.getValue() != null) {
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
                    PalletCell cell = getCellAtCoordinates(e.x, e.y);
                    if (cell != null && !cell.equals(lastSelectedCell)) {
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
        for (PalletCell[] rowCells : scannedElements) {
            for (PalletCell cell : rowCells) {
                if (PalletCell.hasValue(cell) && cell.getType() == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
