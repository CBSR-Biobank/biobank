package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.security.RoleSaveAction;
import edu.ualberta.med.biobank.common.action.security.RoleSaveInput;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget.PermissionTreeRes;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RoleEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private final RoleWrapper roleWrapper;
    private final Role role;
    private final Role originalRole;

    private PermissionCheckTreeWidget tree;

    public RoleEditDialog(Shell parent, Role role) {
        super(parent);

        BiobankApplicationService service = SessionManager.getAppService();

        this.role = role;
        this.originalRole = new Role();

        copyRole(role, originalRole);

        this.roleWrapper = new RoleWrapper(service, role);

        if (role.isNew()) {
            currentTitle = Messages.RoleEditDialog_title_add;
            titleAreaMessage = Messages.RoleEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.RoleEditDialog_title_edit;
            titleAreaMessage = Messages.RoleEditDialog_titlearea_modify;
        }
    }

    private static final void copyRole(Role src, Role dst) {
        dst.setName(dst.getName());
        dst.getPermissions().addAll(src.getPermissions());
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
            Messages.RoleEditDialog_property_title_name, null, roleWrapper,
            RolePeer.NAME.getName(), new NonEmptyStringValidator(
                Messages.RoleEditDialog_msg_name_required));

        tree = new PermissionCheckTreeWidget(contents, true,
            PermissionEnum.valuesList());
        tree.setSelections(role.getPermissions());

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        tree.setLayoutData(gd);
    }

    @Override
    protected void okPressed() {
        try {
            PermissionTreeRes res = tree.getAddedAndRemovedNodes();
            role.getPermissions().addAll(res.addedPermissions);

            IdResult result = SessionManager.getAppService().doAction(
                new RoleSaveAction(new RoleSaveInput(role)));

            role.setId(result.getId());

            close();
        } catch (Throwable t) {
            TmpUtil.displayException(t);
        }
    }

    @Override
    protected void cancelPressed() {
        copyRole(originalRole, role);
        super.cancelPressed();
    }
}