package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.wrappers.GroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.validators.EmailValidator;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.handlers.LogoutHandler;
import edu.ualberta.med.biobank.validators.EmptyStringValidator;
import edu.ualberta.med.biobank.validators.MatchingTextValidator;
import edu.ualberta.med.biobank.validators.OrValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserEditDialog extends BgcBaseDialog {
    public static final int CLOSE_PARENT_RETURN_CODE = 3;
    private static final int PASSWORD_LENGTH_MIN = 5;

    private static final String MSG_PASSWORD_REQUIRED = NLS.bind(
        Messages.UserEditDialog_passwords_length_msg, PASSWORD_LENGTH_MIN);

    private UserWrapper originalUser = new UserWrapper(null);
    private MembershipInfoTable membershipInfoTable;
    private MultiSelectWidget<GroupWrapper> groupsWidget;

    public UserEditDialog(Shell parent, UserWrapper originalUser) {
        super(parent);

        Assert.isNotNull(originalUser);

        this.originalUser = originalUser;

        if (originalUser.isNew()) {
            originalUser.setNeedPwdChange(true);
        }
    }

    @Override
    protected String getDialogShellTitle() {
        if (originalUser.isNew()) {
            return Messages.UserEditDialog_title_add;
        }
        return Messages.UserEditDialog_title_edit;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (originalUser.isNew()) {
            return Messages.UserEditDialog_description_add;
        }
        return Messages.UserEditDialog_description_edit;
    }

    @Override
    protected String getTitleAreaTitle() {
        return getDialogShellTitle();
    }

    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabFolder tb = new TabFolder(contents, SWT.TOP);
        tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createUserFields(createTabItem(tb,
            Messages.UserEditDialog_general_title, 2));

        createMembershipsSection(createTabItem(tb,
            Messages.UserEditDialog_roles_permissions_title, 1));

        createGroupsSection(createTabItem(tb,
            Messages.UserEditDialog_group_title, 1));
    }

    private Composite createTabItem(TabFolder tb, String title, int columns) {
        TabItem item = new TabItem(tb, SWT.NONE);
        item.setText(title);
        Composite contents = new Composite(tb, SWT.NONE);
        contents.setLayout(new GridLayout(columns, false));
        item.setControl(contents);
        return contents;
    }

    private void createUserFields(Composite contents) {
        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.UserEditDialog_login_label, null, originalUser,
            UserPeer.LOGIN.getName(), new NonEmptyStringValidator(
                Messages.UserEditDialog_loginName_validation_msg));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.UserEditDialog_firstName_label, null, originalUser,
            UserPeer.FULL_NAME.getName(), new NonEmptyStringValidator(
                Messages.UserEditDialog_fullName_validator_msg));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.UserEditDialog_Email_label, null, originalUser,
            UserPeer.EMAIL.getName(), new EmailValidator(
                Messages.UserEditDialog_email_validator_msg));

        createBoundWidgetWithLabel(contents, Button.class, SWT.CHECK,
            Messages.UserEditDialog_bulkemail_label, null, originalUser,
            UserPeer.RECV_BULK_EMAILS.getName(), null);

        if (!originalUser.equals(SessionManager.getUser()))
            createPasswordWidgets(contents);
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

        membershipInfoTable = new MembershipInfoTable(contents, originalUser);
    }

    private void createGroupsSection(Composite contents)
        throws ApplicationException {
        groupsWidget = new MultiSelectWidget<GroupWrapper>(contents,
            SWT.NONE, Messages.UserEditDialog_groups_available,
            Messages.UserEditDialog_groups_selected, 200) {
            @Override
            protected String getTextForObject(GroupWrapper nodeObject) {
                return nodeObject.getName();
            }
        };

        groupsWidget.setSelections(
            GroupWrapper.getAllGroups(SessionManager.getAppService()),
            originalUser.getGroupCollection(false));
    }

    protected void addMembership() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                MembershipWrapper ms = new MembershipWrapper(SessionManager
                    .getAppService());
                ms.setPrincipal(originalUser);

                MembershipEditDialog dlg = new MembershipEditDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(), ms);
                int res = dlg.open();
                if (res == Status.OK) {
                    membershipInfoTable.reloadCollection(
                        originalUser.getMembershipCollection(true), null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        // try saving or updating the user inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            // FIXME tried to add
            // "tasks.persistAdded(this, UserPeer.GROUP_COLLECTION);" in
            // "addPersistTasks" method of userwrapper but didn't work.
            // The following is working fine, but do we want to generalize it?

            WrapperTransaction tx = new WrapperTransaction(
                SessionManager.getAppService());
            tx.persist(originalUser);

            for (GroupWrapper g : groupsWidget.getAddedToSelection()) {
                g.addToUserCollection(Arrays.asList(originalUser));
                tx.persist(g);
            }

            for (GroupWrapper g : groupsWidget.getRemovedFromSelection()) {
                g.removeFromUserCollection(Arrays.asList(originalUser));
                tx.persist(g);
            }
            // add into group after persisting because user needs to be created
            // first

            tx.commit();

            if (SessionManager.getUser().equals(originalUser)) {
                // if the User is making changes to himself, logout
                BgcPlugin.openInformation(
                    Messages.UserEditDialog_user_persist_title,
                    Messages.UserEditDialog_user_persist_msg);

                LogoutHandler lh = new LogoutHandler();
                try {
                    lh.execute(null);
                } catch (ExecutionException e) {
                }
                setReturnCode(CLOSE_PARENT_RETURN_CODE);
            } else {
                setReturnCode(OK);
            }
            close();
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.UserEditDialog_save_error_title, MessageFormat
                        .format(Messages.UserEditDialog_login_unique_error_msg,
                            originalUser.getLogin()));
            } else {
                BgcPlugin.openAsyncError(
                    Messages.UserEditDialog_save_error_title, e);
            }
        }
    }

    private void createPasswordWidgets(Composite parent) {
        AbstractValidator passwordValidator;
        passwordValidator = new StringLengthValidator(PASSWORD_LENGTH_MIN,
            MSG_PASSWORD_REQUIRED);

        if (!originalUser.isNew()) {
            // existing users can have their password field left blank
            passwordValidator = new OrValidator(Arrays.asList(
                new EmptyStringValidator(""), passwordValidator), //$NON-NLS-1$
                MSG_PASSWORD_REQUIRED);
        }

        BgcBaseText password = (BgcBaseText) createBoundWidgetWithLabel(parent,
            BgcBaseText.class, SWT.BORDER | SWT.PASSWORD,
            (originalUser.isNew() ? Messages.UserEditDialog_password_new_label
                : Messages.UserEditDialog_password_label), new String[0],
            originalUser, "password", passwordValidator); //$NON-NLS-1$

        password.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                originalUser.setNeedPwdChange(true);
            }
        });

        BgcBaseText passwordRetyped =
            (BgcBaseText) createBoundWidgetWithLabel(
                parent,
                BgcBaseText.class,
                SWT.BORDER | SWT.PASSWORD,
                (originalUser.isNew() ? Messages.UserEditDialog_password_retype_new_label
                    : Messages.UserEditDialog_password_retype_label),
                new String[0],
                originalUser,
                "password", new MatchingTextValidator( //$NON-NLS-1$
                    Messages.UserEditDialog_passwords_match_error_msg, password));

        MatchingTextValidator.addListener(password, passwordRetyped);
    }

    @Override
    protected void cancelPressed() {
        try {
            originalUser.reset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.cancelPressed();
    }
}