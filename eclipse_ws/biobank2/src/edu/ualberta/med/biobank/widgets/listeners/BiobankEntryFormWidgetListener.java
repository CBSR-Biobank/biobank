package edu.ualberta.med.biobank.widgets.listeners;

import java.util.EventListener;

public interface BiobankEntryFormWidgetListener extends EventListener {
	public void selectionChanged(MultiSelectEvent event);
}
