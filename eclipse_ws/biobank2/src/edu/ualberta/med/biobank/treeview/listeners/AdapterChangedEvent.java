package edu.ualberta.med.biobank.treeview.listeners;

import java.util.EventObject;

public class AdapterChangedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public AdapterChangedEvent(Object source) {
        super(source);
    }
}
