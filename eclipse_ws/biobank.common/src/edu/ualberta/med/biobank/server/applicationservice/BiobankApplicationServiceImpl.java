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
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    private Map<String, Map<String, Boolean>> cachedPrivilegesMap = new HashMap<String, Map<String, Boolean>>();

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    @Override
    public boolean hasPrivilege(Class<?> clazz, Integer id, String privilegeName)
        throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            AuthorizationManager am = SecurityServiceProvider
                .getAuthorizationManager(APPLICATION_CONTEXT_NAME);
            String objectId = clazz.getName();
            if (id == null) {
                return checkPermission(am, userLogin, objectId, privilegeName);
            }
            return am.checkPermission(userLogin, objectId, "id", id.toString(),
                privilegeName);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private Boolean checkPermission(AuthorizationManager am, String userLogin,
        String objectId, String privilegeName) throws CSException {
        Map<String, Boolean> objectsMap = cachedPrivilegesMap
            .get(privilegeName);
        if (objectsMap == null) {
            objectsMap = new HashMap<String, Boolean>();
            cachedPrivilegesMap.put(privilegeName, objectsMap);
        }
        Boolean res = objectsMap.get(objectId);
        if (res == null) {
            res = am.checkPermission(userLogin, objectId, privilegeName);
        }
        objectsMap.put(objectId, res);
        return res;
    }

    private boolean isWebsiteAdministrator() throws ApplicationException {
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
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
                List<edu.ualberta.med.biobank.common.security.Group> list = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
                for (Object object : upm.getObjects(new GroupSearchCriteria(
                    new Group()))) {
                    list.add(createGroup(upm, (Group) object));
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

    private edu.ualberta.med.biobank.common.security.Group createGroup(
        UserProvisioningManager upm, Group group)
        throws CSObjectNotFoundException {
        edu.ualberta.med.biobank.common.security.Group biobankGroup = new edu.ualberta.med.biobank.common.security.Group(
            group.getGroupId(), group.getGroupName());

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
            biobankGroup.addProtectionElementPrivilege(
                pe.getProtectionElementName(), privileges, pe.getValue());
        }
        return biobankGroup;
    }

    @Override
    public List<edu.ualberta.med.biobank.common.security.User> getSecurityUsers()
        throws ApplicationException {
        if (isWebsiteAdministrator()) {
            try {
                UserProvisioningManager upm = SecurityServiceProvider
                    .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);

                List<edu.ualberta.med.biobank.common.security.User> list = new ArrayList<edu.ualberta.med.biobank.common.security.User>();
                Map<Long, User> users = new HashMap<Long, User>();

                for (Object g : upm.getObjects(new GroupSearchCriteria(
                    new Group()))) {
                    Group group = (Group) g;
                    for (Object u : upm.getUsers(group.getGroupId().toString())) {
                        User user = (User) u;
                        if (!users.containsKey(user.getUserId())) {
                            list.add(createUser(upm, user));
                            users.put(user.getUserId(), user);
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
    public edu.ualberta.med.biobank.common.security.User getCurrentUser()
        throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);

            Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
            String userLogin = authentication.getName();
            User serverUser = upm.getUser(userLogin);
            if (serverUser == null)
                throw new ApplicationException("Problem with user retrieval");
            return createUser(upm, serverUser);
        } catch (ApplicationException ae) {
            log.error("Error getting current user", ae);
            throw ae;
        } catch (Exception ex) {
            log.error("Error getting current user", ex);
            throw new ApplicationException(ex);
        }
    }

    private edu.ualberta.med.biobank.common.security.User createUser(
        UserProvisioningManager upm, User serverUser)
        throws CSObjectNotFoundException {
        edu.ualberta.med.biobank.common.security.User userDTO = new edu.ualberta.med.biobank.common.security.User();
        userDTO.setId(serverUser.getUserId());
        userDTO.setLogin(serverUser.getLoginName());
        userDTO.setFirstName(serverUser.getFirstName());
        userDTO.setLastName(serverUser.getLastName());
        userDTO.setEmail(serverUser.getEmailId());

        if (serverUser.getStartDate() != null) {
            userDTO.setNeedToChangePassword(true);
        }

        List<edu.ualberta.med.biobank.common.security.Group> groups = new ArrayList<edu.ualberta.med.biobank.common.security.Group>();
        for (Object o : upm.getGroups(serverUser.getUserId().toString())) {
            groups.add(createGroup(upm, (Group) o));
        }
        userDTO.setGroups(groups);
        return userDTO;
    }

}
