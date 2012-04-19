package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.User;
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
            throw new ActionException(INADEQUATE_PERMISSIONS_ERRMSG);
        }

        try {
            BiobankCSMSecurityUtil.deleteUser(user);
        } catch (Exception e) {
            throw new ActionException(CSM_USER_DELETE_FAILURE_ERRMSG);
        }

        context.getSession().delete(user);
        return new EmptyResult();
    }
}
