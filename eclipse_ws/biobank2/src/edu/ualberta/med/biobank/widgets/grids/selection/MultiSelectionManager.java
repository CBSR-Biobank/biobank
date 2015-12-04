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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;

public class MultiSelectionManager implements MouseListener, MouseTrackListener, KeyListener {

    private static Logger log = LoggerFactory.getLogger(MultiSelectionManager.class.getName());

    private final ContainerDisplayWidget container;

    private enum SelectionMode {
        NONE, MULTI, RANGE;
    }

    private boolean selectionTrackOn = false;
    private SelectionMode selectionMode = SelectionMode.NONE;

    private final Map<RowColPos, AbstractUIWell> selectedCells;
    private final List<MultiSelectionListener> listeners;

    private AbstractUIWell lastSelectedCell;

    // the cell the user selected by pressing the left mouse button
    private AbstractUIWell lastAnchorCell = null;

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
        this.specificBehaviour = t;
        container.addMouseListener(this);
        container.addMouseTrackListener(this);
        container.addKeyListener(this);
        enabled = true;
    }

    public void disableMultiSelection() {
        container.removeMouseListener(this);
        container.removeMouseTrackListener(this);
        container.removeKeyListener(this);
        clearMultiSelection();

        if (specificBehaviour != null) {
            Map<RowColPos, ? extends AbstractUIWell> cells = container.getCells();
            if (cells != null) {
                for (AbstractUIWell cell : cells.values()) {
                    specificBehaviour.removeSelection(cell);
                }
            }
        }
        enabled = false;
    }

    public void clearMultiSelection() {
        clearMultiSelection(true);
    }

    @SuppressWarnings("nls")
    public void clearMultiSelection(boolean notify) {
        for (AbstractUIWell cell : selectedCells.values()) {
            log.debug("clearMultiSelection: unselecting cell " + cell.getPositionStr());
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

    @SuppressWarnings("nls")
    private void addAllCellsInRange(AbstractUIWell cell) {
        if (lastAnchorCell == null) {
            lastAnchorCell = cell;
        }

        log.trace("addAllCellsInRange: cell: " + cell.getPositionStr());
        log.trace("addAllCellsInRange: lastSelectedCell: " + lastAnchorCell.getPositionStr());

        AbstractUIWell firstCell = lastAnchorCell;
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
                AbstractUIWell cellToAdd =
                    container.getCells().get(new RowColPos(indexRow, indexCol));
                if (cellToAdd != null
                    && specificBehaviour.isSelectable(cellToAdd)) {
                    if (!selectedCells.values().contains(cellToAdd)) {
                        log.trace("addAllCellsInRange: selected: " + cellToAdd.getPositionStr());
                        selectCell(cellToAdd);
                    }
                }
            }
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

    @Override
    public void mouseDoubleClick(MouseEvent e) {
        // do nothing
    }

    @SuppressWarnings("nls")
    @Override
    public void mouseDown(MouseEvent e) {
        selectionTrackOn = true;
        AbstractUIWell cell = container.getObjectAtCoordinates(e.x, e.y);
        if ((cell != null) && specificBehaviour.isSelectable(cell)) {
            switch (selectionMode) {
            case MULTI:
                if (selectedCells.containsValue(cell)) {
                    selectedCells.remove(new RowColPos(cell.getRow(), cell.getCol()));
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
                boolean alreadySelected = selectedCells.containsValue(cell);
                if (alreadySelected && (selectedCells.size() == 1)) {
                    selectedCells.clear();
                    cell.setSelected(false);
                    lastSelectedCell = null;
                } else {
                    log.debug("clearing muliple selection");
                    clearMultiSelection(false);
                    selectCell(cell);
                }
                lastAnchorCell = cell;
                break;
            }
            notifyListeners();
            container.updateCells();
        }
    }

    @Override
    public void mouseUp(MouseEvent e) {
        selectionTrackOn = false;
    }

    @Override
    public void mouseEnter(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseExit(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseHover(MouseEvent e) {
        if (selectionTrackOn) {
            AbstractUIWell cell = container.getObjectAtCoordinates(e.x, e.y);
            if (cell != null && !cell.equals(lastSelectedCell)) {
                selectCell(cell);
                notifyListeners();
                container.redraw();
            }
        }
    }

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
        } else if (e.keyCode == SWT.ESC) {
            clearMultiSelection(false);
            notifyListeners();
            container.updateCells();
        }
    }

}
