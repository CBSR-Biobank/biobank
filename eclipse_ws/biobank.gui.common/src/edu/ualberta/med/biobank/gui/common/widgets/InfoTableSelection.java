package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.jface.viewers.ISelection;

public class InfoTableSelection implements ISelection {

    private Object object;

    public InfoTableSelection(Object object) {
        this.object = object;
    }

    @Override
    public boolean isEmpty() {
        return (object == null);
    }

    public Object getObject() {
        return object;
    }

}
