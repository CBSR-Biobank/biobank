package edu.ualberta.med.biobank.widgets.listeners;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNode;

/**
 * Drag support for moving items between TreeViewers in this widget.
 * 
 */
public class TreeViewerDragListener<T> implements DragSourceListener {
    private TreeViewer viewer;

    private MultiSelectNode<T>[] dragData;

    public TreeViewerDragListener(TreeViewer viewer, Transfer dndTransfer) {
        this.viewer = viewer;

        viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { dndTransfer }, this);
    }

    @Override
    public void dragStart(DragSourceEvent event) {
        event.doit = !viewer.getSelection().isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dragSetData(DragSourceEvent event) {
        Object[] selections = ((IStructuredSelection) viewer.getSelection())
            .toArray();

        int count = 0;
        MultiSelectNode<T>[] nodes = new MultiSelectNode[selections.length];
        for (Object sel : selections) {
            nodes[count] = (MultiSelectNode<T>) sel;
            count++;
        }
        event.data = nodes;
        dragData = nodes;
        dragFinished(event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dragFinished(DragSourceEvent event) {
        if (!event.doit || dragData == null)
            return;

        MultiSelectNode<T> rootNode = (MultiSelectNode<T>) viewer.getInput();
        for (MultiSelectNode<T> node : dragData) {
            rootNode.removeChild(node);
        }
    }
}