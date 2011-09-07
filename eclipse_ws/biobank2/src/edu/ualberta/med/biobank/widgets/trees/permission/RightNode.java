package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class RightNode implements PermissionNode {

    private BbRightWrapper right;
    private PermissionNode parent;
    private Map<PrivilegeWrapper, PrivilegeNode> children;
    private PermissionWrapper permission;

    public RightNode(PermissionNode parent, BbRightWrapper right) {
        this.parent = parent;
        this.right = right;
        initPermission(right);
    }

    private void initPermission(BbRightWrapper right) {
        permission = new PermissionWrapper(right.getAppService());
        permission.setRight(right);
    }

    @Override
    public PermissionNode getParent() {
        return parent;
    }

    @Override
    public Collection<PrivilegeNode> getChildren() {
        if (children == null) {
            children = new HashMap<PrivilegeWrapper, PrivilegeNode>();
            for (PrivilegeWrapper p : right
                .getAvailablePrivilegeCollection(false)) {
                children.put(p, new PrivilegeNode(this, p));
            }
        }
        return children.values();
    }

    public BbRightWrapper getRight() {
        return right;
    }

    @Override
    public String getText() {
        return right.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RightNode) {
            RightNode r = (RightNode) o;
            return getRight().equals(r.getRight());
        }
        return false;
    }

    public PermissionWrapper getPermission() {
        return permission;
    }

    public void setPermission(PermissionWrapper p) {
        this.permission = p;
    }

    @Override
    public String toString() {
        return "Right=" //$NON-NLS-1$
            + right.toString()
            + (permission == null ? "" : (" Permission=" + permission //$NON-NLS-1$ //$NON-NLS-2$
                .toString()));
    }

    public PrivilegeNode getNode(PrivilegeWrapper p) {
        return children.get(p);
    }

}
