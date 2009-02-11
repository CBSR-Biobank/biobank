package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class NodeLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		if (element instanceof Node) {
			return ((Node) element).getName();
		}
		return new String();
	}

	public void addListener(ILabelProviderListener listener) {		
	}

	public void dispose() {
		
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {		
	}

}
