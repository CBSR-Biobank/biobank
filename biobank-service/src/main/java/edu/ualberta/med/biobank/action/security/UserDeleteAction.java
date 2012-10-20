package edu.ualberta.med.biobank.action.security;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.security.Group;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;

public class UserDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();
    private static final Permission PERMISSION = new UserManagerPermission();

    @SuppressWarnings("nls")
    public static final LString INADEQUATE_PERMISSIONS_ERRMSG =
        bundle.tr("You do not have adequate permissions to delete this user.")
            .format();
    @SuppressWarnings("nls")
    public static final LString CSM_USER_DELETE_FAILURE_ERRMSG =
        bundle.tr("Unable to delete associated CSM user.").format();

    private final UserDeleteInput input;

    public UserDeleteAction(UserDeleteInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, input.getUserId());

        if (!user.isFullyManageable(context.getUser())) {
            throw new LocalizedException(INADEQUATE_PERMISSIONS_ERRMSG);
        }

        // the group side is the managing side, so we must remove the user from
        // the group and then save it
        for (Group group : user.getGroups()) {
            group.getUsers().remove(user);
        }

        // delete and flush the user first as this is easier to revert than
        // deleting the CSM user.
        context.getSession().delete(user);
        context.getSession().flush();

        BiobankCSMSecurityUtil.deleteUser(user);

        return new EmptyResult();
    }
}
