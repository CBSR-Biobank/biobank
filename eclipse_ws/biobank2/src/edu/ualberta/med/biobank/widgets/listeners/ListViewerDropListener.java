package edu.ualberta.med.biobank.widgets.listeners;

import java.util.List;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import edu.ualberta.med.biobank.widgets.multiselect.NewMultiSelectWidget;

/**
 * Drop support for moving items between TreeViewers in this widget.
 * 
 */
public class ListViewerDropListener<T> extends ViewerDropAdapter {

    private NewMultiSelectWidget<T> multiSelect;

    public ListViewerDropListener(ListViewer viewer,
        NewMultiSelectWidget<T> multiSelect) {
        super(viewer);
        this.multiSelect = multiSelect;

        viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { multiSelect.getDndTransfer() }, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean performDrop(Object data) {
        ListViewer viewer = (ListViewer) getViewer();

        List<T> list = (List<T>) viewer.getInput();
        multiSelect.dropInto(viewer, list);
        multiSelect.notifyListeners();
        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        if (target != null && target == this.getSelectedObject()) {
            return false;
        } else
            return multiSelect.getDndTransfer().isSupportedType(transferType);
    }
}