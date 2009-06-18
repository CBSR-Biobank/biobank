package edu.ualberta.med.biobank.widgets.listener;

import java.util.EventListener;

public interface MultiSelectListener extends EventListener {
	public void selectionChanged(MultiSelectEvent event);
}
