package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class NodeInput implements IEditorInput {
	private Node node;

	public NodeInput(Node o) {
		node = o;
	}

	public int getIndex() {
		if (node != null) return node.getId();
		return 0;
	}
	
	public Node getNode() {
		return node;
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
		if (node != null) { 
			String name = node.getName();
			if (name != null) {
				if (node instanceof SiteAdapter) return "Site " + name;
				else if (node instanceof StudyAdapter) return "Study " + name;
				else if (node instanceof ClinicAdapter) return "Clinic " + name;
				else Assert.isTrue(false, "tooltip name for "
						+ node.getClass().getName() + " not implemented");
			}
			else {
				if (node instanceof SiteAdapter) return "New Site";
				else if (node instanceof StudyAdapter) return "New Study";
				else if (node instanceof ClinicAdapter) return "New Clinic";
				else Assert.isTrue(false, "tooltip name for "
						+ node.getClass().getName() + " not implemented");
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
		if (node == null) return false;
		
		if (o instanceof NodeInput) {
			if (node.getClass() != ((NodeInput)o).node.getClass()) return false;
		
			return (getIndex() == ((NodeInput)o).getIndex()); 
		}
		return false;
	}
}
