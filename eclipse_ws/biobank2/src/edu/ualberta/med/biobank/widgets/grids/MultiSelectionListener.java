package edu.ualberta.med.biobank.widgets.grids;

import java.util.EventListener;

public interface MultiSelectionListener extends EventListener {

    public void selectionChanged(MultiSelectionEvent mse);
}
