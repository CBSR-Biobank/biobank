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

    public static final String APPLICATION_CONTEXT_NAME = "biobank"; //$NON-NLS-1$

    public static final String GLOBAL_FEATURE_START_NAME = "Global Feature: "; //$NON-NLS-1$

    public static final String CENTER_FEATURE_START_NAME = "Center Feature: "; //$NON-NLS-1$

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
                    Messages
                        .getString("BiobankSecurityUtil.pwd.verif.error.msg")); //$NON-NLS-1$
            }
            if (oldPassword.equals(newPassword)) {
                throw new ApplicationException(
                    Messages.getString("BiobankSecurityUtil.pwd.new.error.msg")); //$NON-NLS-1$
            }
            User user = upm.getUser(userLogin);
            user.setPassword(newPassword);
            user.setStartDate(null);
            upm.modifyUser(user);
        } catch (ApplicationException ae) {
            log.error(
                Messages.getString("BiobankSecurityUtil.pwd.modif.error.msg"), ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error(
                Messages.getString("BiobankSecurityUtil.pwd.modif.error.msg"), ex); //$NON-NLS-1$
            throw new ApplicationException(ex);
        }
    }

    public static List<edu.ualberta.med.biobank.common.security.Group> getSecurityGroups(
        boolean includeSuperAdmin) throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                List<edu.ualberta.med.biobank.common.security.Group> list = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
                for (Object object : upm.getObjects(new GroupSearchCriteria(
                    new Group()))) {
                    Group g = (Group) object;
                    if (includeSuperAdmin
                        || !edu.ualberta.med.biobank.common.security.Group.GROUP_SUPER_ADMIN_ID
                            .equals(g.getGroupId()))
                        list.add(createGroup(upm, (Group) object));
                }
                return list;
            } catch (Exception ex) {
                log.error(
                    Messages
                        .getString("BiobankSecurityUtil.retrieve.groups.error.msg"), ex); //$NON-NLS-1$
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.webadmin.groups.error.msg")); //$NON-NLS-1$
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
            if ("id".equals(pe.getAttribute())) { //$NON-NLS-1$
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

    public static List<edu.ualberta.med.biobank.common.security.User> getSecurityUsers()
        throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);

                List<edu.ualberta.med.biobank.common.security.User> list = new ArrayList<edu.ualberta.med.biobank.common.security.User>();
                Map<Long, User> allUsers = new HashMap<Long, User>();
                Map<Long, edu.ualberta.med.biobank.common.security.Group> allGroups = new HashMap<Long, edu.ualberta.med.biobank.common.security.Group>();
                for (edu.ualberta.med.biobank.common.security.Group group : getSecurityGroups(true)) {
                    allGroups.put(group.getId(), group);
                }
                for (Long groupId : allGroups.keySet()) {
                    for (Object u : upm.getUsers(groupId.toString())) {
                        User serverUser = (User) u;
                        if (!allUsers.containsKey(serverUser.getUserId())) {
                            list.add(createUser(upm, serverUser, allGroups));
                            allUsers.put(serverUser.getUserId(), serverUser);
                        }
                    }
                }
                return list;
            } catch (Exception ex) {
                log.error(
                    Messages
                        .getString("BiobankSecurityUtil.retrieve.users.error.msg"), ex); //$NON-NLS-1$
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.webadmin.users.error.msg")); //$NON-NLS-1$
        }
    }

    public static edu.ualberta.med.biobank.common.security.User persistUser(
        edu.ualberta.med.biobank.common.security.User user)
        throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                if (user.getLogin() == null) {
                    throw new ApplicationException(
                        Messages
                            .getString("BiobankSecurityUtil.login.set.error.msg")); //$NON-NLS-1$
                }

                User serverUser = null;
                if (user.getId() != null) {
                    serverUser = upm.getUserById(user.getId().toString());
                }
                if (serverUser == null) {
                    serverUser = new User();
                }

                serverUser.setLoginName(user.getLogin());
                serverUser.setFirstName(user.getFirstName());
                serverUser.setLastName(user.getLastName());
                serverUser.setEmailId(user.getEmail());

                String password = user.getPassword();
                if (password != null && !password.isEmpty()) {
                    serverUser.setPassword(password);
                }

                if (user.passwordChangeRequired()) {
                    serverUser.setStartDate(new Date());
                }

                Set<Group> groups = new HashSet<Group>();
                for (edu.ualberta.med.biobank.common.security.Group groupDto : user
                    .getGroups()) {
                    Group g = upm.getGroupById(groupDto.getId().toString());
                    if (g == null) {
                        throw new ApplicationException("Invalid group " //$NON-NLS-1$
                            + groupDto + " user groups."); //$NON-NLS-1$
                    }
                    groups.add(g);
                }
                if (groups.size() == 0) {
                    throw new ApplicationException(
                        Messages
                            .getString("BiobankSecurityUtil.nogroup_error_msg")); //$NON-NLS-1$
                }
                serverUser.setGroups(groups);
                if (serverUser.getUserId() == null) {
                    upm.createUser(serverUser);
                } else {
                    upm.modifyUser(serverUser);
                }
                serverUser = upm.getUser(serverUser.getLoginName());
                user.setId(serverUser.getUserId());
                return user;
            } catch (ApplicationException ae) {
                log.error("Error persisting security user", ae); //$NON-NLS-1$
                throw ae;
            } catch (Exception ex) {
                log.error("Error persisting security user", ex); //$NON-NLS-1$
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.wenadmin.modif.user.error.msg")); //$NON-NLS-1$
        }
    }

    public static void deleteUser(String login) throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                String currentLogin = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
                if (currentLogin.equals(login)) {
                    throw new ApplicationException(
                        Messages
                            .getString("BiobankSecurityUtil.delete.self.error.msg")); //$NON-NLS-1$
                }
                User serverUser = upm.getUser(login);
                if (serverUser == null) {
                    throw new ApplicationException("Security user " + login //$NON-NLS-1$
                        + " not found."); //$NON-NLS-1$
                }
                upm.removeUser(serverUser.getUserId().toString());
            } catch (ApplicationException ae) {
                log.error("Error deleting security user", ae); //$NON-NLS-1$
                throw ae;
            } catch (Exception ex) {
                log.error("Error deleting security user", ex); //$NON-NLS-1$
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.webadmin.delete.user.error.msg")); //$NON-NLS-1$
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
                throw new ApplicationException(
                    Messages
                        .getString("BiobankSecurityUtil.user.retrieve.error.msg")); //$NON-NLS-1$
            return createUser(upm, serverUser, null);
        } catch (ApplicationException ae) {
            log.error(Messages
                .getString("BiobankSecurityUtil.current.user.error.msg"), ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error(Messages
                .getString("BiobankSecurityUtil.current.user.error.msg"), ex); //$NON-NLS-1$
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

    public static void unlockUser(String userName) throws ApplicationException {
        if (isSuperAdministrator()) {
            LockoutManager.getInstance().unLockUser(userName);
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
                        Messages
                            .getString("BiobankSecurityUtil.group.modif.error.msg")); //$NON-NLS-1$
                }
                if (group.getName() == null) {
                    throw new ApplicationException(
                        Messages
                            .getString("BiobankSecurityUtil.name.error.msg")); //$NON-NLS-1$
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
                log.error("Error persisting security group", ae); //$NON-NLS-1$
                throw ae;
            } catch (Exception ex) {
                log.error("Error persisting security group", ex); //$NON-NLS-1$
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.webadmin.group.modif.error.msg")); //$NON-NLS-1$
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
        pe.setAttribute("id"); //$NON-NLS-1$
        pe.setValue(centerId.toString());
        ProtectionElementSearchCriteria c = new ProtectionElementSearchCriteria(
            pe);
        List<?> peList = upm.getObjects(c);
        if (peList.size() != 1)
            throw new ApplicationException(
                "Problem with center protection element for id=" + centerId); //$NON-NLS-1$
        pe = (ProtectionElement) peList.get(0);
        Set<ProtectionGroup> pgs = upm.getProtectionGroups(pe
            .getProtectionElementId().toString());
        if (pgs.size() != 1)
            throw new ApplicationException(
                "Problem with protection group for center with id=" + centerId); //$NON-NLS-1$
        return pgs.iterator().next();
    }

    private static void addPGRoleAssociationToGroup(
        UserProvisioningManager upm, Group serverGroup, Long protectionGroupID,
        String roleName) throws ApplicationException, CSTransactionException {
        Role role = new Role();
        role.setName(roleName);
        List<?> roles = upm.getObjects(new RoleSearchCriteria(role));
        if (roles.size() != 1)
            throw new ApplicationException("Problem getting role " + roleName); //$NON-NLS-1$
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
                        throw new ApplicationException("Security group " //$NON-NLS-1$
                            + group.getName() + " not found."); //$NON-NLS-1$
                    }
                    upm.removeGroup(serverGroup.getGroupId().toString());
                } else {
                    throw new ApplicationException("Deletion of group " //$NON-NLS-1$
                        + group.getName() + " is not authorized."); //$NON-NLS-1$
                }
            } catch (ApplicationException ae) {
                log.error("Error deleting security group", ae); //$NON-NLS-1$
                throw ae;
            } catch (Exception ex) {
                log.error("Error deleting security group", ex); //$NON-NLS-1$
                throw new ApplicationException(ex.getMessage(), ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.webadmin_delete_eror.msg")); //$NON-NLS-1$
        }
    }

    public static List<ProtectionGroupPrivilege> getSecurityGlobalFeatures()
        throws ApplicationException {
        return getSecurityFeatures(GLOBAL_FEATURE_START_NAME);
    }

    public static List<ProtectionGroupPrivilege> getSecurityCenterFeatures()
        throws ApplicationException {
        return getSecurityFeatures(CENTER_FEATURE_START_NAME);
    }

    private static List<ProtectionGroupPrivilege> getSecurityFeatures(
        String protectionGroupNameStart) throws ApplicationException {
        if (isSuperAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
                ProtectionGroup pg = new ProtectionGroup();
                pg.setProtectionGroupName(protectionGroupNameStart + "%"); //$NON-NLS-1$
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
                log.error("Error retrieving security features", ex); //$NON-NLS-1$
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                Messages
                    .getString("BiobankSecurityUtil.webadmin.features.error.msg")); //$NON-NLS-1$
        }
    }

    private static boolean isSuperAdministrator() throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
            User user = upm.getUser(userLogin);
            if (user == null) {
                throw new ApplicationException(
                    Messages
                        .getString("BiobankSecurityUtil.user.retrieve.error.msg")); //$NON-NLS-1$
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
            log.error("Error checking isWebsiteAdministrator", ae); //$NON-NLS-1$
            throw ae;
        } catch (Exception ex) {
            log.error("Error checking isWebsiteAdministrator", ex); //$NON-NLS-1$
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
            pe.setProtectionElementName(centerClass.getSimpleName() + "/" //$NON-NLS-1$
                + centerNameShort);
            pe.setProtectionElementDescription(centerNameShort);
            pe.setObjectId(centerClass.getName());
            pe.setAttribute("id"); //$NON-NLS-1$
            pe.setValue(centerId.toString());
            upm.createProtectionElement(pe);

            // Create a new protection group for this protection element only
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplication(currentApplication);
            pg.setProtectionGroupName(centerClass.getSimpleName() + " " //$NON-NLS-1$
                + centerNameShort);
            pg.setProtectionGroupDescription("Protection group for center " //$NON-NLS-1$
                + centerNameShort + " (id=" + centerId + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            pg.setProtectionElements(new HashSet<ProtectionElement>(Arrays
                .asList(pe)));
            upm.createProtectionGroup(pg);
        } catch (Exception e) {
            log.error("error adding new center security", e); //$NON-NLS-1$
            throw new RuntimeException("Error adding new center " + centerId //$NON-NLS-1$
                + ":" + centerNameShort + " security:" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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
            searchPE.setAttribute("id"); //$NON-NLS-1$
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
            log.error("error deleting center security", e); //$NON-NLS-1$
            throw new RuntimeException("Error deleting center " + centerId //$NON-NLS-1$
                + ":" + nameShort + " security: " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }
}
