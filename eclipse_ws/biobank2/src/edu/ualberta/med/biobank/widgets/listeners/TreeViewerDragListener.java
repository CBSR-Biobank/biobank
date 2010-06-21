package edu.ualberta.med.biobank.widgets.listeners;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNode;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNodeTransfer;

/**
 * Drag support for moving items between TreeViewers in this widget.
 * 
 */
public class TreeViewerDragListener implements DragSourceListener {
    private TreeViewer viewer;

    private MultiSelectNode[] dragData;

    public TreeViewerDragListener(TreeViewer viewer) {
        this.viewer = viewer;

        viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { MultiSelectNodeTransfer.getInstance() }, this);
    }

    public void dragStart(DragSourceEvent event) {
        event.doit = !viewer.getSelection().isEmpty();
    }

    public void dragSetData(DragSourceEvent event) {
        Object[] selections = ((IStructuredSelection) viewer.getSelection())
            .toArray();

        int count = 0;
        MultiSelectNode[] nodes = new MultiSelectNode[selections.length];
        for (Object sel : selections) {
            nodes[count] = (MultiSelectNode) sel;
            count++;
        }
        event.data = nodes;
        dragData = nodes;
        dragFinished(event);
    }

    public void dragFinished(DragSourceEvent event) {
        if (!event.doit || dragData == null)
            return;

        MultiSelectNode rootNode = (MultiSelectNode) viewer.getInput();
        for (MultiSelectNode node : dragData) {
            rootNode.removeChild(node);
        }
    }
}