package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.LocalizedString;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

public class BiobankCSMSecurityUtil {
    @SuppressWarnings("nls")
    public static final String APPLICATION_CONTEXT_NAME = "biobank";

    @SuppressWarnings("nls")
    public static final String GLOBAL_FEATURE_START_NAME = "Global Feature: ";

    @SuppressWarnings("nls")
    public static final String CENTER_FEATURE_START_NAME = "Center Feature: ";

    @SuppressWarnings("nls")
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
                throw new LocalizedException(LocalizedString.tr("Only the" +
                    " user itself can modify its password through this method"));
            if (!oldPassword.equals(authentication.getCredentials())) {
                throw new LocalizedException(LocalizedString.tr("Cannot" +
                    " modify password: verification password is incorrect."));
            }
            if (oldPassword.equals(newPassword)) {
                throw new LocalizedException(LocalizedString.tr("New password" +
                    " needs to be different from the old one."));
            }
            user.setPassword(newPassword);
            user.setStartDate(null);
            upm.modifyUser(user);
        } catch (CSException e) {
            throw new LocalizedException(
                LocalizedString.tr("Unexpected problem while modifying user"),
                e);
        }
    }

    public static void unlockUser(String userNameToUnlock) {
        // FIXME do we want to check here that the user launching this action
        // can actually do it ?
        LockoutManager.getInstance().unLockUser(userNameToUnlock);
    }

    @SuppressWarnings("nls")
    public static Long persistUser(edu.ualberta.med.biobank.model.User user,
        String password) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            if (user.getLogin() == null)
                throw new LocalizedException(
                    LocalizedString.tr("Login should be set"));
            boolean newUser = (user.getId() == null);
            User serverUser;
            if (newUser) {
                serverUser = upm.getUser(user.getLogin());
                if (serverUser == null) {
                    serverUser = new User();
                } else
                    throw new LocalizedException(
                        LocalizedString.tr("Login {0} already exists.",
                            user.getLogin()));
            } else {
                if (user.getCsmUserId() == null)
                    throw new LocalizedException(
                        LocalizedString.tr(
                            "User with id {0} is missing a csmUserId",
                            user.getId()));
                serverUser = null;
                try {
                    serverUser = upm
                        .getUserById(user.getCsmUserId().toString());
                } catch (CSObjectNotFoundException confe) {
                    throw new LocalizedException(
                        LocalizedString.tr(
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
        } catch (CSException e) {
            throw new LocalizedException(
                LocalizedString.tr("Unexpected problem while modifying user"),
                e);
        }
    }

    @SuppressWarnings("nls")
    public static void deleteUser(edu.ualberta.med.biobank.model.User user) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            String currentLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            if (currentLogin.equals(user.getLogin())) {
                throw new LocalizedException(
                    LocalizedString.tr("User cannot delete himself"));
            }
            if (user.getCsmUserId() == null)
                throw new LocalizedException(
                    LocalizedString.tr(
                        "User with id {0} is missing a csmUserId", user.getId()));
            User serverUser = upm.getUserById(user.getCsmUserId().toString());
            if (serverUser == null) {
                throw new LocalizedException(
                    LocalizedString.tr(
                        "CSM Security user with id {0} not found.",
                        user.getCsmUserId()));
            }
            upm.removeUser(serverUser.getUserId().toString());
        } catch (CSException e) {
            throw new LocalizedException(
                LocalizedString.tr("Unexpected problem while modifying user"),
                e);
        }
    }

    @SuppressWarnings("nls")
    public static String getUserPassword(String login) {
        try {
            UserProvisioningManager upm =
                SecurityServiceProvider
                    .getUserProvisioningManager(BiobankCSMSecurityUtil.APPLICATION_CONTEXT_NAME);
            User serverUser = upm.getUser(login);
            if (serverUser == null) {
                throw new LocalizedException(
                    LocalizedString.tr("User {0} not found", login));
            }
            // FIXME how safe is this?
            return serverUser.getPassword();
        } catch (CSException e) {
            throw new LocalizedException(
                LocalizedString.tr("Unexpected problem while modifying user"),
                e);
        }
    }

    @SuppressWarnings("nls")
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
                throw new LocalizedException(
                    LocalizedString.tr(
                        "CSM Security user with id {0} not found.",
                        csmUserId), onfe);
            }
        } catch (CSException e) {
            throw new LocalizedException(
                LocalizedString.tr("Unexpected problem while modifying user"),
                e);
        }
    }
}
