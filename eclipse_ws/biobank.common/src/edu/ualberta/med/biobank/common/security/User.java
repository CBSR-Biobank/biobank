package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
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

    private transient List<CenterWrapper<?>> workingCenters;

    private transient CenterWrapper<?> currentWorkingCenter;

    // only the current working center id will be serialized
    private Integer currentWorkingCenterId;

    private boolean needToChangePassword;

    private List<Group> groups = new ArrayList<Group>();

    private List<Integer> workingCenterIds;

    private boolean inSuperAdminMode = false;

    /**
     * [object type | privilege] = list of center class names. Specific rights
     * applied on the center type.
     */
    private static transient Map<TypePrivilegeKey, List<String>> specificRightsMapping;

    private static class TypePrivilegeKey {
        public String type;
        public Privilege privilege;

        public TypePrivilegeKey(String type, Privilege privilege) {
            this.type = type;
            this.privilege = privilege;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof TypePrivilegeKey) {
                TypePrivilegeKey tpk = (TypePrivilegeKey) object;
                return type.equals(tpk.type) && privilege.equals(tpk.privilege);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return type.hashCode() + privilege.hashCode();
        }
    }

    static {
        specificRightsMapping = new HashMap<TypePrivilegeKey, List<String>>();

        addSpecificMappings(
            Container.class.getName(),
            Arrays.asList(Privilege.CREATE, Privilege.UPDATE, Privilege.DELETE),
            Arrays.asList(Site.class.getName()));
        addSpecificMappings(
            ContainerType.class.getName(),
            Arrays.asList(Privilege.CREATE, Privilege.UPDATE, Privilege.DELETE),
            Arrays.asList(Site.class.getName()));
    }

    private static void addSpecificMappings(String objectType,
        List<Privilege> privileges, List<String> centerClassNames) {
        for (Privilege privilege : privileges) {
            specificRightsMapping.put(new TypePrivilegeKey(objectType,
                privilege), centerClassNames);
        }
    }

    public boolean isLockedOut() {
        return isLockedOut;
    }

    public void setLockedOut(boolean isLockedOut) {
        this.isLockedOut = isLockedOut;
    }

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
            if (group.isSuperAdministratorGroup()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if this user is administrator for this center.
     */
    public boolean isAdministratorForCurrentCenter() {
        if (currentWorkingCenter != null && isInSuperAdminMode())
            return true;
        for (Group group : groups) {
            if (group.isAdministratorForCenter(currentWorkingCenter)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInSuperAdminMode() {
        return inSuperAdminMode;
    }

    public void setInSuperAdminMode(boolean inSuperAdminMode) {
        this.inSuperAdminMode = inSuperAdminMode && isSuperAdministrator();
    }

    public boolean hasPrivilegeOnProtectionGroup(Privilege privilege,
        String protectionGroupName) {
        for (Group group : groups) {
            if (group.hasPrivilegeOnProtectionGroup(privilege,
                protectionGroupName, currentWorkingCenter)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPrivilegeOnObject(Privilege privilege,
        Class<?> objectClazz) {
        return hasPrivilegeOnObject(privilege, objectClazz, null);
    }

    public boolean hasPrivilegeOnObject(Privilege privilege,
        Class<?> objectClazz, List<? extends CenterWrapper<?>> specificCenters) {
        String type = objectClazz.getName();
        if (ModelWrapper.class.isAssignableFrom(objectClazz)) {
            ModelWrapper<?> wrapper = null;
            try {
                Constructor<?> constructor = objectClazz
                    .getConstructor(WritableApplicationService.class);
                wrapper = (ModelWrapper<?>) constructor
                    .newInstance((WritableApplicationService) null);
            } catch (NoSuchMethodException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            } catch (IllegalAccessException e) {
                return false;
            } catch (InstantiationException e) {
                return false;
            }
            type = wrapper.getWrappedClass().getName();
        }
        boolean currentCenterRights = true;
        CenterWrapper<?> currentCenter = getCurrentWorkingCenter();
        if (!isInSuperAdminMode() && currentCenter != null) {
            // check object specific rights depending on center type
            List<String> centerSpecificRights = specificRightsMapping
                .get(new TypePrivilegeKey(type, privilege));
            if (centerSpecificRights != null) {
                currentCenterRights = centerSpecificRights
                    .contains(currentCenter.getWrappedClass().getName());
            }
            // check object rights depending on centers set on object
            if (specificCenters != null && specificCenters.size() > 0)
                currentCenterRights = currentCenterRights
                    && specificCenters.contains(currentCenter);
        }
        return currentCenterRights && hasPrivilegeOnObject(privilege, type);
    }

    private boolean hasPrivilegeOnObject(Privilege privilege, String type) {
        for (Group group : groups) {
            if (group.hasPrivilegeOnObject(privilege, type)) {
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
                .getCenters(appService);
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
        currentWorkingCenter = center;
        // only the id is serialized
        if (center == null)
            currentWorkingCenterId = null;
        else
            currentWorkingCenterId = center.getId();
    }

    public CenterWrapper<?> getCurrentWorkingCenter() {
        return currentWorkingCenter;
    }

    public SiteWrapper getCurrentWorkingSite() {
        if (currentWorkingCenter instanceof SiteWrapper)
            return (SiteWrapper) currentWorkingCenter;
        return null;
    }

    /**
     * To use on the server side to initialize the currentWorkingCenter from the
     * id
     */
    public void initCurrentWorkingCenter(WritableApplicationService appService) {
        if (currentWorkingCenter == null && currentWorkingCenterId != null) {
            try {
                currentWorkingCenter = CenterWrapper.getCenterFromId(
                    appService, currentWorkingCenterId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean canPerformActions(SecurityFeature... features) {
        return canPerformActions(Arrays.asList(features));
    }

    // FIXME for now assume features are center features (so can use
    // isAdministratorForCurrentCenter)
    public boolean canPerformActions(List<SecurityFeature> features) {
        boolean ok = isAdministratorForCurrentCenter();
        for (SecurityFeature feature : features) {
            ok = ok
                || hasPrivilegeOnProtectionGroup(Privilege.UPDATE,
                    feature.getName());
        }
        return ok;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User)
            return login.equals(((User) o).login);
        return false;
    }

    @Override
    public int hashCode() {
        if (login == null)
            return super.hashCode();
        return login.hashCode();
    }
}
