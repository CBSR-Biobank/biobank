package edu.ualberta.med.biobank.forms.input;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.treeview.AdaptorBase;

public class FormInput implements IEditorInput {
	private AdaptorBase node;

	public FormInput(AdaptorBase o) {
		node = o;
	}

	public Integer getIndex() {
		if (node != null)
			return node.getId();
		return 0;
	}

	public AdaptorBase getNode() {
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
		return node.getTitle();
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

			return (getIndex() == ((FormInput) o).getIndex());
		}
		return false;
	}

}
