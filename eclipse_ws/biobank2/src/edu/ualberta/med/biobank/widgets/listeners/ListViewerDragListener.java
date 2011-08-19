package edu.ualberta.med.biobank.widgets.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNodeTransfer;

/**
 * Drag support for moving items between TreeViewers in this widget.
 * 
 */
public class ListViewerDragListener<T> implements DragSourceListener {
    private ListViewer viewer;

    private List<T> dragData;

    public ListViewerDragListener(ListViewer viewer) {
        this.viewer = viewer;

        viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
            new Transfer[] { MultiSelectNodeTransfer.getInstance() }, this);
    }

    @Override
    public void dragStart(DragSourceEvent event) {
        event.doit = !viewer.getSelection().isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dragSetData(DragSourceEvent event) {
        List<T> selections = ((IStructuredSelection) viewer.getSelection())
            .toList();

        List<T> copy = new ArrayList<T>();
        copy.addAll(selections);
        event.data = copy;
        dragData = copy;
        dragFinished(event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dragFinished(DragSourceEvent event) {
        if (!event.doit || dragData == null)
            return;

        List<T> viewerInput = (List<T>) viewer.getInput();
        viewerInput.removeAll(dragData);
        viewer.setInput(viewerInput);
    }
}