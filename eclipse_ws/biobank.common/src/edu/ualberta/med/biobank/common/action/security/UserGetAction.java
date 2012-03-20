package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.SimpleResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.User;

public class UserGetAction implements Action<SimpleResult<User>> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final UserGetInput input;

    public UserGetAction(UserGetInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public SimpleResult<User> run(ActionContext context) throws ActionException {
        User user = context.load(User.class, input.getUserId());

        User dto = new User();
        
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setNeedPwdChange(user.getNeedPwdChange());
        dto.setRecvBulkEmails(user.getRecvBulkEmails());

        return SimpleResult.of(dto);
    }
}
