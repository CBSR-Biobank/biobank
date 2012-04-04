package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import edu.ualberta.med.biobank.model.PermissionEnum;

public class PermissionCheckTreeWidget extends Composite {
    private final PermissionsCheckStateHandler permissionsCheckStateHandler =
        new PermissionsCheckStateHandler();
    private final Map<PermissionEnum, PermissionNode> nodes =
        new HashMap<PermissionEnum, PermissionNode>();
    private final Set<PermissionEnum> disabled = new HashSet<PermissionEnum>();

    private ContainerCheckedTreeViewer treeviewer;
    private PermissionRootNode rootNode;

    public PermissionCheckTreeWidget(Composite parent, boolean title,
        List<PermissionEnum> permissions) {
        super(parent, SWT.NONE);

        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.marginTop = 10;
        gl.horizontalSpacing = 5;
        gl.verticalSpacing = 5;
        this.setLayout(gl);

        if (title) {
            Label label = new Label(this, SWT.NONE);
            label.setText("Permissions:");
            GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
        }

        // Label filterLabel = new Label(this, SWT.NONE);
        // filterLabel.setText(Messages.PermissionCheckTreeWidget_show_label);
        // final Combo comboShowRights = new Combo(this, SWT.READ_ONLY);
        // comboShowRights
        // .add(Messages.PermissionCheckTreeWidget_allcenters_label);
        // comboShowRights.add(Messages.PermissionCheckTreeWidget_sites_label);
        // comboShowRights.add(Messages.PermissionCheckTreeWidget_clinics_label);
        // comboShowRights.add(Messages.PermissionCheckTreeWidget_rgs_labels);
        // comboShowRights.select(0);
        // comboShowRights.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
        // false));
        // comboShowRights.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // treeviewer.refresh();
        // }
        // });

        treeviewer = new ContainerCheckedTreeViewer(this);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 160;
        gd.horizontalSpan = 2;
        treeviewer.getTree().setLayoutData(gd);

        treeviewer.setUseHashlookup(true);
        treeviewer.setLabelProvider(new PermissionLabelProvider());
        treeviewer.setContentProvider(new PermissionContentProvider());
        treeviewer.setComparator(new ViewerComparator());

        treeviewer.setInput(buildContent(permissions));
        treeviewer.expandToLevel(AbstractTreeViewer.ALL_LEVELS);

        treeviewer.addCheckStateListener(permissionsCheckStateHandler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        treeviewer.getTree().setEnabled(enabled);
    }

    public void setInput(Collection<PermissionEnum> perms) {
        treeviewer.removeCheckStateListener(permissionsCheckStateHandler);
        try {
            treeviewer.setInput(buildContent(perms));
        } finally {
            treeviewer.addCheckStateListener(permissionsCheckStateHandler);
        }
    }

    private Collection<PermissionNode> buildContent(
        Collection<PermissionEnum> allPermissions) {
        nodes.clear();
        for (PermissionEnum perm : allPermissions) {
            PermissionNode node = new PermissionNode(rootNode, perm);
            nodes.put(perm, node);
        }
        return nodes.values();
    }

    public void setSelections(Collection<PermissionEnum> permissions) {
        treeviewer.removeCheckStateListener(permissionsCheckStateHandler);
        try {
            List<IPermissionCheckTreeNode> checkedNodes =
                new ArrayList<IPermissionCheckTreeNode>();
            for (PermissionEnum permission : permissions) {
                PermissionNode node = nodes.get(permission);
                checkedNodes.add(node);
            }
            treeviewer.setCheckedElements(checkedNodes.toArray());
        } finally {
            treeviewer.addCheckStateListener(permissionsCheckStateHandler);
        }
    }

    public Set<PermissionEnum> getCheckedElements() {
        Set<PermissionEnum> checked = new HashSet<PermissionEnum>();
        for (Object checkedO : treeviewer.getCheckedElements()) {
            if (checkedO instanceof PermissionNode) {
                PermissionNode node = (PermissionNode) checkedO;
                checked.add(node.getPermission());
            }
        }
        return checked;
    }

    public void setDisabled(Set<PermissionEnum> perms) {
        disabled.clear();
        disabled.addAll(perms);

        treeviewer.refresh();
    }

    public boolean hasCheckedItems() {
        return treeviewer.getCheckedElements().length > 0;
    }

    public void addCheckStateListener(ICheckStateListener checkStateListener) {
        treeviewer.addCheckStateListener(checkStateListener);
    }

    public void removeCheckStateListener(ICheckStateListener checkStateListener) {
        treeviewer.removeCheckStateListener(checkStateListener);
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
            if (parentElement instanceof IPermissionCheckTreeNode)
                return ((IPermissionCheckTreeNode) parentElement).getChildren()
                    .toArray();
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof IPermissionCheckTreeNode)
                return ((IPermissionCheckTreeNode) element).getParent();
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof IPermissionCheckTreeNode)
                return getChildren(element).length > 0;
            return false;
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
            if (element instanceof IPermissionCheckTreeNode)
                return ((IPermissionCheckTreeNode) element).getText();
            return "Problem with display"; 
        }
    }

    private class PermissionsCheckStateHandler implements ICheckStateListener {
        @Override
        public void checkStateChanged(CheckStateChangedEvent event) {

            Object element = event.getElement();
            if (!(element instanceof PermissionNode)) return;

            // PermissionNode node = (PermissionNode) event.getElement();
            // PermissionEnum perm = node.getPermission();

        }

        @SuppressWarnings("unused")
        private void updateParent(PermissionRootNode parent) {

        }
    }
}
