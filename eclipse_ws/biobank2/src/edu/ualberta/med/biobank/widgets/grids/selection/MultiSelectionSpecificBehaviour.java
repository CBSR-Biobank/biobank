package edu.ualberta.med.biobank.widgets.grids.selection;

import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;

public abstract class MultiSelectionSpecificBehaviour {

    public abstract void removeSelection(AbstractUIWell cell);

    public abstract boolean isSelectable(AbstractUIWell cell);
}
