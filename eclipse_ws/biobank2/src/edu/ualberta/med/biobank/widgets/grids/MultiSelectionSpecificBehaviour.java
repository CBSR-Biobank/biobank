package edu.ualberta.med.biobank.widgets.grids;

import edu.ualberta.med.biobank.model.Cell;

public abstract class MultiSelectionSpecificBehaviour {

    public abstract void removeSelection(Cell cell);

    public abstract boolean isSelectable(Cell cell);
}
