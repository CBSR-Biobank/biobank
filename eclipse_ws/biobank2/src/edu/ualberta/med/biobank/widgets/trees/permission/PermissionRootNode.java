package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.model.PermissionEnum;

public class PermissionRootNode implements IPermissionCheckTreeNode {

    private Map<PermissionEnum, PermissionNode> children = new HashMap<PermissionEnum, PermissionNode>();

    @Override
    public String getText() {
        return "All permissions";
    }

    @Override
    public IPermissionCheckTreeNode getParent() {
        return null;
    }

    @Override
    public Collection<PermissionNode> getChildren() {
        return children.values();
    }

    public void setChildren(Map<PermissionEnum, PermissionNode> children) {
        this.children = children;
    }

    public PermissionNode getNode(PermissionEnum right) {
        return children.get(right);
    }

}
