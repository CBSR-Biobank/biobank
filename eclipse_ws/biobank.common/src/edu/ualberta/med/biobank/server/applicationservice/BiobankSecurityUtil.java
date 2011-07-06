package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroupRoleContext;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.dao.ProtectionGroupSearchCriteria;
import gov.nih.nci.security.dao.RoleSearchCriteria;
import gov.nih.nci.security.dao.SearchCriteria;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;

public class BiobankSecurityUtil {

    private static Logger log = Logger.getLogger(BiobankSecurityUtil.class
        .getName());

    public static final String APPLICATION_CONTEXT_NAME = "biobank";

    public static final String GLOBAL_FEATURE_START_NAME = "Global Feature: ";

    public static final String CENTER_FEATURE_START_NAME = "Center Feature: ";

    public static void modifyPassword(String oldPassword, String newPassword)
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);

            Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
            String userLogin = authentication.getName();
            if (!oldPassword.equals(authentication.getCredentials())) {
                throw new ApplicationException(
                    "Cannot modify password: verification password is incorrect.");
            }
            if (oldPassword.equals(newPassword)) {
                throw new ApplicationException(
                    "New password needs to be different from the old one.");
            }
            User user = upm.getUser(userLogin);
            user.setPassword(newPassword);
            user.setStartDate(null);
            upm.modifyUser(user);
        } catch (ApplicationException ae) {
            log.error("Error modifying password", ae);
            throw ae;
        } catch (Exception ex) {
            log.error("Error modifying password", ex);
            throw new ApplicationException(ex);
        }
    }

    public static List<edu.ualberta.med.biobank.common.security.Group> getSecurityGroups(
        edu.ualberta.med.biobank.common.security.User currentUser,
        boolean includeSuperAdmin) throws ApplicationException {
        if (canPerformCenterAdminAction(currentUser)) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                List<edu.ualberta.med.biobank.common.security.Group> list = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
                for (Object object : upm.getObjects(new GroupSearchCriteria(
                    new Group()))) {
                    Group g = (Group) object;
                    if (includeSuperAdmin
                        || !edu.ualberta.med.biobank.common.security.Group.GROUP_SUPER_ADMIN_ID
                            .equals(g.getGroupId())) {
                        edu.ualberta.med.biobank.common.security.Group createGroup = createGroup(
                            upm, (Group) object);
                        // If is only center admin (not super admin), then
                        // return only groups of the same center
                        if (currentUser == null
                            || currentUser.isSuperAdministrator()
                            || createGroup.getWorkingCenterIds().contains(
                                currentUser.getCurrentWorkingCenter().getId()))
                            list.add(createGroup);
                    }
                }
                return list;
            } catch (Exception ex) {
                log.error("Error retrieving security groups", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators or Administrators of current center can retrieve security groups");
        }
    }

    private static edu.ualberta.med.biobank.common.security.Group createGroup(
        UserProvisioningManager upm, Group group)
        throws CSObjectNotFoundException {
        edu.ualberta.med.biobank.common.security.Group biobankGroup = new edu.ualberta.med.biobank.common.security.Group(
            group.getGroupId(), group.getGroupName());
        biobankGroup.setWorkingCenterIds(new ArrayList<Integer>());
        Set<?> pepcList = upm
            .getProtectionElementPrivilegeContextForGroup(group.getGroupId()
                .toString());
        for (Object o : pepcList) {
            ProtectionElementPrivilegeContext pepc = (ProtectionElementPrivilegeContext) o;
            ProtectionElement pe = pepc.getProtectionElement();
            Set<edu.ualberta.med.biobank.common.security.Privilege> privileges = new HashSet<edu.ualberta.med.biobank.common.security.Privilege>();
            for (Object r : pepc.getPrivileges()) {
                Privilege csmPrivilege = (Privilege) r;
                privileges
                    .add(edu.ualberta.med.biobank.common.security.Privilege
                        .valueOf(csmPrivilege.getName()));
            }
            String type = pe.getObjectId();
            String id = null;
            if ("id".equals(pe.getAttribute())) {
                id = pe.getValue();
            }
            biobankGroup.addProtectionElementPrivilege(type, id, privileges);
            if ((type.equals(Site.class.getName())
                || type.equals(Clinic.class.getName()) || type
                    .equals(ResearchGroup.class.getName())) && id != null) {
                if (privileges
                    .contains(edu.ualberta.med.biobank.common.security.Privilege.UPDATE)) {
                    biobankGroup.getWorkingCenterIds().add(new Integer(id));
                }
            }
        }
        Set<?> pgrcList = upm.getProtectionGroupRoleContextForGroup(group
            .getGroupId().toString());
        for (Object o : pgrcList) {
            ProtectionGroupRoleContext pgrc = (ProtectionGroupRoleContext) o;
            ProtectionGroup pg = pgrc.getProtectionGroup();
            Set<edu.ualberta.med.biobank.common.security.Privilege> privileges = new HashSet<edu.ualberta.med.biobank.common.security.Privilege>();
            boolean containsFullAccessObject = false;
            for (Object r : pgrc.getRoles()) {
                Role role = (Role) r;
                if (role
                    .getName()
                    .equals(
                        edu.ualberta.med.biobank.common.security.Group.OBJECT_FULL_ACCESS))
                    containsFullAccessObject = true;
                for (Object p : upm.getPrivileges(role.getId().toString())) {
                    Privilege csmPrivilege = (Privilege) p;
                    privileges
                        .add(edu.ualberta.med.biobank.common.security.Privilege
                            .valueOf(csmPrivilege.getName()));
                }
            }
            biobankGroup.addProtectionGroupPrivilege(pg.getProtectionGroupId(),
                pg.getProtectionGroupName(),
                pg.getProtectionGroupDescription(), privileges);
            if (edu.ualberta.med.biobank.common.security.Group.PG_CENTER_ADMINISTRATOR_ID
                .equals(pg.getProtectionGroupId()) && containsFullAccessObject)
                biobankGroup.setIsWorkingCentersAdministrator(true);
        }
        return biobankGroup;
    }

    public static List<edu.ualberta.med.biobank.common.security.User> getSecurityUsers(
        edu.ualberta.med.biobank.common.security.User currentUser)
        throws ApplicationException {
        if (canPerformCenterAdminAction(currentUser)) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);

                List<edu.ualberta.med.biobank.common.security.User> list = new ArrayList<edu.ualberta.med.biobank.common.security.User>();
                Map<Long, User> allUsers = new HashMap<Long, User>();
                Map<Long, edu.ualberta.med.biobank.common.security.Group> allGroups = new HashMap<Long, edu.ualberta.med.biobank.common.security.Group>();
                for (edu.ualberta.med.biobank.common.security.Group group : getSecurityGroups(
                    currentUser, true)) {
                    allGroups.put(group.getId(), group);
                }
                for (Long groupId : allGroups.keySet()) {
                    for (Object u : upm.getUsers(groupId.toString())) {
                        User serverUser = (User) u;
                        if (!allUsers.containsKey(serverUser.getUserId())) {
                            edu.ualberta.med.biobank.common.security.User newUser = createUser(
                                upm, serverUser, allGroups);
                            // If is only center admin (not super admin), then
                            // return only users of the same center
                            if (currentUser == null
                                || currentUser.isSuperAdministrator()
                                || newUser.getWorkingCenterIds().contains(
                                    currentUser.getCurrentWorkingCenter()
                                        .getId())) {
                                list.add(newUser);
                            }
                            allUsers.put(serverUser.getUserId(), serverUser);
                        }
                    }
                }
                return list;
            } catch (Exception ex) {
                log.error("Error retrieving security users", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators or Administrators of current center can retrieve all security users");
        }
    }

    public static edu.ualberta.med.biobank.common.security.User persistUser(
        edu.ualberta.med.biobank.common.security.User currentUser,
        edu.ualberta.med.biobank.common.security.User newUser)
        throws ApplicationException {
        if (canPerformCenterAdminAction(currentUser)) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                if (newUser.getLogin() == null) {
                    throw new ApplicationException("Login should be set");
                }

                User serverUser = null;
                if (newUser.getId() != null) {
                    serverUser = upm.getUserById(newUser.getId().toString());
                }
                if (serverUser == null) {
                    serverUser = new User();
                }

                serverUser.setLoginName(newUser.getLogin());
                serverUser.setFirstName(newUser.getFirstName());
                serverUser.setLastName(newUser.getLastName());
                serverUser.setEmailId(newUser.getEmail());

                String password = newUser.getPassword();
                if (password != null && !password.isEmpty()) {
                    serverUser.setPassword(password);
                }

                if (newUser.passwordChangeRequired()) {
                    serverUser.setStartDate(new Date());
                }

                Set<Group> groups = new HashSet<Group>();
                for (edu.ualberta.med.biobank.common.security.Group groupDto : newUser
                    .getGroups()) {
                    Group g = upm.getGroupById(groupDto.getId().toString());
                    if (g == null) {
                        throw new ApplicationException("Invalid group "
                            + groupDto + " user groups.");
                    }
                    groups.add(g);
                }
                if (groups.size() == 0) {
                    throw new ApplicationException(
                        "No group has been set for this user.");
                }
                serverUser.setGroups(groups);
                if (serverUser.getUserId() == null) {
                    upm.createUser(serverUser);
                } else {
                    upm.modifyUser(serverUser);
                }
                serverUser = upm.getUser(serverUser.getLoginName());
                newUser.setId(serverUser.getUserId());
                return newUser;
            } catch (ApplicationException ae) {
                log.error("Error persisting security user", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error persisting security user", ex);
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators or Administrators of current center can add/modify users");
        }
    }

    public static void deleteUser(
        edu.ualberta.med.biobank.common.security.User currentUser,
        String loginToDelete) throws ApplicationException {
        if (canPerformCenterAdminAction(currentUser)) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                String currentLogin = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
                if (currentLogin.equals(loginToDelete)) {
                    throw new ApplicationException("User cannot delete himself");
                }
                User serverUser = upm.getUser(loginToDelete);
                if (serverUser == null) {
                    throw new ApplicationException("Security user "
                        + loginToDelete + " not found.");
                }
                upm.removeUser(serverUser.getUserId().toString());
            } catch (ApplicationException ae) {
                log.error("Error deleting security user", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error deleting security user", ex);
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators or Administrators of current center can delete users");
        }
    }

    public static edu.ualberta.med.biobank.common.security.User getCurrentUser()
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);

            Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
            String userLogin = authentication.getName();
            User serverUser = upm.getUser(userLogin);
            if (serverUser == null)
                throw new ApplicationException("Problem with user retrieval");
            return createUser(upm, serverUser, null);
        } catch (ApplicationException ae) {
            log.error("Error getting current user", ae);
            throw ae;
        } catch (Exception ex) {
            log.error("Error getting current user", ex);
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }

    private static edu.ualberta.med.biobank.common.security.User createUser(
        UserProvisioningManager upm, User serverUser,
        Map<Long, edu.ualberta.med.biobank.common.security.Group> allGroups)
        throws CSObjectNotFoundException {
        edu.ualberta.med.biobank.common.security.User userDTO = new edu.ualberta.med.biobank.common.security.User();
        userDTO.setId(serverUser.getUserId());
        userDTO.setLogin(serverUser.getLoginName());
        userDTO.setFirstName(serverUser.getFirstName());
        userDTO.setLastName(serverUser.getLastName());
        userDTO.setEmail(serverUser.getEmailId());
        userDTO.setLockedOut(LockoutManager.getInstance().isUserLockedOut(
            serverUser.getLoginName()));

        if (serverUser.getStartDate() != null) {
            userDTO.setNeedToChangePassword(true);
        }

        List<edu.ualberta.med.biobank.common.security.Group> userGroups = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
        for (Object o : upm.getGroups(serverUser.getUserId().toString())) {
            if (allGroups == null) {
                userGroups.add(createGroup(upm, (Group) o));
            } else {
                edu.ualberta.med.biobank.common.security.Group userGroup = allGroups
                    .get(((Group) o).getGroupId());
                if (userGroup != null)
                    userGroups.add(userGroup);
            }
        }
        userDTO.setGroups(userGroups);
        return userDTO;
    }

    public static void unlockUser(
        edu.ualberta.med.biobank.common.security.User currentUser,
        String userNameToUnlock) throws ApplicationException {
        if (canPerformCenterAdminAction(currentUser)) {
            LockoutManager.getInstance().unLockUser(userNameToUnlock);
        }
    }

    public static edu.ualberta.med.biobank.common.security.Group persistGroup(
        edu.ualberta.med.biobank.common.security.Group group)
        throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                if (!group.canBeEdited()) {
                    throw new ApplicationException(
                        "This group cannot be modified.");
                }
                if (group.getName() == null) {
                    throw new ApplicationException("Name should be set.");
                }

                Group serverGroup = null;
                edu.ualberta.med.biobank.common.security.Group oldGroup = null;
                if (group.getId() != null) {
                    serverGroup = upm.getGroupById(group.getId().toString());
                    oldGroup = createGroup(upm, serverGroup);
                }
                if (serverGroup == null) {
                    serverGroup = new Group();
                    oldGroup = new edu.ualberta.med.biobank.common.security.Group();
                }
                serverGroup.setGroupName(group.getName());
                if (serverGroup.getGroupId() == null) {
                    upm.createGroup(serverGroup);
                } else {
                    upm.modifyGroup(serverGroup);
                }
                // Default is Read Only for all Objects (protection group with
                // id=1
                addPGRoleAssociationToGroup(upm, serverGroup, 1L,
                    edu.ualberta.med.biobank.common.security.Group.READ_ONLY);

                if (group.getIsWorkingCentersAdministrator()) {
                    addPGRoleAssociationToGroup(
                        upm,
                        serverGroup,
                        edu.ualberta.med.biobank.common.security.Group.PG_CENTER_ADMINISTRATOR_ID,
                        edu.ualberta.med.biobank.common.security.Group.OBJECT_FULL_ACCESS);
                } else {
                    upm.removeGroupFromProtectionGroup(
                        edu.ualberta.med.biobank.common.security.Group.PG_CENTER_ADMINISTRATOR_ID
                            .toString(), serverGroup.getGroupId().toString());
                }

                List<Integer> oldCentersList = oldGroup.getWorkingCenterIds();
                for (Integer centerId : group.getWorkingCenterIds()) {
                    oldCentersList.remove(centerId);
                    setCenterSecurityForGroup(
                        upm,
                        serverGroup,
                        centerId,
                        edu.ualberta.med.biobank.common.security.Group.CENTER_FULL_ACCESS);
                }
                for (Integer centerId : oldCentersList) {
                    removeCenterSecurityForGroup(upm, serverGroup, centerId);
                }
                modifyFeatures(upm, serverGroup,
                    oldGroup.getGlobalFeaturesEnabled(),
                    group.getGlobalFeaturesEnabled());
                modifyFeatures(upm, serverGroup,
                    oldGroup.getCenterFeaturesEnabled(),
                    group.getCenterFeaturesEnabled());
                return createGroup(upm, serverGroup);
            } catch (ApplicationException ae) {
                log.error("Error persisting security group", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error persisting security group", ex);
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators or Administrators of current center can add/modify groups");
        }
    }

    private static void modifyFeatures(UserProvisioningManager upm,
        Group serverGroup, List<Integer> oldFeatures, List<Integer> newFeatures)
        throws ApplicationException, CSTransactionException {
        for (Integer pgId : newFeatures) {
            oldFeatures.remove(pgId);
            addPGRoleAssociationToGroup(
                upm,
                serverGroup,
                pgId.longValue(),
                edu.ualberta.med.biobank.common.security.Group.OBJECT_FULL_ACCESS);
        }
        if (serverGroup.getGroupId() != null)
            for (Integer pgId : oldFeatures) {
                upm.removeGroupFromProtectionGroup(pgId.toString(), serverGroup
                    .getGroupId().toString());
            }

    }

    private static void removeCenterSecurityForGroup(
        UserProvisioningManager upm, Group serverGroup, Integer centerId)
        throws CSObjectNotFoundException, ApplicationException,
        CSTransactionException {
        ProtectionGroup pg = getProtectionGroupForCenter(upm, centerId);
        upm.removeGroupFromProtectionGroup(
            pg.getProtectionGroupId().toString(), serverGroup.getGroupId()
                .toString());
    }

    private static void setCenterSecurityForGroup(UserProvisioningManager upm,
        Group serverGroup, Integer centerId, String roleName)
        throws ApplicationException, CSTransactionException,
        CSObjectNotFoundException {
        ProtectionGroup pg = getProtectionGroupForCenter(upm, centerId);
        addPGRoleAssociationToGroup(upm, serverGroup,
            pg.getProtectionGroupId(), roleName);
    }

    @SuppressWarnings("unchecked")
    private static ProtectionGroup getProtectionGroupForCenter(
        UserProvisioningManager upm, Integer centerId)
        throws ApplicationException, CSObjectNotFoundException {
        ProtectionElement pe = new ProtectionElement();
        // FIXME would be better to get the exact class name to search
        // pe.setObjectId(Site.class.getName());
        pe.setAttribute("id");
        pe.setValue(centerId.toString());
        ProtectionElementSearchCriteria c = new ProtectionElementSearchCriteria(
            pe);
        List<?> peList = upm.getObjects(c);
        if (peList.size() != 1)
            throw new ApplicationException(
                "Problem with center protection element for id=" + centerId);
        pe = (ProtectionElement) peList.get(0);
        Set<ProtectionGroup> pgs = upm.getProtectionGroups(pe
            .getProtectionElementId().toString());
        if (pgs.size() != 1)
            throw new ApplicationException(
                "Problem with protection group for center with id=" + centerId);
        return pgs.iterator().next();
    }

    private static void addPGRoleAssociationToGroup(
        UserProvisioningManager upm, Group serverGroup, Long protectionGroupID,
        String roleName) throws ApplicationException, CSTransactionException {
        Role role = new Role();
        role.setName(roleName);
        List<?> roles = upm.getObjects(new RoleSearchCriteria(role));
        if (roles.size() != 1)
            throw new ApplicationException("Problem getting role " + roleName);
        role = (Role) roles.get(0);
        upm.assignGroupRoleToProtectionGroup(protectionGroupID.toString(),
            serverGroup.getGroupId().toString(), new String[] { role.getId()
                .toString() });
    }

    public static void deleteGroup(
        edu.ualberta.med.biobank.common.security.Group group)
        throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                if (group.canBeDeleted()) {
                    Group serverGroup = upm.getGroupById(group.getId()
                        .toString());
                    if (serverGroup == null) {
                        throw new ApplicationException("Security group "
                            + group.getName() + " not found.");
                    }
                    upm.removeGroup(serverGroup.getGroupId().toString());
                } else {
                    throw new ApplicationException("Deletion of group "
                        + group.getName() + " is not authorized.");
                }
            } catch (ApplicationException ae) {
                log.error("Error deleting security group", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error deleting security group", ex);
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators or Administrators of current center can delete groups");
        }
    }

    public static List<ProtectionGroupPrivilege> getSecurityGlobalFeatures(
        edu.ualberta.med.biobank.common.security.User currentUser)
        throws ApplicationException {
        return getSecurityFeatures(currentUser, GLOBAL_FEATURE_START_NAME);
    }

    public static List<ProtectionGroupPrivilege> getSecurityCenterFeatures(
        edu.ualberta.med.biobank.common.security.User currentUser)
        throws ApplicationException {
        return getSecurityFeatures(currentUser, CENTER_FEATURE_START_NAME);
    }

    private static List<ProtectionGroupPrivilege> getSecurityFeatures(
        edu.ualberta.med.biobank.common.security.User currentUser,
        String protectionGroupNameStart) throws ApplicationException {
        if (canPerformCenterAdminAction(currentUser)) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                ProtectionGroup pg = new ProtectionGroup();
                pg.setProtectionGroupName(protectionGroupNameStart + "%");
                List<ProtectionGroupPrivilege> features = new ArrayList<ProtectionGroupPrivilege>();
                for (Object object : upm
                    .getObjects(new ProtectionGroupSearchCriteria(pg))) {
                    ProtectionGroup pgFeature = (ProtectionGroup) object;
                    features.add(new ProtectionGroupPrivilege(pgFeature
                        .getProtectionGroupId(), pgFeature
                        .getProtectionGroupName(), pgFeature
                        .getProtectionGroupDescription()));
                }
                return features;
            } catch (Exception ex) {
                log.error("Error retrieving security features", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only super administrators can retrieve security features");
        }
    }

    private static boolean canPerformCenterAdminAction(
        edu.ualberta.med.biobank.common.security.User currentUser)
        throws ApplicationException {
        if (currentUser == null)
            return isSuperAdministrator();
        return currentUser.isAdministratorForCurrentCenter()
            || currentUser.isSuperAdministrator();
    }

    private static boolean isSuperAdministrator() throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
            User user = upm.getUser(userLogin);
            if (user == null) {
                throw new ApplicationException("Error retrieving security user");
            }
            Set<?> groups = upm.getGroups(user.getUserId().toString());
            for (Object obj : groups) {
                Group group = (Group) obj;
                if (group
                    .getGroupName()
                    .equals(
                        edu.ualberta.med.biobank.common.security.Group.GROUP_SUPER_ADMIN)) {
                    return true;
                }
            }
            return false;
        } catch (ApplicationException ae) {
            log.error("Error checking isWebsiteAdministrator", ae);
            throw ae;
        } catch (Exception ex) {
            log.error("Error checking isWebsiteAdministrator", ex);
            throw new ApplicationException(ex);
        }
    }

    public static void newCenterSecurity(Integer centerId,
        String centerNameShort, Class<?> centerClass) {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            Application currentApplication = upm
                .getApplication(APPLICATION_CONTEXT_NAME);
            // Create protection element for the center
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(currentApplication);
            pe.setProtectionElementName(centerClass.getSimpleName() + "/"
                + centerNameShort);
            pe.setProtectionElementDescription(centerNameShort);
            pe.setObjectId(centerClass.getName());
            pe.setAttribute("id");
            pe.setValue(centerId.toString());
            upm.createProtectionElement(pe);

            // Create a new protection group for this protection element only
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplication(currentApplication);
            pg.setProtectionGroupName(centerClass.getSimpleName() + " "
                + centerNameShort);
            pg.setProtectionGroupDescription("Protection group for center "
                + centerNameShort + " (id=" + centerId + ")");
            pg.setProtectionElements(new HashSet<ProtectionElement>(Arrays
                .asList(pe)));
            upm.createProtectionGroup(pg);
        } catch (Exception e) {
            log.error("error adding new center security", e);
            throw new RuntimeException("Error adding new center " + centerId
                + ":" + centerNameShort + " security:" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void deleteCenterSecurity(Integer centerId, String nameShort,
        Class<?> centerClass) {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            ProtectionElement searchPE = new ProtectionElement();
            searchPE.setObjectId(centerClass.getName());
            searchPE.setAttribute("id");
            searchPE.setValue(centerId.toString());
            SearchCriteria sc = new ProtectionElementSearchCriteria(searchPE);
            List<ProtectionElement> peToDelete = upm.getObjects(sc);
            if (peToDelete == null || peToDelete.size() == 0) {
                return;
            }
            List<String> pgIdsToDelete = new ArrayList<String>();
            for (ProtectionElement pe : peToDelete) {
                Set<ProtectionGroup> pgs = upm.getProtectionGroups(pe
                    .getProtectionElementId().toString());
                for (ProtectionGroup pg : pgs) {
                    // remove the protection group only if it contains only
                    // this protection element
                    String pgId = pg.getProtectionGroupId().toString();
                    if (upm.getProtectionElements(pgId).size() == 1) {
                        pgIdsToDelete.add(pgId);
                    }
                }
                upm.removeProtectionElement(pe.getProtectionElementId()
                    .toString());
            }
            for (String pgId : pgIdsToDelete) {
                upm.removeProtectionGroup(pgId);
            }
        } catch (Exception e) {
            log.error("error deleting center security", e);
            throw new RuntimeException("Error deleting center " + centerId
                + ":" + nameShort + " security: " + e.getMessage());
        }

    }
}
