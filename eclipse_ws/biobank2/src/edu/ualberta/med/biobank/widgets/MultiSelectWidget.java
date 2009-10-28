package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.forms.FormUtils;

public class MultiSelectWidget extends BiobankWidget {

    private TreeViewer selTree;

    private TreeViewer availTree;

    private MultiSelectNode selTreeRootNode = new MultiSelectNode(null, 0,
        "selRoot");

    private MultiSelectNode availTreeRootNode = new MultiSelectNode(null, 0,
        "availRoot");

    private int minHeight;

    public MultiSelectWidget(Composite parent, int style, String leftLabel,
        String rightLabel, int minHeight) {
        super(parent, style);

        this.minHeight = minHeight;

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        selTree = createLabelledTree(this, leftLabel);
        selTree.setInput(selTreeRootNode);
        selTree.setComparator(new ViewerComparator());
        availTree = createLabelledTree(this, rightLabel);
        availTree.setInput(availTreeRootNode);
        availTree.setComparator(new ViewerComparator());

        dragAndDropSupport(availTree, selTree);
        dragAndDropSupport(selTree, availTree);
    }

    private TreeViewer createLabelledTree(Composite parent, String label) {
        Composite selComposite = new Composite(parent, SWT.NONE);
        selComposite.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        selComposite.setLayoutData(gd);

        Label l = new Label(selComposite, SWT.NONE);
        l.setText(label);
        l.setFont(FormUtils.getHeadingFont());
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.CENTER;
        l.setLayoutData(gd);

        TreeViewer tv = new TreeViewer(selComposite);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = minHeight;
        gd.widthHint = 180;
        tv.getTree().setLayoutData(gd);

        tv.setLabelProvider(new MultiSelectNodeLabelProvider());
        tv.setContentProvider(new MultiSelectNodeContentProvider());

        return tv;
    }

    private void dragAndDropSupport(TreeViewer fromList, TreeViewer toList) {
        new TreeViewerDragListener(fromList);
        new TreeViewerDropListener(toList, this);
    }

    public void addSelections(ListOrderedMap available, List<Integer> selected) {
        MapIterator it = available.mapIterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();
            if (selected.contains(key)) {
                selTreeRootNode.addChild(new MultiSelectNode(selTreeRootNode,
                    key, (String) it.getValue()));
            } else {
                availTreeRootNode.addChild(new MultiSelectNode(
                    availTreeRootNode, key, (String) it.getValue()));
            }
        }
    }

    public void setSelections(ListOrderedMap available, List<Integer> selected) {
        selTreeRootNode.clear();
        availTreeRootNode.clear();
        addSelections(available, selected);
    }

    /**
     * Return the selected items in the order specified by user.
     * 
     */
    public List<Integer> getSelected() {
        List<Integer> result = new ArrayList<Integer>();
        for (MultiSelectNode node : selTreeRootNode.getChildren()) {
            result.add(new Integer(node.getId()));
        }
        return result;
    }
}
