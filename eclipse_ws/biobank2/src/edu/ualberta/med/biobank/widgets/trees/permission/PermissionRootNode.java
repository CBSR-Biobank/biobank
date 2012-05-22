package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.model.PermissionEnum;

public class PermissionRootNode implements IPermissionCheckTreeNode {
    private static final I18n i18n = I18nFactory
        .getI18n(PermissionRootNode.class);

    private Map<PermissionEnum, PermissionNode> children =
        new HashMap<PermissionEnum, PermissionNode>();

    @SuppressWarnings("nls")
    @Override
    public String getText() {
        return i18n.tr("All permissions");
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
