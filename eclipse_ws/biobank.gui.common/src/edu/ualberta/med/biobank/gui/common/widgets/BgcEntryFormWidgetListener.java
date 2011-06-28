package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.EventListener;

public interface BgcEntryFormWidgetListener extends EventListener {
    public void selectionChanged(MultiSelectEvent event);
}
