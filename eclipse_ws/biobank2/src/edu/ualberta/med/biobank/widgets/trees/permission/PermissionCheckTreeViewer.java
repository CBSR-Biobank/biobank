package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PermissionCheckTreeViewer extends CheckboxTreeViewer {

    private List<PrivilegeWrapper> defaultPrivilegeSelection = null;

    public PermissionCheckTreeViewer(Composite parent) {
        super(parent);
        initViewer();
    }

    public void setDefaultPrivilegeSelection(
        List<PrivilegeWrapper> defaultPrivilegeSelection) {
        this.defaultPrivilegeSelection = defaultPrivilegeSelection;
    }

    private void initViewer() {
        setUseHashlookup(true);
        addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                doCheckStateChanged(event.getElement(),
                    defaultPrivilegeSelection);
            }
        });
        addTreeListener(new ITreeViewerListener() {
            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
            }

            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                Widget item = findItem(event.getElement());
                if (item instanceof TreeItem) {
                    initializeItem((TreeItem) item);
                }
            }
        });
        this.setLabelProvider(new PermissionLabelProvider());
        this.setContentProvider(new PermissionContentProvider());
        this.setComparator(new ViewerComparator());
        this.setCheckStateProvider(new ICheckStateProvider() {

            @Override
            public boolean isGrayed(Object element) {
                return ((PermissionNode) element).isGrayed();
            }

            @Override
            public boolean isChecked(Object element) {
                return ((PermissionNode) element).isChecked();
            }
        });
    }

    /**
     * Update element after a checkstate change.
     * 
     * @param element
     */
    protected void doCheckStateChanged(Object element,
        List<PrivilegeWrapper> defaultPrivilegeSelection) {
        Widget item = findItem(element);
        if (item instanceof TreeItem) {
            TreeItem treeItem = (TreeItem) item;
            PermissionNode node = (PermissionNode) element;
            node.setChecked(treeItem.getChecked(), defaultPrivilegeSelection);
            treeItem.setGrayed(node.isGrayed());
            updateChildrenItems(treeItem);
            updateParentItems(treeItem.getParentItem());
        }
    }

    /**
     * The item has expanded. Updates the checked state of its children.
     */
    private void initializeItem(TreeItem item) {
        updateChildrenItems(item);
    }

    /**
     * Updates the check state of all created children
     */
    private void updateChildrenItems(TreeItem parent) {
        Item[] children = getChildren(parent);
        for (int i = 0; i < children.length; i++) {
            TreeItem curr = (TreeItem) children[i];
            if (curr.getData() != null) {
                PermissionNode node = (PermissionNode) curr.getData();
                boolean grayed = node.isGrayed();
                curr.setChecked(grayed || node.isChecked());
                curr.setGrayed(grayed);
                updateChildrenItems(curr);
            }
        }
    }

    /**
     * Updates the check / gray state of all parent items
     */
    private void updateParentItems(TreeItem item) {
        if (item != null) {
            PermissionNode node = (PermissionNode) item.getData();
            boolean grayed = node.isGrayed();
            item.setChecked(grayed || node.isChecked());
            item.setGrayed(grayed);
            updateParentItems(item.getParentItem());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICheckable#setChecked(java.lang.Object,
     * boolean)
     */
    @Override
    public boolean setChecked(Object element, boolean state) {
        if (super.setChecked(element, state)) {
            doCheckStateChanged(element, null);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.CheckboxTreeViewer#setCheckedElements(java.
     * lang.Object[])
     */
    @Override
    public void setCheckedElements(Object[] elements) {
        super.setCheckedElements(elements);
        for (int i = 0; i < elements.length; i++) {
            doCheckStateChanged(elements[i], null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.AbstractTreeViewer#setExpanded(org.eclipse.
     * swt.widgets.Item, boolean)
     */
    @Override
    protected void setExpanded(Item item, boolean expand) {
        super.setExpanded(item, expand);
        if (expand && item instanceof TreeItem) {
            initializeItem((TreeItem) item);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.CheckboxTreeViewer#getCheckedElements()
     */
    @Override
    public Object[] getCheckedElements() {
        Object[] checked = super.getCheckedElements();
        // add all items that are children of a checked node but not created yet
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < checked.length; i++) {
            Object curr = checked[i];
            result.add(curr);
            Widget item = findItem(curr);
            if (item != null) {
                Item[] children = getChildren(item);
                // check if contains the dummy node
                if (children.length == 1 && children[0].getData() == null) {
                    // not yet created
                    collectChildren(curr, result);
                }
            }
        }
        return result.toArray();
    }

    /**
     * Recursively add the filtered children of element to the result.
     * 
     * @param element
     * @param result
     */
    private void collectChildren(Object element, ArrayList<Object> result) {
        Object[] filteredChildren = getFilteredChildren(element);
        for (int i = 0; i < filteredChildren.length; i++) {
            Object curr = filteredChildren[i];
            result.add(curr);
            collectChildren(curr, result);
        }
    }

    public class PermissionLabelProvider implements ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof PermissionNode)
                return ((PermissionNode) element).getText();
            return "Problem with display"; //$NON-NLS-1$
        }
    }

    public class PermissionContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Object[]) {
                return (Object[]) inputElement;
            }
            if (inputElement instanceof Collection) {
                return ((Collection) inputElement).toArray();
            }
            return new Object[0];
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof PermissionNode)
                return ((PermissionNode) parentElement).getChildren().toArray();
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof PermissionNode)
                return ((PermissionNode) element).getParent();
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof PermissionNode)
                return getChildren(element).length > 0;
            return false;
        }
    }

}
