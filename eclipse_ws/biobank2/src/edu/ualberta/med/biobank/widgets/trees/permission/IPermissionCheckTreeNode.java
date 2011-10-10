package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;

public interface IPermissionCheckTreeNode {

    public IPermissionCheckTreeNode getParent();

    public Collection<? extends IPermissionCheckTreeNode> getChildren();

    public String getText();

}
