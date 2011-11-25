package edu.ualberta.med.biobank.widgets.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNode;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

/**
 * Drop support for moving items between TreeViewers in this widget.
 * 
 */
public class TreeViewerDropListener<T> extends ViewerDropAdapter {

    private MultiSelectWidget<T> multiSelect;

    public TreeViewerDropListener(TreeViewer viewer,
        MultiSelectWidget<T> multiSelect) {
        super(viewer);
        this.multiSelect = multiSelect;

        viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { multiSelect.getDndTransfer() }, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean performDrop(Object data) {
        TreeViewer viewer = (TreeViewer) getViewer();
        MultiSelectNode<T> target = (MultiSelectNode<T>) getCurrentTarget();
        MultiSelectNode<T> targetParent;

        if (target != null) {
            targetParent = target.getParent();
        } else {
            targetParent = (MultiSelectNode<T>) getViewer().getInput();
        }

        for (MultiSelectNode<T> node : (MultiSelectNode<T>[]) data) {
            targetParent.addChild(node);
            viewer.reveal(node);
        }
        multiSelect.notifyListeners();
        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        if (target != null && target == this.getSelectedObject())
            return false;
        return multiSelect.getDndTransfer().isSupportedType(transferType);
    }
}