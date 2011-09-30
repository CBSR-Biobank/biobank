package edu.ualberta.med.biobank.treeview.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class NodeContentProvider implements ITreeContentProvider,
    IDeltaListener {
    protected TreeViewer viewer;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
     * Object)
     */
    @Override
    public Object[] getChildren(Object element) {
        Assert.isTrue(element instanceof AdapterBase, "Invalid object"); //$NON-NLS-1$
        return ((AdapterBase) element).getChildren().toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
     * )
     */
    @Override
    public Object getParent(Object element) {
        Assert.isTrue(element instanceof AdapterBase, "Invalid object"); //$NON-NLS-1$
        return ((AdapterBase) element).getParent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
     * Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        Assert.isTrue(element instanceof AdapterBase, "Invalid object"); //$NON-NLS-1$
        return ((AdapterBase) element).hasChildren();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
     * .lang.Object)
     */
    @Override
    public Object[] getElements(Object element) {
        return getChildren(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
     * .viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer) viewer;
        if (oldInput != null) {
            removeListenerFrom((AdapterBase) oldInput);
        }
        if (newInput != null) {
            addListenerTo((AdapterBase) newInput);
        }
    }

    protected void addListenerTo(AdapterBase node) {
        node.addListener(this);
        for (AbstractAdapterBase child : node.getChildren()) {
            addListenerTo((AdapterBase) child);
        }
    }

    protected void removeListenerFrom(AdapterBase node) {
        node.removeListener(this);
        for (AbstractAdapterBase child : node.getChildren()) {
            removeListenerFrom((AdapterBase) child);
        }
    }

    @Override
    public void add(DeltaEvent event) {
        AdapterBase node = ((AdapterBase) event.receiver()).getParent();
        if (!viewer.isBusy()) {
            viewer.refresh(node, false);
        }
    }

    @Override
    public void remove(DeltaEvent event) {
        add(event);
    }
}
