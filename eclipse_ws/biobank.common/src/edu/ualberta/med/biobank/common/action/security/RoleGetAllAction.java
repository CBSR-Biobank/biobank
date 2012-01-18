package edu.ualberta.med.biobank.common.action.security;

import java.util.List;

import org.hibernate.FetchMode;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Role;

public class RoleGetAllAction implements Action<ListResult<Role>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    @Override
    public ListResult<Role> run(ActionContext context) throws ActionException {
        @SuppressWarnings("unchecked")
        List<Role> roles = (List<Role>) context.getSession()
            .createCriteria(Role.class)
            .setFetchMode("permissionCollection", FetchMode.JOIN)
            .list();
        return new ListResult<Role>(roles);
    }

}
