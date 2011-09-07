package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PrivilegeNode implements PermissionNode {

    private PermissionNode parent;
    private PrivilegeWrapper privilege;

    public PrivilegeNode(PermissionNode parent, PrivilegeWrapper privilege) {
        this.parent = parent;
        this.privilege = privilege;
    }

    @Override
    public PermissionNode getParent() {
        return parent;
    }

    @Override
    public List<PermissionNode> getChildren() {
        return new ArrayList<PermissionNode>();
    }

    public PrivilegeWrapper getPrivilege() {
        return privilege;
    }

    @Override
    public String getText() {
        return privilege.getName();
    }
}
