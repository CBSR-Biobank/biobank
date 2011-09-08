package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

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

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, null);
    }

    @Override
    public void setChecked(boolean checked,
        List<PrivilegeWrapper> defaultPrivilegeSelection) {
        for (RightNode child : getChildren())
            child.setChecked(checked, defaultPrivilegeSelection);
    }

    @Override
    public boolean isChecked() {
        int count = countCheckedChildren();
        return count == getChildren().size();
    }

    @Override
    public boolean isGrayed() {
        int countGrayed = 0;
        int countChecked = 0;
        for (RightNode child : getChildren()) {
            if (child.isGrayed())
                countGrayed++;
            if (child.isChecked())
                countChecked++;
        }
        return countGrayed > 0 || countChecked > 0
            && countChecked < getChildren().size();
    }

    protected int countCheckedChildren() {
        int count = 0;
        for (RightNode child : getChildren()) {
            if (child.isChecked())
                count++;
        }
        return count;
    }

    protected int countGrayedChildren() {
        int count = 0;
        for (RightNode child : getChildren()) {
            if (child.isGrayed())
                count++;
        }
        return count;
    }

}
