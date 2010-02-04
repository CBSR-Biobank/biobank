package edu.ualberta.med.biobank.forms.input;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.treeview.AdapterBase;

public class FormInput implements IEditorInput {
    private AdapterBase node;

    public FormInput(AdapterBase o) {
        node = o;
    }

    public int getIndex() {
        if (node != null) {
            Integer id = node.getId();
            if (id != null)
                return id.intValue();
        }
        return -1;
    }

    public AdapterBase getNode() {
        return node;
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
        if (node == null)
            return null;
        return node.getTooltipText();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if ((node == null) || (o == null))
            return false;

        if (o instanceof FormInput) {
            if (node.getClass() != ((FormInput) o).node.getClass())
                return false;

            int myIndex = getIndex();
            int oIndex = ((FormInput) o).getIndex();

            return ((myIndex != -1) && (oIndex != -1) && (myIndex == oIndex));
        }
        return false;
    }

}
