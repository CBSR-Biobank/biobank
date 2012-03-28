package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
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
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.security.GroupGetOutput;
import edu.ualberta.med.biobank.common.action.security.GroupSaveAction;
import edu.ualberta.med.biobank.common.action.security.GroupSaveInput;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContext;
import edu.ualberta.med.biobank.common.peer.GroupPeer;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends AbstractSecurityEditDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private final Group group;
    private final MembershipContext membershipContext;
    private final ManagerContext context;

    private MembershipInfoTable membershipInfoTable;
    private MultiSelectWidget<User> usersWidget;

    public GroupEditDialog(Shell parent, GroupGetOutput output,
        ManagerContext context) {
        super(parent);

        this.group = output.getGroup();
        this.membershipContext = output.getContext();
        this.context = context;

        if (group.isNew()) {
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

        membershipInfoTable =
            new MembershipInfoTable(contents, group, membershipContext, context);
    }

    private void createUsersSection(Composite contents)
        throws ApplicationException {
        usersWidget = new MultiSelectWidget<User>(contents, SWT.NONE,
            Messages.GroupEditDialog_available_users_label,
            Messages.GroupEditDialog_selected_users_label, 200) {
            @Override
            protected String getTextForObject(User node) {
                return node.getFullName() + " (" + node.getLogin() + ")";
            }
        };

        usersWidget.setSelections(context.getUsers(),
            new ArrayList<User>(group.getUsers()));
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
                Membership m = new Membership();
                m.setPrincipal(group);

                Shell shell = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell();

                MembershipEditWizard wiz = new MembershipEditWizard(m, context);
                WizardDialog dlg = new SecurityWizardDialog(shell, wiz);

                int res = dlg.open();
                if (res == Status.OK) {
                    m.setPrincipal(group);
                    group.getMemberships().add(m);

                    membershipInfoTable.setCollection(group.getMemberships());
                    membershipInfoTable.setSelection(m);
                } else {
                    m.setPrincipal(null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            group.getUsers().addAll(usersWidget.getAddedToSelection());

            // FIXME: for now it's faster to use the name as the description
            group.setDescription(group.getName());

            IdResult result = SessionManager.getAppService().doAction(
                new GroupSaveAction(new GroupSaveInput(group)));
            group.setId(result.getId());

            close();
        } catch (Throwable t) {
            TmpUtil.displayException(t);
        }
    }
}