package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;

public class PermissionRootNode implements PermissionNode {

    private Map<BbRightWrapper, RightNode> children = new HashMap<BbRightWrapper, RightNode>();

    @Override
    public String getText() {
        return Messages.PermissionRootNode_text;
    }

    @Override
    public PermissionNode getParent() {
        return null;
    }

    @Override
    public Collection<RightNode> getChildren() {
        return children.values();
    }

    public void setChildren(Map<BbRightWrapper, RightNode> children) {
        this.children = children;
    }

    public RightNode getNode(BbRightWrapper right) {
        return children.get(right);
    }

}
