package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

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
		MultiSelect.log4j.trace("dragStart: " + event.toString());
	}

	public void dragSetData(DragSourceEvent event) {
		Object[] selections = ((IStructuredSelection) viewer.getSelection())
			.toArray();

		int count = 0;
		MultiSelectNode[] nodes = new MultiSelectNode[selections.length];
		for (Object sel : selections) {
			nodes[count] = (MultiSelectNode) sel;
			++count;
		}
		event.data = nodes;
		dragData = nodes;
		MultiSelect.log4j.trace("dragSetData: " + event.toString());
	}

	public void dragFinished(DragSourceEvent event) {
		if (!event.doit)
			return;

		MultiSelectNode rootNode = (MultiSelectNode) viewer.getInput();
		for (MultiSelectNode node : dragData) {
			rootNode.removeChild(node);
			MultiSelect.log4j.trace("removed " + node.getName() + " from "
					+ rootNode.getName() + ", event: " + event.toString());
		}
	}
}