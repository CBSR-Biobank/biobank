package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PrivilegeNode implements PermissionNode {

    private RightNode parent;
    private PrivilegeWrapper privilege;

    public PrivilegeNode(RightNode parent, PrivilegeWrapper privilege) {
        this.parent = parent;
        this.privilege = privilege;
    }

    @Override
    public RightNode getParent() {
        return parent;
    }

    @Override
    public Collection<PermissionNode> getChildren() {
        return new ArrayList<PermissionNode>();
    }

    public PrivilegeWrapper getPrivilege() {
        return privilege;
    }

    @Override
    public String getText() {
        return privilege.getName();
    }

    @Override
    public String toString() {
        return "Privilege=" + privilege.toString(); //$NON-NLS-1$
    }

}
