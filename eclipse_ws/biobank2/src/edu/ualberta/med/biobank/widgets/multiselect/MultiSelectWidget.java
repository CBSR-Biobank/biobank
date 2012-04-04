package edu.ualberta.med.biobank.widgets.multiselect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
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

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.widgets.listeners.TreeViewerDragListener;
import edu.ualberta.med.biobank.widgets.listeners.TreeViewerDropListener;

public abstract class MultiSelectWidget<T> extends BgcBaseWidget {

    private TreeViewer selTree;

    private TreeViewer availTree;

    private Button moveRightButton;

    private Button moveLeftButton;

    private MultiSelectNode<T> selTreeRootNode = new MultiSelectNode<T>(null,
        null);

    private MultiSelectNode<T> availTreeRootNode = new MultiSelectNode<T>(null,
        null);

    private int minHeight;

    protected boolean ctrl;

    private List<T> allObjectsList = new ArrayList<T>();

    private Transfer dndTransfer;

    /**
     * Default will drag and drop for ModelWrapper. Should use the other
     * constructor if want to use another one
     */
    public MultiSelectWidget(Composite parent, int style, String leftLabel,
        String rightLabel, int minHeight) {
        this(parent, style, leftLabel, rightLabel, minHeight,
            MultiSelectNodeTransfer.getInstance());
    }

    public MultiSelectWidget(Composite parent, int style, String leftLabel,
        String rightLabel, int minHeight, ByteArrayTransfer dndTransfer) {
        super(parent, style);
        this.dndTransfer = dndTransfer;

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
        moveRightButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ARROW_RIGHT));
        moveRightButton.setToolTipText("Move to selected");
        moveLeftButton = new Button(moveComposite, SWT.PUSH);
        moveLeftButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ARROW_LEFT));
        moveLeftButton
            .setToolTipText("Remove from selected");

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

    public void addSelections(Collection<T> allObjects,
        Collection<T> selectedObjects) {
        allObjectsList.addAll(allObjects);
        for (T o : allObjects) {
            if (selectedObjects.contains(o)) {
                selTreeRootNode.addChild(new MultiSelectNode<T>(
                    selTreeRootNode, o));
            } else {
                availTreeRootNode.addChild(new MultiSelectNode<T>(
                    availTreeRootNode, o));
            }
        }
    }

    /**
     * same as addSelections but remove previously set elements
     */
    public void setSelections(Collection<T> allObjects,
        Collection<T> selectedObjects) {
        selTreeRootNode.clear();
        availTreeRootNode.clear();
        addSelections(allObjects, selectedObjects);
        selTreeRootNode.reset();
        availTreeRootNode.reset();
    }

    public void setFilter(ViewerFilter filter) {
        ViewerFilter[] filters = new ViewerFilter[] { filter };
        availTree.setFilters(filters);
        selTree.setFilters(filters);
    }

    @SuppressWarnings("unchecked")
    private void moveTreeViewerSelection(TreeViewer srcTree, TreeViewer destTree) {
        MultiSelectNode<T> srcRootNode = (MultiSelectNode<T>) srcTree
            .getInput();
        MultiSelectNode<T> destRootNode = (MultiSelectNode<T>) destTree
            .getInput();
        List<?> fromSelection = ((IStructuredSelection) srcTree.getSelection())
            .toList();

        for (Object obj : fromSelection) {
            MultiSelectNode<T> node = (MultiSelectNode<T>) obj;
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

        tv.setLabelProvider(new LabelProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public String getText(Object element) {
                MultiSelectNode<T> node = ((MultiSelectNode<T>) element);
                return getTextForObject(node.getNodeObject());
            }
        });
        tv.setContentProvider(new MultiSelectNodeContentProvider<T>());

        return tv;
    }

    protected abstract String getTextForObject(T nodeObject);

    private void dragAndDropSupport(TreeViewer fromList, TreeViewer toList) {
        new TreeViewerDragListener<T>(fromList, dndTransfer);
        new TreeViewerDropListener<T>(toList, this);
    }

    /**
     * Return the selected items in the order specified by user.
     * 
     */
    public List<T> getSelected() {
        List<T> result = new ArrayList<T>();
        for (MultiSelectNode<T> node : selTreeRootNode.getChildren()) {
            result.add(node.getNodeObject());
        }
        return result;
    }

    public List<T> getAddedToSelection() {
        List<T> result = new ArrayList<T>();
        for (MultiSelectNode<T> node : selTreeRootNode.getAddedChildren()) {
            result.add(node.getNodeObject());
        }
        return result;
    }

    public List<T> getRemovedFromSelection() {
        List<T> result = new ArrayList<T>();
        for (MultiSelectNode<T> node : selTreeRootNode.getRemovedChildren()) {
            result.add(node.getNodeObject());
        }
        return result;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selTree.getControl().setEnabled(enabled);
        availTree.getControl().setEnabled(enabled);
    }

    public void refreshLists() {
        availTree.refresh();
        selTree.refresh();
    }

    public Transfer getDndTransfer() {
        return dndTransfer;
    }

}
