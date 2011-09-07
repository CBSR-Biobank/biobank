package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.List;

public interface PermissionNode {

    public PermissionNode getParent();

    public List<PermissionNode> getChildren();

    public String getText();
}
