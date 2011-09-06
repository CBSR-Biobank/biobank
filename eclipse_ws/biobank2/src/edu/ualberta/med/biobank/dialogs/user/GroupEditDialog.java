package edu.ualberta.med.biobank.dialogs.user;

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
import edu.ualberta.med.biobank.common.peer.BbGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private BbGroupWrapper originalGroup;
    private MembershipInfoTable membershipInfoTable;
    private MultiSelectWidget<UserWrapper> usersWidget;

    public GroupEditDialog(Shell parent, BbGroupWrapper originalGroup) {
        super(parent);
        Assert.isNotNull(originalGroup);
        this.originalGroup = originalGroup;
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

        createGeneralFields(createTabItem(tb, "General", 2));

        createMembershipsSection(createTabItem(tb,
            Messages.UserEditDialog_roles_permissions_title, 1));

        createUsersSection(createTabItem(tb, "Users", 1));

    }

    private void createGeneralFields(Composite createTabItem) {
        createBoundWidgetWithLabel(createTabItem, BgcBaseText.class,
            SWT.BORDER, Messages.GroupEditDialog_property_title_name, null,
            originalGroup, BbGroupPeer.NAME.getName(),
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

        membershipInfoTable = new MembershipInfoTable(contents, originalGroup);
    }

    private void createUsersSection(Composite contents)
        throws ApplicationException {
        // FIXME something else than a double list selection might be better
        usersWidget = new MultiSelectWidget<UserWrapper>(contents, SWT.NONE,
            "Available users", "Selected users", 200) {
            @Override
            protected String getTextForObject(UserWrapper nodeObject) {
                return nodeObject.getFullName() + " (" + nodeObject.getLogin() //$NON-NLS-1$
                    + ")"; //$NON-NLS-1$
            }
        };

        usersWidget.setSelections(
            UserWrapper.getAllUsers(SessionManager.getAppService()),
            originalGroup.getUserCollection(false));
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
                MembershipAddDialog dlg = new MembershipAddDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    originalGroup);
                int res = dlg.open();
                if (res == Status.OK) {
                    membershipInfoTable.getCollection()
                        .add(dlg.getMembership());
                    membershipInfoTable.reloadCollection(
                        originalGroup.getMembershipCollection(true), null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            originalGroup.persist();
            close();
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error,
                    Messages.GroupEditDialog_msg_error_name_used);
            } else {
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error, e);
            }
        }
    }

    @Override
    protected void cancelPressed() {
        try {
            originalGroup.reset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.cancelPressed();
    }
}