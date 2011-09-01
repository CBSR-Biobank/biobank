package edu.ualberta.med.biobank.server.applicationservice;

import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.text.MessageFormat;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;

public class BiobankCSMSecurityUtil {

    private static Logger log = Logger.getLogger(BiobankCSMSecurityUtil.class
        .getName());

    public static final String APPLICATION_CONTEXT_NAME = "biobank"; //$NON-NLS-1$

    public static final String GLOBAL_FEATURE_START_NAME = "Global Feature: "; //$NON-NLS-1$

    public static final String CENTER_FEATURE_START_NAME = "Center Feature: "; //$NON-NLS-1$

    public static void modifyPassword(Long csmUserId, String oldPassword,
        String newPassword) throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);

            Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
            String userLogin = authentication.getName();
            User user = upm.getUser(userLogin);
            if (!user.getUserId().equals(csmUserId))
                throw new ApplicationException(
                    "Only the user itself can modify its password through this method");
            if (!oldPassword.equals(authentication.getCredentials())) {
                throw new ApplicationException(
                    Messages
                        .getString("BiobankSecurityUtil.pwd.verif.error.msg")); //$NON-NLS-1$
            }
            if (oldPassword.equals(newPassword)) {
                throw new ApplicationException(
                    Messages.getString("BiobankSecurityUtil.pwd.new.error.msg")); //$NON-NLS-1$
            }
            user.setPassword(newPassword);
            user.setStartDate(null);
            upm.modifyUser(user);
        } catch (ApplicationException ae) {
            log.error("Error modifying password", ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error("Error modifying password", ex); //$NON-NLS-1$
            throw new ApplicationException(ex);
        }
    }

    public static void unlockUser(String userNameToUnlock) {
        // FIXME do we want to check here that the user launching this action
        // can actually do it ?
        LockoutManager.getInstance().unLockUser(userNameToUnlock);
    }

    public static Long persistUser(edu.ualberta.med.biobank.model.User user,
        String password) throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
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
                    throw new ApplicationException(MessageFormat.format(
                        "Login {0} already exists.", user.getLogin()));
            } else {
                if (user.getCsmUserId() == null)
                    throw new ApplicationException(
                        MessageFormat.format(
                            "User with id {0} is missing a csmUserId",
                            user.getId()));
                serverUser = null;
                try {
                    serverUser = upm
                        .getUserById(user.getCsmUserId().toString());
                } catch (CSObjectNotFoundException confe) {
                    throw new ApplicationException(MessageFormat.format(
                        "CSM Security user with id {0} not found.",
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

    public static void deleteUser(edu.ualberta.med.biobank.model.User user)
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            String currentLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            if (currentLogin.equals(user.getLogin())) {
                throw new ApplicationException(
                    Messages
                        .getString("BiobankSecurityUtil.delete.self.error.msg")); //$NON-NLS-1$
            }
            if (user.getCsmUserId() == null)
                throw new ApplicationException(MessageFormat.format(
                    "User with id {0} is missing a csmUserId", user.getId()));
            User serverUser = upm.getUserById(user.getCsmUserId().toString());
            if (serverUser == null) {
                throw new ApplicationException(MessageFormat.format(
                    "CSM security user with id {0} not found.",
                    user.getCsmUserId()));
            }
            upm.removeUser(serverUser.getUserId().toString());
        } catch (ApplicationException ae) {
            log.error("Error deleting security user", ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error("Error deleting security user", ex); //$NON-NLS-1$
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }

    public static String getUserPassword(String login)
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            User serverUser = upm.getUser(login);
            if (serverUser == null) {
                throw new ApplicationException("Security user " + login
                    + " not found.");
            }
            // FIXME how safe is this?
            return serverUser.getPassword();
        } catch (ApplicationException ae) {
            log.error("Error retrieving csm security user password", ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error("Error retrieving csm security user password", ex); //$NON-NLS-1$
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }

    public static boolean isUserLockedOut(Long csmUserId)
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            try {
                User serverUser = upm.getUserById(csmUserId.toString());
                return LockoutManager.getInstance().isUserLockedOut(
                    serverUser.getLoginName());
            } catch (CSObjectNotFoundException onfe) {
                throw new ApplicationException(MessageFormat.format(
                    "Security user with id {0} not found.", csmUserId), onfe);
            }
        } catch (ApplicationException ae) {
            log.error("Error retrieving csm security user password", ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error("Error retrieving csm security user password", ex); //$NON-NLS-1$
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }
}
