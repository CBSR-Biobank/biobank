package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class User implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private boolean isLockedOut;

    private List<SiteWrapper> workingSites;

    private SiteWrapper currentWorkingSite;

    public boolean isLockedOut() {
        return isLockedOut;
    }

    public void setLockedOut(boolean isLockedOut) {
        this.isLockedOut = isLockedOut;
    }

    private boolean needToChangePassword;

    private List<Group> groups = new ArrayList<Group>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setNeedToChangePassword(boolean needToChangePassword) {
        this.needToChangePassword = needToChangePassword;
    }

    public boolean passwordChangeRequired() {
        return needToChangePassword;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void copy(User user) {
        id = user.getId();
        login = user.getLogin();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        password = user.getPassword();
        email = user.getEmail();
        needToChangePassword = user.passwordChangeRequired();
        groups = new ArrayList<Group>(user.getGroups());
    }

    public boolean isWebsiteAdministrator() {
        for (Group group : groups) {
            if (group.isWebsiteAdministrator()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSiteAdministrator(SiteWrapper site) {
        Integer id = null;
        if (site != null)
            id = site.getId();
        return isSiteAdministrator(id);
    }

    public boolean isSiteAdministrator(Integer siteId) {
        if (isWebsiteAdministrator()) {
            return true;
        }
        for (Group group : groups) {
            if (group.isSiteAdministrator(siteId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Use to check if a user can modify objects inside a site
     */
    public boolean canUpdateSite(SiteWrapper site) {
        Integer id = null;
        if (site != null)
            id = site.getId();
        return canUpdateSite(id);
    }

    /**
     * Use to check if a user can modify objects inside a site
     */
    public boolean canUpdateSite(Integer siteId) {
        if (siteId == null)
            return false;
        return hasPrivilegeOnObject(Privilege.UPDATE, Site.class.getName(),
            siteId);
    }

    public boolean hasPrivilegeOnProtectionGroup(Privilege privilege,
        String protectionGroupName, Integer siteId) {
        for (Group group : groups) {
            if (group.hasPrivilegeOnProtectionGroup(privilege,
                protectionGroupName, siteId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPrivilegeOnObject(Privilege privilege, Integer siteId,
        ModelWrapper<?> modelWrapper) {
        boolean canCreateDeleteUpdate = true;
        String type = modelWrapper.getWrappedClass().getName();
        if (privilege != Privilege.READ && siteId != null)
            canCreateDeleteUpdate = canUpdateSite(siteId);
        canCreateDeleteUpdate = canCreateDeleteUpdate
            && modelWrapper.checkSpecificAccess(this, siteId);
        return canCreateDeleteUpdate
            && hasPrivilegeOnObject(privilege, type, modelWrapper.getId());
    }

    public boolean hasPrivilegeOnObject(Privilege privilege, Integer siteId,
        Class<?> objectClazz, Integer objectId) {
        if (ModelWrapper.class.isAssignableFrom(objectClazz)) {
            ModelWrapper<?> wrapper = null;
            try {
                Constructor<?> constructor = objectClazz
                    .getConstructor(WritableApplicationService.class);
                wrapper = (ModelWrapper<?>) constructor
                    .newInstance((WritableApplicationService) null);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return hasPrivilegeOnObject(privilege, siteId, wrapper);
        }
        String type = objectClazz.getName();
        return hasPrivilegeOnObject(privilege, type, objectId);
    }

    private boolean hasPrivilegeOnObject(Privilege privilege, String type,
        Integer id) {
        for (Group group : groups) {
            if (group.hasPrivilegeOnObject(privilege, type, id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("id", id);
        properties.put("login", login);
        properties.put("password", password);
        properties.put("firstName", firstName);
        properties.put("lastName", lastName);
        properties.put("email", email);
        properties.put("groups", groups);
        return properties.toString();
    }

    // FIXME list caching : what if a new site ? user should log off anyway ?
    // Should return centres when the full security is set
    public List<SiteWrapper> getWorkingCenters(
        WritableApplicationService appService) throws Exception {
        if (workingSites == null) {
            List<SiteWrapper> allSites = SiteWrapper.getSites(appService);
            workingSites = new ArrayList<SiteWrapper>();
            for (SiteWrapper site : allSites) {
                if (canUpdateSite(site.getId())) {
                    workingSites.add(site);
                }
            }
        }
        return workingSites;
    }

    public void setCurrentWorkingSite(SiteWrapper site) {
        this.currentWorkingSite = site;
    }

    public SiteWrapper getCurrentWorkingCentre() {
        return currentWorkingSite;
    }
}
