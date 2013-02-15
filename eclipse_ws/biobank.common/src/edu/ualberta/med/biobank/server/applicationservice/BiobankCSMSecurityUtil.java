package edu.ualberta.med.biobank.server.applicationservice;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.security.UserSaveAction;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

public class BiobankCSMSecurityUtil {
    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory.getLogger(UserSaveAction.class);

    @SuppressWarnings("nls")
    public static final String APPLICATION_CONTEXT_NAME = "biobank";
    @SuppressWarnings("nls")
    public static final String GLOBAL_FEATURE_START_NAME = "Global Feature: ";
    @SuppressWarnings("nls")
    public static final String CENTER_FEATURE_START_NAME = "Center Feature: ";

    @SuppressWarnings("nls")
    private static final Tr LOGIN_EXISTS =
        bundle.tr("Login {0} already exists.");
    @SuppressWarnings("nls")
    private static final LString LOGIN_REQUIRED =
        bundle.tr("Login should be set").format();
    @SuppressWarnings("nls")
    private static final Tr CSM_USER_IS_NULL =
        bundle.tr("User with id {0} is missing a csmUserId.");
    @SuppressWarnings("nls")
    private static final Tr CSM_USER_ID_NOT_FOUND =
        bundle.tr("CSM Security user with id {0} not found.");

    @SuppressWarnings("nls")
    private static final LString UNEXPECTED_PROBLEM =
        bundle.tr("Unexpected problem while modifying user.").format();
    @SuppressWarnings("nls")
    private static final Tr USER_NOT_FOUND =
        bundle.tr("User {0} not found.");
    @SuppressWarnings("nls")
    private static final LString CANNOT_DELETE_SELF =
        bundle.tr("User cannot delete himself.").format();
    @SuppressWarnings("nls")
    private static final LString SELF_PW_UPDATE_ONLY =
        bundle.tr("Only the user itself can modify its password through" +
            " this method").format();
    @SuppressWarnings("nls")
    private static final LString OLD_PW_INCORRECT =
        bundle.tr("Cannot modify password: verification password is" +
            " incorrect.").format();
    @SuppressWarnings("nls")
    private static final LString OLD_PW_CANNOT_EQUAL_NEW =
        bundle.tr("New password needs to be different from the old one.")
            .format();

    public static void modifyPassword(Long csmUserId, String oldPassword,
        String newPassword) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);

            Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
            String userLogin = authentication.getName();
            User user = upm.getUser(userLogin);
            if (!user.getUserId().equals(csmUserId))
                throw new LocalizedException(SELF_PW_UPDATE_ONLY);
            if (!oldPassword.equals(authentication.getCredentials())) {
                throw new LocalizedException(OLD_PW_INCORRECT);
            }
            if (oldPassword.equals(newPassword)) {
                throw new LocalizedException(OLD_PW_CANNOT_EQUAL_NEW);
            }
            user.setPassword(newPassword);
            user.setStartDate(null);
            upm.modifyUser(user);
        } catch (CSException e) {
            throw new LocalizedException(UNEXPECTED_PROBLEM, e);
        }
    }

    public static void unlockUser(String userNameToUnlock) {
        // FIXME do we want to check here that the user launching this action
        // can actually do it ?
        LockoutManager.getInstance().unLockUser(userNameToUnlock);
    }

    public static Long persistUser(edu.ualberta.med.biobank.model.User user,
        String password) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            if (user.getLogin() == null)
                throw new LocalizedException(LOGIN_REQUIRED);
            boolean newUser = (user.getId() == null);
            User serverUser;
            if (newUser) {
                serverUser = upm.getUser(user.getLogin());
                if (serverUser == null) {
                    serverUser = new User();
                } else
                    throw new LocalizedException(LOGIN_EXISTS.format(
                        user.getLogin()));
            } else {
                if (user.getCsmUserId() == null)
                    throw new LocalizedException(
                        CSM_USER_IS_NULL.format(user.getId()));
                serverUser = null;
                try {
                    serverUser = upm
                        .getUserById(user.getCsmUserId().toString());
                } catch (CSObjectNotFoundException confe) {
                    throw new LocalizedException(CSM_USER_ID_NOT_FOUND.format(
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
        } catch (CSException e) {
            log.error(e.getMessage(), e);
            throw new LocalizedException(UNEXPECTED_PROBLEM, e);
        }
    }

    public static void deleteUser(edu.ualberta.med.biobank.model.User user) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            String currentLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            if (currentLogin.equals(user.getLogin())) {
                throw new LocalizedException(CANNOT_DELETE_SELF);
            }
            if (user.getCsmUserId() == null)
                throw new LocalizedException(CSM_USER_ID_NOT_FOUND.format(user
                    .getId()));
            User serverUser = upm.getUserById(user.getCsmUserId().toString());
            if (serverUser == null) {
                throw new LocalizedException(CSM_USER_ID_NOT_FOUND.format(
                    user.getCsmUserId()));
            }
            upm.removeUser(serverUser.getUserId().toString());
        } catch (CSException e) {
            throw new LocalizedException(UNEXPECTED_PROBLEM, e);
        }
    }

    public static String getUserPassword(String login) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            User serverUser = upm.getUser(login);
            if (serverUser == null) {
                throw new LocalizedException(USER_NOT_FOUND.format(login));
            }
            // FIXME how safe is this?
            return serverUser.getPassword();
        } catch (CSException e) {
            throw new LocalizedException(UNEXPECTED_PROBLEM, e);
        }
    }

    public static boolean isUserLockedOut(Long csmUserId) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            try {
                User serverUser = upm.getUserById(csmUserId.toString());
                return LockoutManager.getInstance().isUserLockedOut(
                    serverUser.getLoginName());
            } catch (CSObjectNotFoundException onfe) {
                throw new LocalizedException(CSM_USER_ID_NOT_FOUND
                    .format(csmUserId), onfe);
            }
        } catch (CSException e) {
            throw new LocalizedException(UNEXPECTED_PROBLEM, e);
        }
    }
}
