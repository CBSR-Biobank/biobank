package edu.ualberta.med.biobank.treeview.listeners;

import java.util.EventListener;

import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public interface AdapterChangedListener extends EventListener {
    public void changed(AdapterChangedEvent event);
}
