package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;

public interface PermissionNode {

    public PermissionNode getParent();

    public Collection<? extends PermissionNode> getChildren();

    public String getText();

}
