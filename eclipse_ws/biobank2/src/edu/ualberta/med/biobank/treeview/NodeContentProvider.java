package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class NodeContentProvider implements ITreeContentProvider, IDeltaListener {
	protected TreeViewer viewer;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {
        Assert.isTrue(element instanceof Node, "Invalid object");
        return ((Node) element).getChildren().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
        Assert.isTrue(element instanceof Node, "Invalid object");
        return ((Node) element).getParent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
        Assert.isTrue(element instanceof Node, "Invalid object");
        return ((Node) element).hasChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if (oldInput != null) {
			removeListenerFrom((Node) oldInput);
		}
		if (newInput != null) {
			addListenerTo((Node) newInput);
		}
	}
	
	protected void addListenerTo(Node node) {
		node.addListener(this);
		for (Node child : node.getChildren()) {
			addListenerTo(child);
		}
	}
	
	protected void removeListenerFrom(Node node) {
		node.removeListener(this);
		for (Node child : node.getChildren()) {
			removeListenerFrom(child);
		}
	}

	@Override
	public void add(DeltaEvent event) {
		Node node = ((Node)event.receiver()).getParent();
		viewer.refresh(node, false);
	}

	@Override
	public void remove(DeltaEvent event) {
		add(event);
	}
}
