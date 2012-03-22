package edu.ualberta.med.biobank.dialogs.user;

import javax.validation.ConstraintViolationException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.client.util.BiobankProxyHelperImpl;
import edu.ualberta.med.biobank.common.action.security.GroupSaveAction;
import edu.ualberta.med.biobank.common.action.security.GroupSaveInput;
import edu.ualberta.med.biobank.common.peer.GroupPeer;
import edu.ualberta.med.biobank.common.wrappers.GroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private GroupWrapper group;
    private MembershipInfoTable membershipInfoTable;
    private MultiSelectWidget<UserWrapper> usersWidget;

    public GroupEditDialog(Shell parent, GroupWrapper originalGroup) {
        super(parent);
        Assert.isNotNull(originalGroup);
        this.group = originalGroup;

        if (originalGroup.isNew()) {
            currentTitle = Messages.GroupEditDialog_title_add;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.GroupEditDialog_title_edit;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_modify;
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
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabFolder tb = new TabFolder(contents, SWT.TOP);
        tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createGeneralFields(createTabItem(tb,
            Messages.GroupEditDialog_general_tab_title, 2));

        createMembershipsSection(createTabItem(tb,
            Messages.UserEditDialog_roles_permissions_title, 1));

        createUsersSection(createTabItem(tb,
            Messages.GroupEditDialog_users_tab_title, 1));

    }

    private void createGeneralFields(Composite createTabItem) {
        createBoundWidgetWithLabel(createTabItem, BgcBaseText.class,
            SWT.BORDER, Messages.GroupEditDialog_property_title_name, null,
            group, GroupPeer.NAME.getName(),
            new NonEmptyStringValidator(
                Messages.GroupEditDialog_msg_name_required));
    }

    private void createMembershipsSection(Composite contents) {
        Button addButton = new Button(contents, SWT.PUSH);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addMembership();
            }
        });
        addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        addButton.setLayoutData(gd);

        membershipInfoTable = new MembershipInfoTable(contents, group);
    }

    private void createUsersSection(Composite contents)
        throws ApplicationException {
        // FIXME something else than a double list selection might be better
        usersWidget = new MultiSelectWidget<UserWrapper>(contents, SWT.NONE,
            Messages.GroupEditDialog_available_users_label,
            Messages.GroupEditDialog_selected_users_label, 200) {
            @Override
            protected String getTextForObject(UserWrapper nodeObject) {
                return nodeObject.getFullName() + " (" + nodeObject.getLogin() //$NON-NLS-1$
                    + ")"; //$NON-NLS-1$
            }
        };

        usersWidget.setSelections(
            UserWrapper.getAllUsers(SessionManager.getAppService()),
            group.getUserCollection(false));
    }

    private Composite createTabItem(TabFolder tb, String title, int columns) {
        TabItem item = new TabItem(tb, SWT.NONE);
        item.setText(title);
        Composite contents = new Composite(tb, SWT.NONE);
        contents.setLayout(new GridLayout(columns, false));
        item.setControl(contents);
        return contents;
    }

    protected void addMembership() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                MembershipWrapper ms = new MembershipWrapper(SessionManager
                    .getAppService());
                ms.setPrincipal(group);

                MembershipEditDialog dlg = new MembershipEditDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(), ms);
                int res = dlg.open();
                if (res == Status.OK) {
                    membershipInfoTable.reloadCollection(
                        group.getMembershipCollection(true), null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            group.addToUserCollection(usersWidget.getAddedToSelection());
            Group groupModel = group.getWrappedObject();

            // for now it's faster to use the name as the description
            groupModel.setDescription(groupModel.getName());

            Group unproxied =
                (Group) new BiobankProxyHelperImpl()
                    .convertToObject(groupModel);

            SessionManager.getAppService().doAction(
                new GroupSaveAction(new GroupSaveInput(unproxied)));
            close();
        } catch (Throwable t) {
            if (t.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error,
                    Messages.GroupEditDialog_msg_error_name_used);
            } else {
                String message = t.getMessage();
                if (t.getCause() instanceof ConstraintViolationException) {
                    message =
                        ((ConstraintViolationException) t.getCause())
                            .getMessage();
                }
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error, message);
                t.printStackTrace();
            }
        }
    }

    @Override
    protected void cancelPressed() {
        try {
            group.reset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.cancelPressed();
    }
}