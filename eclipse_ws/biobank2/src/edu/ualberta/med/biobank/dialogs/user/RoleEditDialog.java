package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.infotables.RightPrivilegeInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RoleEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private RoleWrapper role;
    private RightPrivilegeInfoTable rightPrivilegeInfoTable;

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

        Section rpSection = createSection(contents,
            Messages.RoleEditDialog_right_privilege_label, Messages.RoleEditDialog_new_assoc_label,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addRightPrivilege();
                }
            });

        rightPrivilegeInfoTable = new RightPrivilegeInfoTable(rpSection, role);
        rpSection.setClient(rightPrivilegeInfoTable);
    }

    protected void addRightPrivilege() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                RightPrivilegeAddDialog dlg = new RightPrivilegeAddDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), role);
                int res = dlg.open();
                if (res == Status.OK) {
                    rightPrivilegeInfoTable.getCollection().addAll(
                        dlg.getRightPrivilegeList());
                    rightPrivilegeInfoTable.reloadCollection(
                        role.getRightPrivilegeCollection(true), null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        try {
            role.persist();
            close();
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
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