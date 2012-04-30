package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

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
            throw new ActionException("insufficient power");
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

        try {
            BiobankCSMSecurityUtil.deleteUser(user);
        } catch (ApplicationException e) {
            // don't throw an exception if the user object cannot even be found.
            if (!(e.getCause() instanceof CSObjectNotFoundException)) {
                throw new ActionException(
                    "unable to delete underlying CSM user", e);
            }
        }

        return new EmptyResult();
    }
}
