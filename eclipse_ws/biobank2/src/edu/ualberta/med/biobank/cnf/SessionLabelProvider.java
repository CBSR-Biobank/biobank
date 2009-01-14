package edu.ualberta.med.biobank.cnf;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import edu.ualberta.med.biobank.model.WsObject;

public class SessionLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return ((WsObject) element).getName();
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
