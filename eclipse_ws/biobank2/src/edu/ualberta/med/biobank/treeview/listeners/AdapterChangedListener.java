package edu.ualberta.med.biobank.treeview.listeners;

import java.util.EventListener;

public interface AdapterChangedListener extends EventListener {
    public void changed(AdapterChangedEvent event);
}
