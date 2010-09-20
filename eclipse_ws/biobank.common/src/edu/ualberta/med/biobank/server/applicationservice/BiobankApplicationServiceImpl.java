package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.ExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.util.ClassCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Implementation of the BiobankApplicationService interface. This class will be
 * only on the server side.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public class BiobankApplicationServiceImpl extends
    WritableApplicationServiceImpl implements BiobankApplicationService {

    private static Logger log = Logger
        .getLogger(BiobankApplicationServiceImpl.class.getName());

    public static final String SITE_CLASS_NAME = "edu.ualberta.med.biobank.model.Site";

    private static final String APPLICATION_CONTEXT_NAME = "biobank2";

    private static final String SITE_ADMIN_PG_ID = "11";

    private static final String CONTAINER_ADMINISTRATION_STRING = "biobank.cbsr.container.administration";

    private static final String CREATE_PRIVILEGE = "CREATE";

    private static final String DELETE_PRIVILEGE = "DELETE";

    private static final String UPDATE_PRIVILEGE = "UPDATE";

    private static final String READ_PRIVILEGE = "READ";

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    @Override
    public boolean canReadObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, READ_PRIVILEGE);
    }

    @Override
    public boolean canReadObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, READ_PRIVILEGE);
    }

    @Override
    public boolean canCreateObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, CREATE_PRIVILEGE);
    }

    @Override
    public boolean canDeleteObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, DELETE_PRIVILEGE);
    }

    @Override
    public boolean canDeleteObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, DELETE_PRIVILEGE);
    }

    @Override
    public boolean canUpdateObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, UPDATE_PRIVILEGE);
    }

    @Override
    public boolean canUpdateObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, UPDATE_PRIVILEGE);
    }

    @Override
    public boolean hasPrivilege(Class<?> clazz, Integer id, String privilegeName)
        throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            AuthorizationManager am = SecurityServiceProvider
                .getAuthorizationManager(APPLICATION_CONTEXT_NAME);
            if (id == null) {
                return am.checkPermission(userLogin, clazz.getName(),
                    privilegeName);
            }
            return am.checkPermission(userLogin, clazz.getName(), "id",
                id.toString(), privilegeName);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean isContainerAdministrator() throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            AuthorizationManager am = SecurityServiceProvider
                .getAuthorizationManager(APPLICATION_CONTEXT_NAME);
            return am.checkPermission(userLogin,
                CONTAINER_ADMINISTRATION_STRING, "CREATE");
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean isWebsiteAdministrator() throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
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
                        edu.ualberta.med.biobank.common.security.Group.GROUP_NAME_WEBSITE_ADMINISTRATOR)) {
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

    /**
     * How can we manage security using sql ??
     */
    @Override
    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException {
        return privateQuery(sqlCriteria, targetClassName);
    }

    @Override
    public SDKQueryResult executeQuery(SDKQuery query)
        throws ApplicationException {
        SDKQueryResult res = super.executeQuery(query);
        if (query instanceof ExampleQuery) {
            Object queryObject = ((ExampleQuery) query).getExample();
            if (queryObject != null && queryObject instanceof Site) {
                if (query instanceof InsertExampleQuery) {
                    newSiteSecurity((Site) res.getObjectResult());
                } else if (query instanceof DeleteExampleQuery) {
                    deleteSiteSecurity((Site) queryObject);
                }
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private void deleteSiteSecurity(Site site) throws ApplicationException {
        Object id = null;
        String nameShort = null;
        try {
            id = site.getId();
            nameShort = site.getNameShort();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            Set<ProtectionElement> siteAdminPEs = upm
                .getProtectionElements(SITE_ADMIN_PG_ID);
            for (ProtectionElement pe : siteAdminPEs) {
                if (pe.getValue().equals(id.toString())) {
                    upm.removeProtectionElement(pe.getProtectionElementId()
                        .toString());
                    return;
                }
            }
        } catch (Exception e) {
            throw new ApplicationException("Error deleting site " + id + ":"
                + nameShort + "security: " + e.getMessage());
        }

    }

    private void newSiteSecurity(Site site) throws ApplicationException {
        Object id = null;
        String nameShort = null;
        try {
            id = site.getId();
            nameShort = site.getNameShort();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            // Create protection element for the site
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(upm.getApplication(APPLICATION_CONTEXT_NAME));
            pe.setProtectionElementName(SITE_CLASS_NAME + "/" + nameShort);
            pe.setProtectionElementDescription(nameShort);
            pe.setObjectId(SITE_CLASS_NAME);
            pe.setAttribute("id");
            pe.setValue(id.toString());
            upm.createProtectionElement(pe);
            // Add the new protection element to the protection group
            // "Site Admin PG"
            upm.addProtectionElements(SITE_ADMIN_PG_ID, new String[] { pe
                .getProtectionElementId().toString() });
        } catch (Exception e) {
            log.error("error adding new site security", e);
            throw new ApplicationException("Error adding new site " + id + ":"
                + nameShort + "security:" + e.getMessage());
        }
    }

    @Override
    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
        throws Exception {
        Log log = new Log();
        log.setAction(action);
        log.setSite(site);
        log.setPatientNumber(patientNumber);
        log.setInventoryId(inventoryID);
        log.setLocationLabel(locationLabel);
        log.setDetails(details);
        log.setType(type);
        logActivity(log);
    }

    @Override
    public void logActivity(Log log) throws Exception {
        Logger logger = Logger.getLogger("Biobank.Activity");
        logger.log(Level.toLevel("INFO"),
            MessageGenerator.generateStringMessage(log));
    }

    @Override
    public List<Object> launchReport(BiobankReport report)
        throws ApplicationException {
        return ReportFactory.createReport(report).generate(this);
    }

    @Override
    public void modifyPassword(String oldPassword, String newPassword)
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);

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

    @Override
    public List<edu.ualberta.med.biobank.common.security.Group> getSecurityGroups()
        throws ApplicationException {
        if (isWebsiteAdministrator()) {
            try {
                List<edu.ualberta.med.biobank.common.security.Group> list = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
                for (Object object : getCSMGroups()) {
                    Group group = (Group) object;
                    list.add(new edu.ualberta.med.biobank.common.security.Group(
                        group.getGroupId(), group.getGroupName()));
                }
                return list;
            } catch (Exception ex) {
                log.error("Error retrieving security groups", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators can retrieve security groups");
        }
    }

    /**
     * Retrieve only groups for application biobank2
     */
    private List<?> getCSMGroups() throws Exception {
        UserProvisioningManager upm = SecurityServiceProvider
            .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
        return upm.getObjects(new GroupSearchCriteria(new Group()));
    }

    @Override
    public List<edu.ualberta.med.biobank.common.security.User> getSecurityUsers()
        throws ApplicationException {
        if (isWebsiteAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);

                List<edu.ualberta.med.biobank.common.security.User> list = new ArrayList<edu.ualberta.med.biobank.common.security.User>();
                for (Object object : getCSMGroups()) {
                    Group serverGroup = (Group) object;
                    for (Object userObj : upm.getUsers(serverGroup.getGroupId()
                        .toString())) {
                        User serverUser = (User) userObj;
                        edu.ualberta.med.biobank.common.security.User userDTO = new edu.ualberta.med.biobank.common.security.User();
                        userDTO.setId(serverUser.getUserId());
                        userDTO.setLogin(serverUser.getLoginName());
                        userDTO.setFirstName(serverUser.getFirstName());
                        userDTO.setLastName(serverUser.getLastName());
                        userDTO.setNeedToChangePassword(serverUser
                            .getStartDate() != null);

                        // no need to send password back in the clear (note:
                        // passwords seem to be encrypted, not hashed)
                        userDTO.setPassword(null);

                        userDTO.setEmail(serverUser.getEmailId());
                        List<edu.ualberta.med.biobank.common.security.Group> groups = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
                        for (Object o : upm.getGroups(serverUser.getUserId()
                            .toString())) {
                            Group userGroup = (Group) o;
                            groups
                                .add(new edu.ualberta.med.biobank.common.security.Group(
                                    userGroup.getGroupId(), userGroup
                                        .getGroupName()));
                        }
                        userDTO.setGroups(groups);
                        list.add(userDTO);
                    }
                }
                return list;
            } catch (ApplicationException ae) {
                log.error("Error retrieving security users", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error retrieving security users", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators can retrieve security users");
        }
    }

    @Override
    public void persistUser(edu.ualberta.med.biobank.common.security.User user)
        throws ApplicationException {
        if (isWebsiteAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
                if (user.getLogin() == null) {
                    throw new ApplicationException("Login should be set");
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

                if (user.isNeedToChangePassword()) {
                    serverUser.setStartDate(new Date());
                }

                Set<Group> groups = new HashSet<Group>();
                for (edu.ualberta.med.biobank.common.security.Group groupDto : user
                    .getGroups()) {
                    Group g = upm.getGroupById(groupDto.getId().toString());
                    if (g == null) {
                        throw new ApplicationException("Invalid group "
                            + groupDto + " user groups.");
                    }
                    groups.add(g);
                }
                if (groups.size() == 0) {
                    throw new Exception("No group has been set for this user.");
                }
                serverUser.setGroups(groups);
                if (serverUser.getUserId() == null) {
                    upm.createUser(serverUser);
                } else {
                    upm.modifyUser(serverUser);
                }
            } catch (ApplicationException ae) {
                log.error("Error persisting security user", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error persisting security user", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators can add/modify users");
        }
    }

    @Override
    public void deleteUser(String login) throws ApplicationException {
        if (isWebsiteAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
                String currentLogin = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
                if (currentLogin.equals(login)) {
                    throw new ApplicationException("User cannot delete himself");
                }
                User serverUser = upm.getUser(login);
                if (serverUser == null) {
                    throw new ApplicationException("Security user " + login
                        + " not found.");
                }
                upm.removeUser(serverUser.getUserId().toString());
            } catch (ApplicationException ae) {
                log.error("Error deleting security user", ae);
                throw ae;
            } catch (Exception ex) {
                log.error("Error deleting security user", ex);
                throw new ApplicationException(ex);
            }
        } else {
            throw new ApplicationException(
                "Only Website Administrators can delete users");
        }
    }

    @Override
    public boolean needPasswordModification() throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            User user = upm.getUser(userLogin);
            if (user == null) {
                throw new ApplicationException("Error retrieving security user");
            }
            return user.getStartDate() != null;
        } catch (ApplicationException ae) {
            log.error("Error checking password status", ae);
            throw ae;
        } catch (Exception ex) {
            log.error("Error checking password status", ex);
            throw new ApplicationException(ex);
        }
    }
}
