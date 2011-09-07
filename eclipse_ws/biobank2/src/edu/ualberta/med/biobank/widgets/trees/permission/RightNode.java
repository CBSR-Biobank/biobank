package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class RightNode implements PermissionNode {

    private BbRightWrapper right;
    private PermissionNode parent;
    private List<PermissionNode> children;

    public RightNode(PermissionNode parent, BbRightWrapper right) {
        this.parent = parent;
        this.right = right;

    }

    @Override
    public PermissionNode getParent() {
        return parent;
    }

    @Override
    public List<PermissionNode> getChildren() {
        if (children == null) {
            children = new ArrayList<PermissionNode>();
            for (PrivilegeWrapper p : right
                .getAvailablePrivilegeCollection(false)) {
                children.add(new PrivilegeNode(this, p));
            }
        }
        return children;
    }

    public BbRightWrapper getRight() {
        return right;
    }

    @Override
    public String getText() {
        return right.getName();
    }
}
