package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.Collections;

import edu.ualberta.med.biobank.model.PermissionEnum;

public class PermissionNode implements IPermissionCheckTreeNode {
    private PermissionEnum permission;
    private final IPermissionCheckTreeNode parent;

    public PermissionNode(IPermissionCheckTreeNode parent,
        PermissionEnum permission) {
        this.parent = parent;
        this.permission = permission;
    }

    @Override
    public IPermissionCheckTreeNode getParent() {
        return parent;
    }

    public PermissionEnum getPermission() {
        return permission;
    }

    @Override
    public String getText() {
        return permission.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PermissionNode) {
            PermissionNode r = (PermissionNode) o;
            return getPermission().equals(r.getPermission());
        }
        return false;
    }

    public void setPermission(PermissionEnum p) {
        this.permission = p;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Permission="
            + permission.toString();
    }

    @Override
    public Collection<? extends IPermissionCheckTreeNode> getChildren() {
        return Collections.emptyList();
    }

}
