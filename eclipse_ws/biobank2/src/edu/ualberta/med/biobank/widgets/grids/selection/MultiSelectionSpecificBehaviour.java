package edu.ualberta.med.biobank.widgets.grids.selection;

import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;

public abstract class MultiSelectionSpecificBehaviour {

    public abstract void removeSelection(AbstractUICell cell);

    public abstract boolean isSelectable(AbstractUICell cell);
}
