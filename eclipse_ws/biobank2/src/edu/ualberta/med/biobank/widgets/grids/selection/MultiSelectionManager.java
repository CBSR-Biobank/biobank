package edu.ualberta.med.biobank.widgets.grids.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;

public class MultiSelectionManager {

    private ContainerDisplayWidget container;

    private MouseListener selectionMouseListener;
    private MouseTrackListener selectionMouseTrackListener;

    private enum SelectionMode {
        NONE, MULTI, RANGE;
    }

    private boolean selectionTrackOn = false;
    private SelectionMode selectionMode = SelectionMode.NONE;

    private Map<RowColPos, AbstractUIWell> selectedCells;
    private AbstractUIWell lastSelectedCell;
    private List<MultiSelectionListener> listeners;

    private MultiSelectionSpecificBehaviour specificBehaviour;

    private boolean enabled = false;

    public MultiSelectionManager(ContainerDisplayWidget container) {
        this.container = container;
        selectedCells = new TreeMap<RowColPos, AbstractUIWell>();
        listeners = new ArrayList<MultiSelectionListener>();
    }

    public Collection<AbstractUIWell> getSelectedCells() {
        return selectedCells.values();
    }

    public Set<RowColPos> getSelectedPositions() {
        return selectedCells.keySet();
    }

    public void enableMultiSelection(MultiSelectionSpecificBehaviour t) {
        initListeners();
        this.specificBehaviour = t;
        container.addMouseListener(selectionMouseListener);
        container.addMouseTrackListener(selectionMouseTrackListener);
        enabled = true;
    }

    public void disableMultiSelection() {
        container.removeMouseListener(selectionMouseListener);
        container.removeMouseTrackListener(selectionMouseTrackListener);
        clearMultiSelection();
        for (AbstractUIWell cell : container.getCells().values()) {
            specificBehaviour.removeSelection(cell);
        }
        enabled = false;
    }

    public void clearMultiSelection() {
        clearMultiSelection(true);
    }

    public void clearMultiSelection(boolean notify) {
        for (AbstractUIWell cell : selectedCells.values()) {
            cell.setSelected(false);
        }
        selectedCells.clear();
        if (notify) {
            notifyListeners();
        }
    }

    private void notifyListeners() {
        notifyListeners(new MultiSelectionEvent(this, selectedCells.size()));
    }

    private void addAllCellsInRange(AbstractUIWell cell) {
        AbstractUIWell firstCell = lastSelectedCell;
        int startRow = firstCell.getRow();
        int endRow = cell.getRow();
        if (startRow > endRow) {
            startRow = cell.getRow();
            endRow = firstCell.getRow();
        }
        for (int indexRow = startRow; indexRow <= endRow; indexRow++) {
            int startCol = firstCell.getCol();
            int endCol = cell.getCol();
            if (startCol > endCol) {
                startCol = cell.getCol();
                endCol = firstCell.getCol();
            }
            for (int indexCol = startCol; indexCol <= endCol; indexCol++) {
                AbstractUIWell cellToAdd = container.getCells().get(
                    new RowColPos(indexRow, indexCol));
                if (cellToAdd != null
                    && specificBehaviour.isSelectable(cellToAdd)) {
                    if (!selectedCells.values().contains(cellToAdd)) {
                        selectCell(cellToAdd);
                    }
                }
            }
        }
    }

    private void initListeners() {
        if (selectionMouseListener == null) {
            selectionMouseListener = new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    selectionTrackOn = true;
                    AbstractUIWell cell = container.getObjectAtCoordinates(e.x,
                        e.y);
                    if (cell != null && specificBehaviour.isSelectable(cell)) {
                        switch (selectionMode) {
                        case MULTI:
                            if (selectedCells.containsValue(cell)) {
                                selectedCells.remove(new RowColPos(cell
                                    .getRow(), cell.getCol()));
                                cell.setSelected(false);
                            } else {
                                selectCell(cell);
                            }
                            break;
                        case RANGE:
                            if (selectedCells.size() > 0) {
                                addAllCellsInRange(cell);
                            } else {
                                selectCell(cell);
                            }
                            break;
                        default:
                            boolean alreadySelected = selectedCells
                                .containsValue(cell);
                            if (alreadySelected && selectedCells.size() == 1) {
                                selectedCells.clear();
                                cell.setSelected(false);
                                lastSelectedCell = null;
                            } else {
                                clearMultiSelection(false);
                                selectCell(cell);
                            }
                            break;
                        }
                        notifyListeners();
                        container.redraw();
                    }
                }

                @Override
                public void mouseUp(MouseEvent e) {
                    selectionTrackOn = false;
                }
            };
        }

        if (selectionMouseTrackListener == null) {
            selectionMouseTrackListener = new MouseTrackAdapter() {
                @Override
                public void mouseHover(MouseEvent e) {
                    if (selectionTrackOn) {
                        AbstractUIWell cell = container.getObjectAtCoordinates(
                            e.x, e.y);
                        if (cell != null && !cell.equals(lastSelectedCell)) {
                            selectCell(cell);
                            notifyListeners();
                            container.redraw();
                        }
                    }
                }
            };

            container.addKeyListener(new KeyListener() {
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
    }

    private void selectCell(AbstractUIWell cell) {
        selectedCells.put(new RowColPos(cell.getRow(), cell.getCol()), cell);
        cell.setSelected(true);
        lastSelectedCell = cell;
    }

    public void addMultiSelectionListener(MultiSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeMultiSelectionListener(MultiSelectionListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(MultiSelectionEvent event) {
        for (MultiSelectionListener listener : listeners) {
            listener.selectionChanged(event);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

}
