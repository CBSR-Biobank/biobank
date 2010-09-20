package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BioBankPlugin;

public class NodeLabelProvider implements ILabelProvider {

    @Override
    public Image getImage(Object element) {
        if (element instanceof AdapterBase) {
            return BioBankPlugin.getDefault().getImage(element);
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof AdapterBase) {
            return ((AdapterBase) element).getLabel();
        }
        return "";
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

}
