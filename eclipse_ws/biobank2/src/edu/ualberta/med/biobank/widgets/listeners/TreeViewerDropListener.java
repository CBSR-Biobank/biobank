package edu.ualberta.med.biobank.widgets.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNode;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNodeTransfer;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

/**
 * Drop support for moving items between TreeViewers in this widget.
 * 
 */
public class TreeViewerDropListener extends ViewerDropAdapter {

    private MultiSelectWidget multiSelect;

    public TreeViewerDropListener(TreeViewer viewer,
        MultiSelectWidget multiSelect) {
        super(viewer);
        this.multiSelect = multiSelect;

        viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { MultiSelectNodeTransfer.getInstance() }, this);
    }

    @Override
    public boolean performDrop(Object data) {
        TreeViewer viewer = (TreeViewer) getViewer();
        MultiSelectNode target = (MultiSelectNode) getCurrentTarget();
        MultiSelectNode targetParent;

        if (target != null) {
            targetParent = target.getParent();
        } else {
            targetParent = (MultiSelectNode) getViewer().getInput();
        }

        for (MultiSelectNode node : (MultiSelectNode[]) data) {
            targetParent.addChild(node);
            viewer.reveal(node);
        }
        multiSelect.notifyListeners();
        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        return MultiSelectNodeTransfer.getInstance().isSupportedType(
            transferType);
    }

}