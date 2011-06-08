package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.EventListener;

public interface BiobankEntryFormWidgetListener extends EventListener {
    public void selectionChanged(MultiSelectEvent event);
}
