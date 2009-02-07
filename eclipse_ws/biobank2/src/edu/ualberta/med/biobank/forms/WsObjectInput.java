package edu.ualberta.med.biobank.forms;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class WsObjectInput implements IEditorInput {
	private Node wsObject;

	public WsObjectInput(Node o) {
		wsObject = o;
	}

	public int getIndex() {
		if (wsObject != null) return wsObject.getId();
		return 0;
	}
	
	public Node getWsObject() {
		return wsObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		if (wsObject != null) { 
			String name = wsObject.getName();
			if (name != null) {
				if (wsObject instanceof SiteAdapter) return "Site " + name;
				if (wsObject instanceof ClinicAdapter) return "Clinic " + name;
			}
			else {
				if (wsObject instanceof SiteAdapter) return "New Site";
				if (wsObject instanceof ClinicAdapter) return "New Clinic";
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getIndex();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (wsObject == null) return false;
		
		if (o instanceof WsObjectInput) {
			if (wsObject.getClass() != ((WsObjectInput)o).wsObject.getClass()) return false;
		
			return (getIndex() == ((WsObjectInput)o).getIndex()); 
		}
		return false;
	}
}
