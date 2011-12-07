package edu.ualberta.med.biobank.common.action.security;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.User;

public class GroupDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer groupId;

    public GroupDeleteAction(Integer id) {
        this.groupId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new UserManagementPermission().isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        BbGroup group = ActionUtil.sessionGet(session, BbGroup.class, groupId);
        session.delete(group);
        return new EmptyResult();
    }

}
