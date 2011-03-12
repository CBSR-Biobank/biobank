package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Center;
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

    private List<CenterWrapper<?>> workingCenters;

    private transient CenterWrapper<?> currentWorkingCenter;

    public boolean isLockedOut() {
        return isLockedOut;
    }

    public void setLockedOut(boolean isLockedOut) {
        this.isLockedOut = isLockedOut;
    }

    private boolean needToChangePassword;

    private List<Group> groups = new ArrayList<Group>();

    private List<Integer> workingCenterIds;

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

    public boolean isSuperAdministrator() {
        for (Group group : groups) {
            if (group.isSuperAdministrator()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCenterAdministrator(CenterWrapper<?> center) {
        Integer id = null;
        if (center != null)
            id = center.getId();
        return isCenterAdministrator(id,
            center == null ? null : center.getWrappedClass());
    }

    public boolean isCenterAdministrator(Integer centerId,
        Class<? extends Center> centerClass) {
        if (isSuperAdministrator()) {
            return true;
        }
        for (Group group : groups) {
            if (group.isAdministratorForCenter(centerId, centerClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Use to check if a user can modify objects inside a center
     */
    public boolean canUpdateCenter(CenterWrapper<?> center) {
        Integer id = null;
        if (center != null)
            id = center.getId();
        return canUpdateCenter(id,
            center == null ? null : center.getWrappedClass());
    }

    /**
     * Use to check if a user can modify objects inside a center
     */
    public boolean canUpdateCenter(Integer centerId,
        Class<? extends Center> centerClass) {
        if (centerId == null)
            return false;
        return hasPrivilegeOnObject(
            Privilege.UPDATE,
            centerClass == null ? Center.class.getName() : centerClass
                .getName(), centerId);
    }

    public boolean hasPrivilegeOnProtectionGroup(Privilege privilege,
        String protectionGroupName, Integer centerId,
        Class<? extends Center> centerClass) {
        for (Group group : groups) {
            if (group.hasPrivilegeOnProtectionGroup(privilege,
                protectionGroupName, centerId, centerClass)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPrivilegeOnObject(Privilege privilege,
        CenterWrapper<?> center, ModelWrapper<?> modelWrapper) {
        boolean canCreateDeleteUpdate = true;
        String type = modelWrapper.getWrappedClass().getName();
        if (privilege != Privilege.READ && center != null)
            canCreateDeleteUpdate = canUpdateCenter(center);
        canCreateDeleteUpdate = canCreateDeleteUpdate
            && modelWrapper.checkSpecificAccess(this, center);
        return canCreateDeleteUpdate
            && hasPrivilegeOnObject(privilege, type, modelWrapper.getId());
    }

    public boolean hasPrivilegeOnObject(Privilege privilege,
        CenterWrapper<?> center, Class<?> objectClazz, Integer objectId) {
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
            return hasPrivilegeOnObject(privilege, center, wrapper);
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
    // Should return centers when the full security is set
    public List<CenterWrapper<?>> getWorkingCenters(
        WritableApplicationService appService) throws Exception {
        if (workingCenters == null) {
            List<CenterWrapper<?>> allCenters = CenterWrapper
                .getAllCenters(appService);
            workingCenters = new ArrayList<CenterWrapper<?>>();
            for (CenterWrapper<?> center : allCenters) {
                if (getWorkingCenterIds().contains(center.getId())) {
                    workingCenters.add(center);
                }
            }
        }
        return workingCenters;
    }

    private List<Integer> getWorkingCenterIds() {
        if (workingCenterIds == null) {
            workingCenterIds = new ArrayList<Integer>();
            for (Group group : getGroups()) {
                workingCenterIds.addAll(group.getWorkingCenterIds());
            }
        }
        return workingCenterIds;
    }

    public void setCurrentWorkingCenter(CenterWrapper<?> center) {
        this.currentWorkingCenter = center;
    }

    public CenterWrapper<?> getCurrentWorkingCenter() {
        try {
            if (currentWorkingCenter != null)
                currentWorkingCenter.reload();
        } catch (Exception e) {
            // FIXME: how to handle?
            e.printStackTrace();
        }
        return currentWorkingCenter;
    }

    public SiteWrapper getCurrentWorkingSite() {
        if (currentWorkingCenter instanceof SiteWrapper)
            return (SiteWrapper) currentWorkingCenter;
        return null;
    }
}
