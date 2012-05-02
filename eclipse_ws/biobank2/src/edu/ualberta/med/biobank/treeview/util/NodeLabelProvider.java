package edu.ualberta.med.biobank.treeview.util;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;

public class NodeLabelProvider implements ILabelProvider {

    @Override
    public Image getImage(Object element) {
        if (element instanceof AbstractAdapterBase) {
            return BiobankPlugin.getDefault().getImage(element);
        }
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public String getText(Object element) {
        if (element instanceof AbstractAdapterBase) {
            return ((AbstractAdapterBase) element).getLabel();
        }
        return "error in getText";
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
