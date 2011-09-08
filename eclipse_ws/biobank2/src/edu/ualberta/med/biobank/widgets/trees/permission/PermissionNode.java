package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;

public interface PermissionNode {

    public PermissionNode getParent();

    public Collection<? extends PermissionNode> getChildren();

    public String getText();

    public void setChecked(boolean checked);

    public void setChecked(boolean checked,
        List<PrivilegeWrapper> defaultPrivilegeSelection);

    public boolean isChecked();

    public boolean isGrayed();

}
