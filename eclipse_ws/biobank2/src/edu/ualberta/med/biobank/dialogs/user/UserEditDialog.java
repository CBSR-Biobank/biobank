package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.handlers.LogoutHandler;
import edu.ualberta.med.biobank.validators.EmptyStringValidator;
import edu.ualberta.med.biobank.validators.MatchingTextValidator;
import edu.ualberta.med.biobank.validators.OrValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserEditDialog extends BgcBaseDialog {
    public static final int CLOSE_PARENT_RETURN_CODE = 3;
    private static final int PASSWORD_LENGTH_MIN = 5;

    private static final String MSG_PASSWORD_REQUIRED = NLS.bind(
        Messages.UserEditDialog_passwords_length_msg, PASSWORD_LENGTH_MIN);

    private User originalUser, modifiedUser = new User();
    private Map<Long, Group> allGroupsMap = new HashMap<Long, Group>();
    private MultiSelectWidget groupsWidget;
    private boolean isNewUser;

    public UserEditDialog(Shell parent, User originalUser,
        List<Group> groupList, boolean isNewUser) {
        super(parent);

        Assert.isNotNull(originalUser);

        this.originalUser = originalUser;

        for (Group group : groupList) {
            allGroupsMap.put(group.getId(), group);
        }

        this.modifiedUser = new User();
        this.modifiedUser.copy(originalUser);
        this.isNewUser = isNewUser;

        if (isNewUser) {
            modifiedUser.setNeedToChangePassword(true);
        }
    }

    @Override
    protected String getDialogShellTitle() {
        if (isNewUser) {
            return Messages.UserEditDialog_title_add;
        } else {
            return Messages.UserEditDialog_title_edit;
        }
    }

    @Override
    protected String getTitleAreaMessage() {
        if (isNewUser) {
            return Messages.UserEditDialog_description_add;
        } else {
            return Messages.UserEditDialog_description_edit;
        }
    }

    @Override
    protected String getTitleAreaTitle() {
        return getDialogShellTitle();
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Control c = createBoundWidgetWithLabel(contents, BgcBaseText.class,
            SWT.BORDER, Messages.UserEditDialog_login_label, null,
            modifiedUser, "login", //$NON-NLS-1$
            new NonEmptyStringValidator(
                Messages.UserEditDialog_loginName_validation_msg));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.UserEditDialog_Email_label, null, modifiedUser,
            "email", null); //$NON-NLS-1$

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.UserEditDialog_firstName_label, null, modifiedUser,
            "firstName", null); //$NON-NLS-1$

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.UserEditDialog_lastname_label, null, modifiedUser,
            "lastName", null); //$NON-NLS-1$

        createPasswordWidgets(contents);

        createGroupsWidget(parent);
    }

    @Override
    protected void okPressed() {
        // try saving or updating the user inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            originalUser.copy(SessionManager.getAppService().persistUser(
                modifiedUser));

            if (SessionManager.getUser().getId().equals(originalUser.getId())) {
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
        } catch (ApplicationException e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.UserEditDialog_save_error_title, MessageFormat
                        .format(Messages.UserEditDialog_login_unique_error_msg,
                            modifiedUser.getLogin()));
            } else {
                BgcPlugin.openAsyncError(
                    Messages.UserEditDialog_save_error_title, e);
            }
        }
    }

    private void createGroupsWidget(Composite parent) {
        final LinkedHashMap<Integer, String> groupMap = new LinkedHashMap<Integer, String>();
        List<String> groupNames = new ArrayList<String>();

        for (Entry<Long, Group> entry : allGroupsMap.entrySet()) {
            Integer groupId = entry.getKey().intValue();
            String groupName = entry.getValue().getName();

            groupNames.add(groupName);
            groupMap.put(groupId, groupName);
        }

        List<Integer> userInGroupIds = new ArrayList<Integer>();
        for (Group group : originalUser.getGroups()) {
            userInGroupIds.add(group.getId().intValue());
        }

        final boolean warnOfRightsDemotion = SessionManager.getUser().equals(
            originalUser)
            && originalUser.isSuperAdministrator();
        groupsWidget = new MultiSelectWidget(parent, SWT.NONE,
            Messages.UserEditDialog_groups_available_label,
            Messages.UserEditDialog_groups_assigned_label, 75);
        groupsWidget.setSelections(groupMap, userInGroupIds);
        groupsWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    List<Group> newGroups = new ArrayList<Group>();

                    if (warnOfRightsDemotion) {
                        for (Integer id : groupsWidget.getRemovedToSelection()) {
                            Group group = allGroupsMap.get(id.longValue());
                            if (group != null
                                && group.isSuperAdministratorGroup()) {
                                if (!BgcPlugin
                                    .openConfirm(
                                        Messages.UserEditDialog_deleteRight_confirm_title,
                                        NLS.bind(
                                            Messages.UserEditDialog_deleteRight_confirm_msg,
                                            Group.GROUP_SUPER_ADMIN))) {
                                    newGroups.add(group);

                                    List<Integer> oldSelection = new ArrayList<Integer>();
                                    oldSelection.addAll(groupsWidget
                                        .getSelected());
                                    oldSelection.addAll(groupsWidget
                                        .getRemovedToSelection());
                                    groupsWidget.setSelections(groupMap,
                                        oldSelection);
                                }
                            }
                        }
                    }

                    for (Integer id : groupsWidget.getSelected()) {
                        newGroups.add(allGroupsMap.get(id.longValue()));
                    }

                    modifiedUser.setGroups(newGroups);
                }
            });
    }

    private void createPasswordWidgets(Composite parent) {
        AbstractValidator passwordValidator;
        passwordValidator = new StringLengthValidator(PASSWORD_LENGTH_MIN,
            MSG_PASSWORD_REQUIRED);

        if (!isNewUser) {
            // existing users can have their password field left blank
            passwordValidator = new OrValidator(Arrays.asList(
                new EmptyStringValidator(""), passwordValidator), //$NON-NLS-1$
                MSG_PASSWORD_REQUIRED);
        }

        BgcBaseText password = (BgcBaseText) createBoundWidgetWithLabel(parent,
            BgcBaseText.class, SWT.BORDER | SWT.PASSWORD,
            (isNewUser ? Messages.UserEditDialog_password_new_label
                : Messages.UserEditDialog_password_label), new String[0],
            modifiedUser, "password", passwordValidator); //$NON-NLS-1$

        BgcBaseText passwordRetyped = (BgcBaseText) createBoundWidgetWithLabel(
            parent, BgcBaseText.class, SWT.BORDER | SWT.PASSWORD,
            (isNewUser ? Messages.UserEditDialog_password_retype_new_label
                : Messages.UserEditDialog_password_retype_label),
            new String[0], modifiedUser, "password", new MatchingTextValidator( //$NON-NLS-1$
                Messages.UserEditDialog_passwords_match_error_msg, password));

        MatchingTextValidator.addListener(password, passwordRetyped);
    }
}