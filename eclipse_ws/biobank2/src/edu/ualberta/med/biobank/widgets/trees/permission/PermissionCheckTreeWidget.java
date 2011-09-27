package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PermissionCheckTreeWidget extends Composite {

    private PermissionCheckTreeViewer treeviewer;

    private PermissionRootNode rootNode;

    private List<PrivilegeWrapper> allPossiblePrivileges;

    public PermissionCheckTreeWidget(Composite parent, boolean title,
        List<BbRightWrapper> allRights) {
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
            label.setText(Messages.PermissionCheckTree_title);
            GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
        }

        Label filterLabel = new Label(this, SWT.NONE);
        filterLabel.setText(Messages.PermissionCheckTreeWidget_show_label);
        final Combo comboShowRights = new Combo(this, SWT.READ_ONLY);
        comboShowRights.add(Messages.PermissionCheckTreeWidget_allcenters_label);
        comboShowRights.add(Messages.PermissionCheckTreeWidget_sites_label);
        comboShowRights.add(Messages.PermissionCheckTreeWidget_clinics_label);
        comboShowRights.add(Messages.PermissionCheckTreeWidget_rgs_labels);
        comboShowRights.select(0);
        comboShowRights.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
            false));
        comboShowRights.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                treeviewer.refresh();
            }
        });

        // Label privilegeFilterLabel = new Label(this, SWT.NONE);
        // privilegeFilterLabel.setText("Privilege default selection:");
        // final ListViewer privilegeViewer = new ListViewer(this, SWT.READ_ONLY
        // | SWT.MULTI);
        // privilegeViewer.getList().setLayoutData(
        // new GridData(SWT.FILL, SWT.NONE, true, false));
        // privilegeViewer.setContentProvider(new ArrayContentProvider());
        // privilegeViewer.setLabelProvider(new LabelProvider() {
        // @Override
        // public String getText(Object element) {
        // return ((PrivilegeWrapper) element).getName();
        // }
        // });
        // privilegeViewer
        // .addSelectionChangedListener(new ISelectionChangedListener() {
        // @SuppressWarnings("unchecked")
        // @Override
        // public void selectionChanged(SelectionChangedEvent event) {
        // treeviewer
        // .setDefaultPrivilegeSelection(((IStructuredSelection) privilegeViewer
        // .getSelection()).toList());
        // }
        // });
        // privilegeViewer.setComparator(new ViewerComparator());
        // privilegeFilterLabel.setVisible(false);
        // privilegeViewer.getList().setVisible(false);

        treeviewer = new PermissionCheckTreeViewer(this);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 300;
        gd.horizontalSpan = 2;
        treeviewer.getTree().setLayoutData(gd);

        treeviewer.setInput(buildContent(allRights));
        treeviewer.expandToLevel(2);
        // privilegeViewer.setInput(allPossiblePrivileges);
        // privilegeViewer.setSelection(new StructuredSelection(
        // allPossiblePrivileges));

        treeviewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                if (element instanceof RightNode) {
                    RightNode node = (RightNode) element;
                    switch (comboShowRights.getSelectionIndex()) {
                    case 1:
                        // all
                        return true;
                    case 2:
                        // sites
                        return node.getRight().isForSite();
                    case 3:
                        // clinics
                        return node.getRight().isForClinic();
                    case 4:
                        // research groups
                        return node.getRight().isForResearchGroup();
                    }
                    return true;
                }
                return true;
            }
        });
    }

    private List<PermissionRootNode> buildContent(List<BbRightWrapper> allRights) {
        rootNode = new PermissionRootNode();
        Set<PrivilegeWrapper> allPossiblePrivilegesSet = new HashSet<PrivilegeWrapper>();
        final Map<BbRightWrapper, RightNode> nodes = new HashMap<BbRightWrapper, RightNode>();
        for (BbRightWrapper r : allRights) {
            RightNode node = new RightNode(rootNode, r);
            nodes.put(r, node);
            for (PrivilegeNode pNode : node.getChildren()) {
                allPossiblePrivilegesSet.add(pNode.getPrivilege());
            }
        }
        this.allPossiblePrivileges = new ArrayList<PrivilegeWrapper>(
            allPossiblePrivilegesSet);
        rootNode.setChildren(nodes);
        return Arrays.asList(rootNode);
    }

    public void setSelections(List<PermissionWrapper> permissions) {
        List<PermissionNode> checkedNodes = new ArrayList<PermissionNode>();
        for (PermissionWrapper permission : permissions) {
            RightNode rNode = rootNode.getNode(permission.getRight());
            rNode.setPermission(permission);
            for (PrivilegeWrapper privilege : permission
                .getPrivilegeCollection(false)) {
                PrivilegeNode pNode = rNode.getNode(privilege);
                if (pNode != null) {
                    pNode.setChecked(true);
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
            for (RightNode rightNode : rootNode.getChildren()) {
                if (rightNode.isChecked() || rightNode.isGrayed()) {
                    List<PrivilegeWrapper> addedPrivilege = new ArrayList<PrivilegeWrapper>();
                    List<PrivilegeWrapper> removedPrivilege = new ArrayList<PrivilegeWrapper>();
                    for (PrivilegeNode p : rightNode.getChildren()) {
                        if (p.isChecked())
                            addedPrivilege.add(p.getPrivilege());
                        else
                            removedPrivilege.add(p.getPrivilege());
                    }
                    rightNode.getPermission().addToPrivilegeCollection(
                        addedPrivilege);
                    rightNode.getPermission().removeFromPrivilegeCollection(
                        removedPrivilege);
                    if (rightNode.getPermission().isNew())
                        addedPermissions.add(rightNode.getPermission());
                } else {
                    if (!rightNode.getPermission().isNew())
                        deletedPermissions.add(rightNode.getPermission());
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
