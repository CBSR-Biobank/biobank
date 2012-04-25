package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContext;
import edu.ualberta.med.biobank.common.action.security.UserGetOutput;
import edu.ualberta.med.biobank.common.action.security.UserSaveAction;
import edu.ualberta.med.biobank.common.action.security.UserSaveInput;
import edu.ualberta.med.biobank.common.action.security.UserSaveOutput;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.validators.EmailValidator;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.handlers.LogoutHandler;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.validators.EmptyStringValidator;
import edu.ualberta.med.biobank.validators.MatchingTextValidator;
import edu.ualberta.med.biobank.validators.OrValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserEditDialog extends AbstractSecurityEditDialog {
    private static final I18n i18n = I18nFactory.getI18n(UserEditDialog.class);
    public static final int CLOSE_PARENT_RETURN_CODE = 3;
    private static final int PASSWORD_LENGTH_MIN = 5;

    @SuppressWarnings("nls")
    // TR: password minimum length validation message
    private static final String MSG_PASSWORD_REQUIRED = i18n.tr(
        "Passwords must be at least {0} characters long.", PASSWORD_LENGTH_MIN);

    private final UserWrapper userWrapper;
    private final User user;
    private final MembershipContext membershipContext;
    private final ManagerContext managerContext;
    private final boolean isFullyManageable;

    private BgcBaseText password;
    private MembershipInfoTable membershipInfoTable;
    private MultiSelectWidget<Group> groupsWidget;

    public UserEditDialog(Shell parent, UserGetOutput output,
        ManagerContext managerContext) {
        super(parent);
        this.user = output.getUser();
        this.membershipContext = output.getContext();
        this.isFullyManageable = output.isFullyManageable();

        this.managerContext = managerContext;

        BiobankApplicationService appService = SessionManager.getAppService();
        this.userWrapper = new UserWrapper(appService, user);

        if (user.isNew()) {
            user.setNeedPwdChange(true);
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        Button okButton = getButton(IDialogConstants.OK_ID);
        okButton.setText(i18n.trc("Button", "Save"));
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        if (user.isNew()) {
            // TR: add user dialog title
            return i18n.tr("Add User");
        }
        // TR: edit user dialog title
        return i18n.tr("Edit User");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        if (user.isNew()) {
            // TR: add user dialog title area message
            return i18n.tr("Add a new user");
        }
        // TR: edit user dialog title area message
        return i18n.tr("Modify an existing user's information");
    }

    @Override
    protected String getTitleAreaTitle() {
        return getDialogShellTitle();
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabFolder tb = new TabFolder(contents, SWT.TOP);
        tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createUserFields(createTabItem(tb,
            i18n.trc("User Edit Dialog Tab Name", "General"), 2));

        createMembershipsSection(createTabItem(tb,
            i18n.trc("User Edit Dialog Tab Name", "Roles and Permissions"), 1));

        createGroupsSection(createTabItem(tb, Group.NAME.plural().toString(), 1));
    }

    private Composite createTabItem(TabFolder tb, String title, int columns) {
        TabItem item = new TabItem(tb, SWT.NONE);
        item.setText(title);
        Composite contents = new Composite(tb, SWT.NONE);
        contents.setLayout(new GridLayout(columns, false));
        item.setControl(contents);
        return contents;
    }

    @SuppressWarnings("nls")
    private void createUserFields(Composite contents) {
        int readOnly = 0;

        if (!isFullyManageable) {
            readOnly = SWT.READ_ONLY;
        }

        Collection<Control> controls = new ArrayList<Control>();

        controls.add(createBoundWidgetWithLabel(contents, BgcBaseText.class,
            SWT.BORDER | readOnly,
            User.PropertyName.LOGIN.toString(),
            null, userWrapper,
            UserPeer.LOGIN.getName(), new NonEmptyStringValidator(
                // TR: validation error message if login name not entered
                i18n.tr("A valid login name is required."))));

        controls.add(createBoundWidgetWithLabel(contents, BgcBaseText.class,
            SWT.BORDER | readOnly,
            User.PropertyName.FULL_NAME.toString(),
            null, userWrapper,
            UserPeer.FULL_NAME.getName(), new NonEmptyStringValidator(
                // TR: validation error message if full name not entered
                i18n.tr("Full name of this user is required"))));

        controls.add(createBoundWidgetWithLabel(contents, BgcBaseText.class,
            SWT.BORDER | readOnly,
            User.PropertyName.EMAIL_ADDRESS.toString(),
            null, userWrapper,
            UserPeer.EMAIL.getName(), new EmailValidator(
                // TR: validation error message if email not entered
                i18n.tr("A valid email is required"))));

        Control checkbox = createBoundWidgetWithLabel(contents, Button.class,
            SWT.CHECK | readOnly,
            User.PropertyName.RECEIVE_BULK_EMAILS.toString(),
            null, userWrapper,
            UserPeer.RECV_BULK_EMAILS.getName(), null);
        controls.add(checkbox);

        if (!isFullyManageable) {
            for (Control c : controls) {
                c.setBackground(BgcWidgetCreator.READ_ONLY_TEXT_BGR);
            }
            checkbox.setEnabled(false);
        } else {
            if (!userWrapper.equals(SessionManager.getUser()))
                createPasswordWidgets(contents);
        }
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
            new MembershipInfoTable(contents, user, membershipContext,
                managerContext);
    }

    @SuppressWarnings("nls")
    private void createGroupsSection(Composite contents) {
        groupsWidget = new MultiSelectWidget<Group>(contents, SWT.NONE,
            // TR: label for list of available groups to choose from combo box
            i18n.tr("Available groups"),
            // TR: label for list of groups chosen to choose from combo box
            i18n.tr("Selected groups"),
            200) {
            @Override
            protected String getTextForObject(Group node) {
                return node.getName();
            }
        };

        Set<Group> available = membershipContext.getGroups();

        groupsWidget.setSelections(
            new ArrayList<Group>(available),
            new ArrayList<Group>(user.getGroups()));
    }

    protected void addMembership() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                Membership m = new Membership();
                m.setPrincipal(user);

                Shell shell = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell();

                MembershipEditWizard wiz =
                    new MembershipEditWizard(m, managerContext);
                WizardDialog dlg = new SecurityWizardDialog(shell, wiz);

                int res = dlg.open();
                if (res == Status.OK) {
                    m.setPrincipal(user);
                    user.getMemberships().add(m);

                    membershipInfoTable.setCollection(user.getMemberships());
                    membershipInfoTable.setSelection(m);
                } else {
                    m.setPrincipal(null);
                }
            }
        });
    }

    @SuppressWarnings("nls")
    @Override
    protected void okPressed() {
        // try saving or updating the user inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            user.getGroups().removeAll(groupsWidget.getRemovedFromSelection());
            user.getGroups().addAll(groupsWidget.getAddedToSelection());

            String pw = null;
            String pwText = password != null ? password.getText() : null;
            if (pwText != null && !pwText.isEmpty()) {
                pw = pwText;
            }

            UserSaveOutput res = SessionManager.getAppService()
                .doAction(new UserSaveAction(
                    new UserSaveInput(user, membershipContext, pw)));
            user.setId(res.getUserId());
            user.setCsmUserId(res.getCsmUserId());

            if (SessionManager.getUser().equals(user)) {
                // if the User is making changes to himself, logout
                BgcPlugin
                    .openInformation(
                        // TR: information dialog title
                        i18n.tr("User Information Saved"),
                        // TR: information dialog message
                        i18n.tr("Your information has been successfully updated. You will be logged out and have to reconnect."));

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
        } catch (Throwable t) {
            TmpUtil.displayException(t);
        }
    }

    @SuppressWarnings("nls")
    private void createPasswordWidgets(Composite parent) {
        AbstractValidator passwordValidator;
        passwordValidator = new StringLengthValidator(PASSWORD_LENGTH_MIN,
            MSG_PASSWORD_REQUIRED);

        if (!user.isNew()) {
            // existing users can have their password field left blank
            passwordValidator = new OrValidator(Arrays.asList(
                new EmptyStringValidator(""), passwordValidator),
                MSG_PASSWORD_REQUIRED);
        }

        password = (BgcBaseText) createBoundWidgetWithLabel(parent,
            BgcBaseText.class, SWT.BORDER | SWT.PASSWORD,
            (user.isNew()
                // TR: user password text box label
                ? i18n.tr("New Password")
                // TR: user password text box label
                : i18n.tr("Password")),
            new String[0],
            userWrapper, "password", passwordValidator);

        password.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                user.setNeedPwdChange(true);
            }
        });

        BgcBaseText passwordRetyped =
            (BgcBaseText) createBoundWidgetWithLabel(
                parent,
                BgcBaseText.class,
                SWT.BORDER | SWT.PASSWORD,
                (user.isNew()
                    // TR: confirm user password text box label
                    ? i18n.tr("Re-Type New Password")
                    // TR: confirm user password text box label
                    : i18n.tr("Re-Type Password")),
                new String[0],
                userWrapper,
                "password", new MatchingTextValidator(
                    // TR: validaton error message if two entered passwords do
                    // not match
                    i18n.tr("The passwords entered do not match."),
                    password));

        MatchingTextValidator.addListener(password, passwordRetyped);
    }
}