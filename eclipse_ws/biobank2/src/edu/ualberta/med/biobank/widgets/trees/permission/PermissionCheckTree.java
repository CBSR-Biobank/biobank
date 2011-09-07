package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PermissionCheckTree extends Composite {

    private ContainerCheckedTreeViewer treeviewer;

    private PermissionRootNode rootNode;

    public PermissionCheckTree(Composite parent, boolean title,
        List<BbRightWrapper> allRights) {
        super(parent, SWT.NONE);
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.marginTop = 10;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 5;
        this.setLayout(gl);

        if (title) {
            Label label = new Label(this, SWT.NONE);
            label.setText(Messages.PermissionCheckTree_title);
        }

        treeviewer = new ContainerCheckedTreeViewer(this);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 300;
        treeviewer.getTree().setLayoutData(gd);

        treeviewer.setLabelProvider(new PermissionLabelProvider());
        treeviewer.setContentProvider(new PermissionContentProvider());
        treeviewer.setComparator(new ViewerComparator());

        treeviewer.setInput(buildContent(allRights));
        treeviewer.expandToLevel(2);

    }

    private List<PermissionRootNode> buildContent(List<BbRightWrapper> allRights) {
        rootNode = new PermissionRootNode();
        final Map<BbRightWrapper, RightNode> nodes = new HashMap<BbRightWrapper, RightNode>();
        for (BbRightWrapper r : allRights) {
            nodes.put(r, new RightNode(rootNode, r));
        }
        rootNode.setChildren(nodes);
        return Arrays.asList(rootNode);
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

    public void setSelections(List<PermissionWrapper> permissions) {
        List<PermissionNode> checkedNodes = new ArrayList<PermissionNode>();
        for (PermissionWrapper permission : permissions) {
            RightNode rNode = rootNode.getNode(permission.getRight());
            rNode.setPermission(permission);
            for (PrivilegeWrapper privilege : permission
                .getPrivilegeCollection(false)) {
                PrivilegeNode pNode = rNode.getNode(privilege);
                if (pNode == null) {
                    // probably means this privilege can't be set to this
                }
                checkedNodes.add(pNode);
            }
        }
        treeviewer.setCheckedElements(checkedNodes.toArray());
    }

    public PermissionTreeRes getAddedAndRemovedNodes() {
        PermissionTreeRes res = new PermissionTreeRes();
        res.buildRes();
        return res;
    }

    public class PermissionTreeRes {
        public List<PermissionWrapper> addedPermissions = new ArrayList<PermissionWrapper>();
        public List<PermissionWrapper> deletedPermissions = new ArrayList<PermissionWrapper>();

        public void buildRes() {
            for (Object o : rootNode.getChildren()) {
                if (o instanceof RightNode) {
                    RightNode r = (RightNode) o;
                    if (treeviewer.getChecked(r)) {
                        List<PrivilegeWrapper> addedPrivilege = new ArrayList<PrivilegeWrapper>();
                        List<PrivilegeWrapper> removedPrivilege = new ArrayList<PrivilegeWrapper>();
                        for (PrivilegeNode p : r.getChildren()) {
                            if (treeviewer.getChecked(p))
                                addedPrivilege.add(p.getPrivilege());
                            else
                                removedPrivilege.add(p.getPrivilege());
                        }
                        r.getPermission().addToPrivilegeCollection(
                            addedPrivilege);
                        r.getPermission().removeFromPrivilegeCollection(
                            removedPrivilege);
                        if (r.getPermission().isNew())
                            addedPermissions.add(r.getPermission());
                    } else {
                        if (!r.getPermission().isNew())
                            deletedPermissions.add(r.getPermission());
                    }
                }
            }
        }
    }

    public boolean hasCheckedItems() {
        return treeviewer.getCheckedElements().length > 0;
    }

    public void addCheckedListener(ICheckStateListener checkStateListener) {
        treeviewer.addCheckStateListener(checkStateListener);
    }
}
