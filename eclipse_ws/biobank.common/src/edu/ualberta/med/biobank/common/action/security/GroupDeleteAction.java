package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Group;

public class GroupDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer groupId;

    public GroupDeleteAction(Group group) {
        if (group == null) {
            throw new IllegalArgumentException();
        }
        this.groupId = group.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Group group = context.load(Group.class, groupId);
        context.getSession().delete(group);
        return new EmptyResult();
    }

}
