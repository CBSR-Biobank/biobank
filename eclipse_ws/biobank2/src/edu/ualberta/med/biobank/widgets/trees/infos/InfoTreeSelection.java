package edu.ualberta.med.biobank.widgets.trees.infos;

import org.eclipse.jface.viewers.ISelection;

public class InfoTreeSelection implements ISelection {

    private Object object;

    public InfoTreeSelection(Object object) {
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
