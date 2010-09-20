package edu.ualberta.med.biobank.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.EmptyStringValidator;
import edu.ualberta.med.biobank.validators.MatchingTextValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.OrValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserEditDialog extends BiobankDialog {
    private static final String TITLE = "User";
    private static final int PASSWORD_LENGTH_MIN = 5;
    private static final String MSG_LOGIN_REQUIRED = "A valid email address is required.";
    private static final String MSG_PASSWORD_REQUIRED = "Passwords must be at least "
        + PASSWORD_LENGTH_MIN + " characters long.";
    private static final String MSG_PASSWORDS_MUST_MATCH = "The passwords entered do not match.";
    private static final String CONFIRM_DEMOTION_TITLE = "Confirm Demotion";
    private static final String CONFIRM_DEMOTION_MESSAGE = "Are you certain you want to remove yourself as a "
        + Group.GROUP_NAME_WEBSITE_ADMINISTRATOR + "?";
    private static final String USER_PERSIST_ERROR_TITLE = "Unable to Save User";
    private static final String MSG_GROUP_REQUIRED = "Each user must be assigned to at least one group. Please assign a group.";
    private static final String MSG_LOGIN_UNIQUE = "Each user login must be unique: \"{0}\" is already taken. Please try a different login name.";

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

    public User getModifiedUser() {
        return modifiedUser;
    }

    @Override
    protected String getDialogShellTitle() {
        if (isNewUser) {
            return "Add " + TITLE;
        } else {
            return "Edit " + TITLE;
        }
    }

    @Override
    protected String getTitleAreaMessage() {
        if (isNewUser) {
            return "Add a new user";
        } else {
            return "Modify an existing user's information";
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

        Control c = createBoundWidgetWithLabel(contents, BiobankText.class,
            SWT.BORDER, "Login", new String[0],
            PojoObservables.observeValue(modifiedUser, "login"),
            new NonEmptyStringValidator(MSG_LOGIN_REQUIRED));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            "Email", new String[0],
            PojoObservables.observeValue(modifiedUser, "email"), null);

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            "First Name", new String[0],
            PojoObservables.observeValue(modifiedUser, "firstName"), null);

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            "Last Name", new String[0],
            PojoObservables.observeValue(modifiedUser, "lastName"), null);

        createPasswordWidgets(contents);

        createGroupsWidget(parent);
    }

    @Override
    protected void okPressed() {
        // try saving or updating the user inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            SessionManager.getAppService().persistUser(modifiedUser);
            originalUser.copy(modifiedUser);
            super.okPressed();
        } catch (ApplicationException e) {
            String message = null;
            if (groupsWidget.getSelected().size() == 0) {
                message = MSG_GROUP_REQUIRED;
            } else {
                message = MessageFormat.format(MSG_LOGIN_UNIQUE,
                    modifiedUser.getLogin());
            }
            BioBankPlugin.openAsyncError(USER_PERSIST_ERROR_TITLE, message);
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
            originalUser.getLogin())
            && originalUser.isWebsiteAdministrator();
        groupsWidget = new MultiSelectWidget(parent, SWT.NONE,
            "Assigned Groups", "Available Groups", 75);
        groupsWidget.setSelections(groupMap, userInGroupIds);
        groupsWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    List<Group> newGroups = new ArrayList<Group>();

                    if (warnOfRightsDemotion) {
                        for (Integer id : groupsWidget.getRemovedToSelection()) {
                            Group group = allGroupsMap.get(id.longValue());
                            if (group != null && group.isWebsiteAdministrator()) {
                                if (!BioBankPlugin.openConfirm(
                                    CONFIRM_DEMOTION_TITLE,
                                    CONFIRM_DEMOTION_MESSAGE)) {
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
                new EmptyStringValidator(""), passwordValidator),
                MSG_PASSWORD_REQUIRED);
        }

        BiobankText password = (BiobankText) createBoundWidgetWithLabel(parent,
            BiobankText.class, SWT.BORDER | SWT.PASSWORD, (isNewUser ? ""
                : "New ") + "Password", new String[0],
            PojoObservables.observeValue(modifiedUser, "password"),
            passwordValidator);

        BiobankText passwordRetyped = (BiobankText) createBoundWidgetWithLabel(
            parent, BiobankText.class, SWT.BORDER | SWT.PASSWORD, "Re-Type "
                + (isNewUser ? "" : "New ") + "Password", new String[0],
            PojoObservables.observeValue(modifiedUser, "password"),
            new MatchingTextValidator(MSG_PASSWORDS_MUST_MATCH, password));

        MatchingTextValidator.addListener(password, passwordRetyped);
    }
}