package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.Collections;

import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;

public class PermissionNode implements IPermissionCheckTreeNode {

    private PermissionWrapper permission;
    private IPermissionCheckTreeNode parent;

    public PermissionNode(IPermissionCheckTreeNode parent,
        PermissionWrapper permission) {
        this.parent = parent;
        this.permission = permission;
    }

    @Override
    public IPermissionCheckTreeNode getParent() {
        return parent;
    }

    public PermissionWrapper getPermission() {
        return permission;
    }

    @Override
    public String getText() {
        return permission.getClassName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PermissionNode) {
            PermissionNode r = (PermissionNode) o;
            return getPermission().equals(r.getPermission());
        }
        return false;
    }

    public void setPermission(PermissionWrapper p) {
        this.permission = p;
    }

    @Override
    public String toString() {
        return "Permission=" //$NON-NLS-1$
            + permission.toString();
    }

    @Override
    public Collection<? extends IPermissionCheckTreeNode> getChildren() {
        return Collections.emptyList();
    }

}
