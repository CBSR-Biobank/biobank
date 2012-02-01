package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget.PermissionTreeRes;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RoleEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private RoleWrapper role;
    private PermissionCheckTreeWidget tree;

    public RoleEditDialog(Shell parent, RoleWrapper role) {
        super(parent);
        Assert.isNotNull(role);
        this.role = role;
        if (role.isNew()) {
            currentTitle = Messages.RoleEditDialog_title_add;
            titleAreaMessage = Messages.RoleEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.RoleEditDialog_title_edit;
            titleAreaMessage = Messages.RoleEditDialog_titlearea_modify;
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return titleAreaMessage;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.RoleEditDialog_property_title_name, null, role,
            RolePeer.NAME.getName(), new NonEmptyStringValidator(
                Messages.RoleEditDialog_msg_name_required));

        tree = new PermissionCheckTreeWidget(contents, true, PermissionEnum.valuesList());
        tree.setSelections(role.getPermissionCollection());

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        tree.setLayoutData(gd);
    }

    @Override
    protected void okPressed() {
        try {
            PermissionTreeRes res = tree.getAddedAndRemovedNodes();
            role.addToPermissionCollection(res.addedPermissions);
            role.removeFromPermissionCollection(res.removedPermissions);
            role.persist();
            close();
        } catch (Exception e) {
            if (e.getMessage() != null
                && e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.RoleEditDialog_msg_persit_error,
                    Messages.RoleEditDialog_msg_error_name_used);
            } else {
                BgcPlugin.openAsyncError(
                    Messages.RoleEditDialog_msg_persit_error, e);
            }
        }
    }

    @Override
    protected void cancelPressed() {
        try {
            role.reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.cancelPressed();
    }
}