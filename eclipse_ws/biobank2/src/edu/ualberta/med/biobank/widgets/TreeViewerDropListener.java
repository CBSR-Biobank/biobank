package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drop support for moving items between TreeViewers in this widget.
 * 
 */
public class TreeViewerDropListener extends ViewerDropAdapter {

	private MultiSelect multiSelect;

	public TreeViewerDropListener(TreeViewer viewer, MultiSelect multiSelect) {
		super(viewer);
		this.multiSelect = multiSelect;

		viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY,
			new Transfer[] { MultiSelectNodeTransfer.getInstance() }, this);
	}

	@Override
	public boolean performDrop(Object data) {
		boolean result = true;

		MultiSelect.log4j.trace("performDrop: event: " + data.toString());
		MultiSelectNode target = (MultiSelectNode) getCurrentTarget();
		if (target == null)
			target = (MultiSelectNode) getViewer().getInput();

		MultiSelectNode[] nodes = (MultiSelectNode[]) data;

		TreeViewer viewer = (TreeViewer) getViewer();

		for (MultiSelectNode node : nodes) {
			MultiSelect.log4j.trace("target: " + target + ", node_parent: "
					+ node.getParent());

			if (target.getParent() == null) {
				target.addChild(node);

				MultiSelect.log4j.trace("added " + node.getName() + " to "
						+ target.getName());
			} else {
				target.getParent().insertAfter(target, node);

				MultiSelect.log4j.trace("inserted " + node.getName()
						+ " after " + target.getName() + " on "
						+ target.getParent().getName());
			}
			viewer.reveal(node);
		}
		multiSelect.notifyListeners();
		return result;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return MultiSelectNodeTransfer.getInstance().isSupportedType(
			transferType);
	}

}