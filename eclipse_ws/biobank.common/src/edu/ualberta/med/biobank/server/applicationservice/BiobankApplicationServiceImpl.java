package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ClientVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionNewerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionOlderException;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
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
import gov.nih.nci.security.dao.SearchCriteria;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.ExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.util.ClassCache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

    private static final String ALL_SITES_PG_ID = "11";

    private static final String SERVER_VERSION_PROP_FILE = "version.properties";

    private static final String SERVER_VERSION_PROP_KEY = "server.version";

    private static int[] serverVersionArr = null;

    private static Properties props = null;

    static {
        props = new Properties();
        try {
            props.load(BiobankApplicationServiceImpl.class
                .getResourceAsStream(SERVER_VERSION_PROP_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
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
                        edu.ualberta.med.biobank.common.security.Group.GROUP_WEBSITE_ADMINISTRATOR)) {
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
            ProtectionElement searchPE = new ProtectionElement();
            searchPE.setObjectId(Site.class.getName());
            searchPE.setAttribute("id");
            searchPE.setValue(id.toString());
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
                    // this protection element and is not the main site
                    // admin group
                    String pgId = pg.getProtectionGroupId().toString();
                    if (!pgId.equals(ALL_SITES_PG_ID)
                        && upm.getProtectionElements(pgId).size() == 1) {
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
            e.printStackTrace();
            throw new ApplicationException("Error deleting site " + id + ":"
                + nameShort + " security: " + e.getMessage());
        }

    }

    private void newSiteSecurity(Site site) throws ApplicationException {
        Integer siteId = null;
        String nameShort = null;
        try {
            siteId = site.getId();
            nameShort = site.getNameShort();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            Application currentApplication = upm
                .getApplication(APPLICATION_CONTEXT_NAME);
            // Create protection element for the site
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(currentApplication);
            pe.setProtectionElementName(SITE_CLASS_NAME + "/" + nameShort);
            pe.setProtectionElementDescription(nameShort);
            pe.setObjectId(SITE_CLASS_NAME);
            pe.setAttribute("id");
            pe.setValue(siteId.toString());
            upm.createProtectionElement(pe);

            // Create a new protection group for this protection element only
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplication(currentApplication);
            pg.setProtectionGroupName(nameShort + " site");
            pg.setProtectionGroupDescription("Protection group for site "
                + nameShort + " (id=" + siteId + ")");
            pg.setProtectionElements(new HashSet<ProtectionElement>(Arrays
                .asList(pe)));
            // parent will be the "all sites" protection group
            ProtectionGroup allSitePg = upm
                .getProtectionGroupById(ALL_SITES_PG_ID);
            pg.setParentProtectionGroup(allSitePg);
            upm.createProtectionGroup(pg);
        } catch (Exception e) {
            log.error("error adding new site security", e);
            throw new ApplicationException("Error adding new site " + siteId
                + ":" + nameShort + " security:" + e.getMessage());
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
            String type = pe.getObjectId();
            String id = null;
            if ("id".equals(pe.getAttribute())) {
                id = pe.getValue();
            }
            biobankGroup.addProtectionElementPrivilege(type, id, privileges);
        }

        Set<?> pgrcList = upm.getProtectionGroupRoleContextForGroup(group
            .getGroupId().toString());
        for (Object o : pgrcList) {
            ProtectionGroupRoleContext pgrc = (ProtectionGroupRoleContext) o;
            ProtectionGroup pg = pgrc.getProtectionGroup();
            Set<edu.ualberta.med.biobank.common.security.Privilege> privileges = new HashSet<edu.ualberta.med.biobank.common.security.Privilege>();
            for (Object r : pgrc.getRoles()) {
                Role role = (Role) r;
                for (Object p : upm.getPrivileges(role.getId().toString())) {
                    Privilege csmPrivilege = (Privilege) p;
                    privileges
                        .add(edu.ualberta.med.biobank.common.security.Privilege
                            .valueOf(csmPrivilege.getName()));
                }
            }
            biobankGroup.addProtectionGroupPrivilege(
                pg.getProtectionGroupName(), privileges);
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

                if (user.passwordChangeRequired()) {
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
        userDTO.setLockedOut(LockoutManager.getInstance().isUserLockedOut(
            serverUser.getLoginName()));

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

    @Override
    public void unlockUser(String userName) throws ApplicationException {
        if (isWebsiteAdministrator()) {
            LockoutManager.getInstance().unLockUser(userName);
        }
    }

    private static int[] versionStrToIntArray(String version) {
        String[] versionSplit = version.split("\\.");

        if ((versionSplit.length != 3) && (versionSplit.length != 4)) {
            // split length is invalid
            return null;
        }

        int[] result = new int[versionSplit.length];

        for (int i = 0; i < versionSplit.length; i++) {
            if (i < 3) {
                try {
                    result[i] = Integer.parseInt(versionSplit[i]);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if ((i == 3) && !versionSplit[3].equals("pre")) {
                return null;
            }
        }
        return result;
    }

    private static void serverVersionStrToIntArray(String version) {
        if (serverVersionArr != null)
            return;
        serverVersionArr = versionStrToIntArray(version);
    }

    @Override
    public void checkVersion(String clientVersion) throws ApplicationException {
        if (props == null) {
            log.error("server does not have a version");
            throw new ServerVersionInvalidException(
                "The server version could not be determined.");
        }

        String serverVersion = props.getProperty(SERVER_VERSION_PROP_KEY);

        if (serverVersion == null) {
            log.error("server does not have a version");
            throw new ServerVersionInvalidException(
                "The server version could not be determined.");
        }

        serverVersionStrToIntArray(serverVersion);
        if (serverVersionArr == null) {
            throw new ServerVersionInvalidException(
                "The server version could not be determined.");
        }

        if (clientVersion == null) {
            log.error("client does not have a version");
            throw new ClientVersionInvalidException(
                "Client authentication failed. "
                    + "The Java Client version is not compatible with the server and must be upgraded.");
        }

        int[] clientVersionArr = versionStrToIntArray(clientVersion);
        if (clientVersionArr == null) {
            throw new ClientVersionInvalidException(
                "The Java Client version is not valid.");
        }

        log.info("check version: server_version/" + serverVersion
            + " client_version/" + clientVersion);

        if (clientVersionArr[0] < serverVersionArr[0]) {
            throw new ServerVersionNewerException(
                "Client authentication failed. "
                    + "The Java Client version is too old to connect to this server.");
        } else if (clientVersionArr[0] > serverVersionArr[0]) {
            throw new ServerVersionOlderException(
                "Client authentication failed. "
                    + "The Java Client version is too new to connect to this server.");
        } else {
            if (clientVersionArr[1] < serverVersionArr[1]) {
                throw new ServerVersionNewerException(
                    "Client authentication failed. "
                        + "The Java Client version is too old to connect to this server.");
            } else if (clientVersionArr[1] > serverVersionArr[1]) {
                throw new ServerVersionOlderException(
                    "Client authentication failed. "
                        + "The Java Client version is too new to connect to this server.");
            }
        }
    }
}
