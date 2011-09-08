package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public class PrivilegeNode implements PermissionNode {

    private RightNode parent;
    private PrivilegeWrapper privilege;
    private boolean checked;

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

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, null);
    }

    @Override
    public void setChecked(boolean checked,
        List<PrivilegeWrapper> defaultPrivilegeSelection) {
        if (checked && defaultPrivilegeSelection != null)
            this.checked = defaultPrivilegeSelection.contains(privilege);
        else
            this.checked = checked;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public boolean isGrayed() {
        return false;
    }

}
