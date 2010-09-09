package edu.ualberta.med.biobank.widgets.grids.selection;

import java.util.EventListener;

public interface MultiSelectionListener extends EventListener {

    public void selectionChanged(MultiSelectionEvent mse);
}
