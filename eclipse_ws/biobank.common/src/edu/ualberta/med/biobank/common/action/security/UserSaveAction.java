package edu.ualberta.med.biobank.common.action.security;

import java.text.MessageFormat;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;
import edu.ualberta.med.biobank.server.applicationservice.Messages;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final Integer userId;
    private final String login;
    private final boolean recvBulkEmails;
    private final boolean needPwdChange;
    private final String fullName;
    private final String email;
    private final ActivityStatus activityStatus;

    public UserSaveAction(User user) {
        this.userId = user.getId();
        this.login = user.getLogin();
        this.recvBulkEmails = user.getRecvBulkEmails();
        this.needPwdChange = user.getNeedPwdChange();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.activityStatus = user.getActivityStatus();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        User user = context.load(User.class, userId, new User());

        // user.setCsmUserId(csmUserId);

        Long csmUserId = user.getCsmUserId();
        if (csmUserId == null) {
            Long csmUserId2 = BiobankCSMSecurityUtil.persistUser(getModel(),
                password);
        }

        user.setLogin(login);
        user.setRecvBulkEmails(recvBulkEmails);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setNeedPwdChange(needPwdChange);

        user.setActivityStatus(activityStatus);

        // TODO: set groups

        context.getSession().saveOrUpdate(user);

        return new IdResult(user.getId());
    }

    private static class CsmUtil {
        public static final String APPLICATION_CONTEXT_NAME = "biobank";

        public static Long persistUser(
            edu.ualberta.med.biobank.model.User user,
            String password) throws ApplicationException {
            try {
                UserProvisioningManager upm =
                    SecurityServiceProvider
                        .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
                if (user.getLogin() == null)
                    throw new ApplicationException(
                        Messages
                            .getString("BiobankSecurityUtil.login.set.error.msg")); //$NON-NLS-1$
                boolean newUser = (user.getId() == null);
                User serverUser;
                if (newUser) {
                    serverUser = upm.getUser(user.getLogin());
                    if (serverUser == null) {
                        serverUser = new User();
                    } else
                        throw new ApplicationException(
                            MessageFormat.format(
                                Messages
                                    .getString("BiobankCSMSecurityUtil.login.exists.error"), user.getLogin())); //$NON-NLS-1$
                } else {
                    if (user.getCsmUserId() == null)
                        throw new ApplicationException(
                            MessageFormat.format(
                                Messages
                                    .getString("BiobankCSMSecurityUtil.user.csm.missing.error"), //$NON-NLS-1$
                                user.getId()));
                    serverUser = null;
                    try {
                        serverUser = upm
                            .getUserById(user.getCsmUserId().toString());
                    } catch (CSObjectNotFoundException confe) {
                        throw new ApplicationException(
                            MessageFormat.format(
                                Messages
                                    .getString("BiobankCSMSecurityUtil.csm.user.not.found.error"), //$NON-NLS-1$
                                user.getCsmUserId()), confe);
                    }
                }
                serverUser.setLoginName(user.getLogin());
                if (password != null && !password.isEmpty()) {
                    serverUser.setPassword(password);
                }
                if (newUser) {
                    upm.createUser(serverUser);
                    serverUser = upm.getUser(user.getLogin());
                } else
                    upm.modifyUser(serverUser);

                // add association of protection group/csm role to the user
                // protection group with id '1' contains all database objects.
                // csm role with id '8' (Object Full Access) contains privileges
                // read, delete, create, update
                if (newUser)
                    upm.assignUserRoleToProtectionGroup(serverUser.getUserId()
                        .toString(), new String[] { String.valueOf(8) }, String
                        .valueOf(1));

                return serverUser.getUserId();
            } catch (ApplicationException ae) {
                log.error("Error persisting csm security user", ae); //$NON-NLS-1$
                throw ae;
            } catch (Exception ex) {
                log.error("Error persisting csm security user", ex); //$NON-NLS-1$
                throw new ApplicationException(ex.getMessage(), ex);
            }
        }
    }
}
