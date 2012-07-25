package edu.ualberta.med.biobank.action.security;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.Group;

public class GroupDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString INADEQUATE_PERMISSIONS_ERRMSG =
        bundle.tr("You do not have adequate permissions to delete this group")
            .format();

    private final GroupDeleteInput input;

    public GroupDeleteAction(GroupDeleteInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagerPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Group group = context.load(Group.class, input.getGroupId());

        if (!group.isFullyManageable(context.getUser()))
            throw new LocalizedException(INADEQUATE_PERMISSIONS_ERRMSG);

        context.getSession().delete(group);
        return new EmptyResult();
    }
}
