package edu.ualberta.med.biobank.widgets.multiselect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.widgets.BiobankWidget;
import edu.ualberta.med.biobank.widgets.listeners.TreeViewerDragListener;
import edu.ualberta.med.biobank.widgets.listeners.TreeViewerDropListener;

public class MultiSelectWidget extends BiobankWidget {

    private TreeViewer selTree;

    private TreeViewer availTree;

    private Button moveRightButton;

    private Button moveLeftButton;

    private MultiSelectNode selTreeRootNode = new MultiSelectNode(null, 0,
        "selRoot");

    private MultiSelectNode availTreeRootNode = new MultiSelectNode(null, 0,
        "availRoot");

    private int minHeight;

    protected boolean ctrl;

    public MultiSelectWidget(Composite parent, int style, String leftLabel,
        String rightLabel, int minHeight) {
        super(parent, style);

        this.minHeight = minHeight;

        setLayout(new GridLayout(3, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        availTree = createLabelledTree(this, leftLabel);
        availTree.setInput(availTreeRootNode);
        availTree.setComparator(new ViewerComparator());

        Composite moveComposite = new Composite(this, SWT.NONE);
        moveComposite.setLayout(new GridLayout(1, false));
        moveComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
            false, true));
        moveRightButton = new Button(moveComposite, SWT.PUSH);
        moveRightButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_ARROW_RIGHT));
        moveRightButton.setToolTipText("Move to selected");
        moveLeftButton = new Button(moveComposite, SWT.PUSH);
        moveLeftButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_ARROW_LEFT));
        moveLeftButton.setToolTipText("Remove from selected");

        selTree = createLabelledTree(this, rightLabel);
        selTree.setInput(selTreeRootNode);
        selTree.setComparator(new ViewerComparator());

        dragAndDropSupport(availTree, selTree);
        dragAndDropSupport(selTree, availTree);

        moveRightButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveTreeViewerSelection(availTree, selTree);
            }
        });

        moveLeftButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveTreeViewerSelection(selTree, availTree);
            }
        });
    }

    private void moveTreeViewerSelection(TreeViewer srcTree, TreeViewer destTree) {
        MultiSelectNode srcRootNode = (MultiSelectNode) srcTree.getInput();
        MultiSelectNode destRootNode = (MultiSelectNode) destTree.getInput();
        List<?> fromSelection = ((IStructuredSelection) srcTree.getSelection())
            .toList();

        for (Object obj : fromSelection) {
            MultiSelectNode node = (MultiSelectNode) obj;
            destRootNode.addChild(node);
            srcRootNode.removeChild(node);
            destTree.reveal(node);
            srcTree.refresh();
        }
        notifyListeners();
    }

    public void selectAll() {
        availTree.getTree().selectAll();
        moveTreeViewerSelection(availTree, selTree);
    }

    public void deselectAll() {
        selTree.getTree().selectAll();
        moveTreeViewerSelection(selTree, availTree);
    }

    private TreeViewer createLabelledTree(Composite parent, String label) {
        Composite selComposite = new Composite(parent, SWT.NONE);
        selComposite.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        selComposite.setLayoutData(gd);

        Label l = new Label(selComposite, SWT.NONE);
        l.setText(label);
        l.setFont(new Font(null, "sans-serif", 8, SWT.BOLD));
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.CENTER;
        l.setLayoutData(gd);

        TreeViewer tv = new TreeViewer(selComposite, SWT.MULTI | SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = minHeight;
        gd.widthHint = 180;
        tv.getTree().setLayoutData(gd);

        tv.getTree().addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.stateMask & SWT.CTRL) != 0)
                    ctrl = true;
                // check characters
                if (ctrl == true && e.keyCode == 'a') {
                    ((Tree) e.getSource()).selectAll();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.stateMask & SWT.CTRL) != 0)
                    ctrl = false;
            }
        });

        tv.setLabelProvider(new MultiSelectNodeLabelProvider());
        tv.setContentProvider(new MultiSelectNodeContentProvider());

        return tv;
    }

    private void dragAndDropSupport(TreeViewer fromList, TreeViewer toList) {
        new TreeViewerDragListener(fromList);
        new TreeViewerDropListener(toList, this);
    }

    public void addSelections(LinkedHashMap<Integer, String> available,
        List<Integer> selected) {
        for (Entry<Integer, String> entry : available.entrySet()) {
            Integer key = entry.getKey();
            String string = entry.getValue();
            if (selected.contains(key)) {
                selTreeRootNode.addChild(new MultiSelectNode(selTreeRootNode,
                    key, string));
            } else {
                availTreeRootNode.addChild(new MultiSelectNode(
                    availTreeRootNode, key, string));
            }
        }
    }

    /**
     * same as addSelections but remove previously set elements
     */
    public void setSelections(LinkedHashMap<Integer, String> available,
        List<Integer> selected) {
        selTreeRootNode.clear();
        availTreeRootNode.clear();
        addSelections(available, selected);
        selTreeRootNode.reset();
        availTreeRootNode.reset();
    }

    /**
     * Return the selected items in the order specified by user.
     * 
     */
    public List<Integer> getSelected() {
        List<Integer> result = new ArrayList<Integer>();
        for (MultiSelectNode node : selTreeRootNode.getChildren()) {
            result.add(node.getId());
        }
        return result;
    }

    public List<Integer> getAddedToSelection() {
        List<Integer> result = new ArrayList<Integer>();
        for (MultiSelectNode node : selTreeRootNode.getAddedChildren()) {
            result.add(node.getId());
        }
        return result;
    }

    public List<Integer> getRemovedToSelection() {
        List<Integer> result = new ArrayList<Integer>();
        for (MultiSelectNode node : selTreeRootNode.getRemovedChildren()) {
            result.add(node.getId());
        }
        return result;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selTree.getControl().setEnabled(enabled);
        availTree.getControl().setEnabled(enabled);
    }

    public void setSelection(List<Integer> selected) {
        availTree.getTree().selectAll();
        availTree.setSelection(new StructuredSelection(selected));
        moveTreeViewerSelection(availTree, selTree);
    }
}
