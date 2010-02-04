package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BioBankPlugin;

public class NodeLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
        return BioBankPlugin.getDefault().getImage(element);
    }

    public String getText(Object element) {
        if (element instanceof AdapterBase) {
            return ((AdapterBase) element).getLabel();
        }
        return new String();
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

}
