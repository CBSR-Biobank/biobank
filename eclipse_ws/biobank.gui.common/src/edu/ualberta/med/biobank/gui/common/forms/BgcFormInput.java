package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class BgcFormInput implements IEditorInput {

    protected Object obj;

    protected String name;

    public BgcFormInput(Object o, String name) {
        obj = o;
        this.name = name;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        return (obj == o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == obj.getClass()) {
            return obj;
        }
        return null;
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }
}
