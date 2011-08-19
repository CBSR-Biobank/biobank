package edu.ualberta.med.biobank.widgets.multiselect;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class NewMultiSelectWidget<T> extends BgcBaseWidget {

    private Button moveRightButton;

    private Button moveLeftButton;

    private List<T> allObjects;

    private List<T> addedToSelection = new ArrayList<T>();
    private List<T> removedFromSelection = new ArrayList<T>();

    private ListViewer availLv;
    private ListViewer selLv;

    private int minHeight;

    protected boolean ctrl;

    public NewMultiSelectWidget(Composite parent, int style, String leftLabel,
        String rightLabel, int minHeight, List<T> allObjects) {
        super(parent, style);

        this.minHeight = minHeight;

        this.allObjects = allObjects;

        setLayout(new GridLayout(3, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        availLv = createLabelledList(this, leftLabel);
        availLv.setComparator(new ViewerComparator());
        availLv.setInput(allObjects);

        Composite moveComposite = new Composite(this, SWT.NONE);
        moveComposite.setLayout(new GridLayout(1, false));
        moveComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
            false, true));
        moveRightButton = new Button(moveComposite, SWT.PUSH);
        moveRightButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ARROW_RIGHT));
        moveRightButton.setToolTipText(Messages.MultiSelectWidget_move_tooltip);
        moveLeftButton = new Button(moveComposite, SWT.PUSH);
        moveLeftButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ARROW_LEFT));
        moveLeftButton
            .setToolTipText(Messages.MultiSelectWidget_remove_tooltip);

        selLv = createLabelledList(this, rightLabel);
        selLv.setComparator(new ViewerComparator());
        availLv.setInput(new ArrayList<T>());

        dragAndDropSupport(availLv, selLv);
        dragAndDropSupport(selLv, availLv);

        moveRightButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveTreeViewerSelection(availLv, selLv, true);
            }
        });

        moveLeftButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveTreeViewerSelection(selLv, availLv, true);
            }
        });
    }

    public void setFilter(ViewerFilter filter) {
        ViewerFilter[] filters = new ViewerFilter[] { filter };
        availLv.setFilters(filters);
        selLv.setFilters(filters);
    }

    @SuppressWarnings("unchecked")
    private void moveTreeViewerSelection(ListViewer srcLv, ListViewer destLv,
        boolean notify) {
        List<T> srcList = (List<T>) srcLv.getInput();
        List<T> destList = (List<T>) destLv.getInput();
        if (destList == null)
            destList = new ArrayList<T>();
        List<T> fromSelection = ((IStructuredSelection) srcLv.getSelection())
            .toList();
        srcList.removeAll(fromSelection);
        srcLv.setInput(srcList);
        destList.addAll(fromSelection);
        destLv.setInput(destList);
        if (destLv == selLv)
            addedToSelection.addAll(fromSelection);
        else
            removedFromSelection.addAll(fromSelection);
        if (notify)
            notifyListeners();
    }

    public void selectAll() {
        availLv.getList().selectAll();
        moveTreeViewerSelection(availLv, selLv, true);
    }

    public void deselectAll() {
        selLv.getList().selectAll();
        moveTreeViewerSelection(selLv, availLv, true);
    }

    private ListViewer createLabelledList(Composite parent, String label) {
        Composite selComposite = new Composite(parent, SWT.NONE);
        selComposite.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        selComposite.setLayoutData(gd);

        Label l = new Label(selComposite, SWT.NONE);
        l.setText(label);
        l.setFont(new Font(null, "sans-serif", 8, SWT.BOLD)); //$NON-NLS-1$
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.CENTER;
        l.setLayoutData(gd);

        ListViewer lv = new ListViewer(selComposite, SWT.MULTI | SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = minHeight;
        gd.widthHint = 180;
        lv.getControl().setLayoutData(gd);

        lv.setLabelProvider(new BiobankLabelProvider());
        lv.setContentProvider(new ArrayContentProvider());

        return lv;
    }

    private void dragAndDropSupport(ListViewer fromList, ListViewer toList) {
        // FIXME need to understand more. Maybe need to come back to TreeViewers
        // after all.
        // new ListViewerDragListener<T>(fromList);
        // new ListViewerDropListener<T>(toList, this);
    }

    public void addSelection(List<T> newSelection) {
        availLv.setSelection(new StructuredSelection(newSelection));
        moveTreeViewerSelection(availLv, selLv, false);
    }

    /**
     * same as addSelection but remove previously selected elements
     */
    public void setSelection(List<T> selection) {
        availLv.setInput(allObjects);
        selLv.setInput(new ArrayList<T>());
        addSelection(selection);
    }

    /**
     * Return the selected items in the order specified by user.
     * 
     */
    @SuppressWarnings("unchecked")
    public List<T> getSelected() {
        // FIXME can we keep an order with ListViewer?
        List<T> result = (List<T>) selLv.getInput();
        // for (MultiSelectNode node : selTreeRootNode.getChildren()) {
        // result.add(node.getId());
        // }
        return result;
    }

    public List<T> getAddedToSelection() {
        return addedToSelection;
    }

    public List<T> getRemovedToSelection() {
        return removedFromSelection;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selLv.getControl().setEnabled(enabled);
        availLv.getControl().setEnabled(enabled);
    }

    public void refreshLists() {
        availLv.refresh();
        selLv.refresh();
    }
}
