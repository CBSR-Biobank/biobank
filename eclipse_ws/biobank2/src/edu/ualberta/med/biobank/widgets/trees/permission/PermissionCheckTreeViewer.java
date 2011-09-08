package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PermissionCheckTreeViewer extends ContainerCheckedTreeViewer {

    private List<PrivilegeWrapper> defaultPrivilegeSelection = null;

    public PermissionCheckTreeViewer(Composite parent) {
        super(parent);
    }

    public void setDefaultPrivilegeSelection(
        List<PrivilegeWrapper> defaultPrivilegeSelection) {
        this.defaultPrivilegeSelection = defaultPrivilegeSelection;
    }

    /**
     * Update element after a checkstate change.
     * 
     * @param element
     */
    @Override
    protected void doCheckStateChanged(Object element) {
        Widget item = findItem(element);
        if (item instanceof TreeItem) {
            TreeItem treeItem = (TreeItem) item;
            treeItem.setGrayed(false);
            updateChildrenItems(treeItem);
            updateParentItems(treeItem.getParentItem());
        }
    }

    /**
     * Updates the check state of all created children
     */
    private void updateChildrenItems(TreeItem parent) {
        Item[] children = getChildren(parent);
        boolean state = parent.getChecked();
        for (int i = 0; i < children.length; i++) {
            TreeItem curr = (TreeItem) children[i];
            if (curr.getData() != null
                && ((curr.getChecked() != state) || curr.getGrayed())) {
                if (!state
                    || defaultPrivilegeSelection == null
                    || !(curr.getData() instanceof PrivilegeNode)
                    || defaultPrivilegeSelection.contains(((PrivilegeNode) curr
                        .getData()).getPrivilege())) {
                    curr.setChecked(state);
                    curr.setGrayed(false);
                    updateChildrenItems(curr);
                }
            }
        }
    }

    /**
     * Updates the check / gray state of all parent items
     */
    private void updateParentItems(TreeItem item) {
        if (item != null) {
            Item[] children = getChildren(item);
            boolean containsChecked = false;
            boolean containsUnchecked = false;
            for (int i = 0; i < children.length; i++) {
                TreeItem curr = (TreeItem) children[i];
                containsChecked |= curr.getChecked();
                containsUnchecked |= (!curr.getChecked() || curr.getGrayed());
            }
            item.setChecked(containsChecked);
            item.setGrayed(containsChecked && containsUnchecked);
            updateParentItems(item.getParentItem());
        }
    }

    /**
     * The item has expanded. Updates the checked state of its children.
     */
    private void initializeItem(TreeItem item) {
        if (item.getChecked() && !item.getGrayed()) {
            updateChildrenItems(item);
        }
    }

    @Override
    protected void setExpanded(Item item, boolean expand) {
        super.setExpanded(item, expand);
        if (expand && item instanceof TreeItem) {
            initializeItem((TreeItem) item);
        }
    }
}
