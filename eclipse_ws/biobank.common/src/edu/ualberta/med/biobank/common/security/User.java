package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class User implements Serializable, BiobankSecurity {

    private static final long serialVersionUID = 1L;

    private static final String CONTAINER_ADMINISTRATION_STRING = "biobank.cbsr.container.administration";

    private static final String GROUP_WEBSITE_ADMINISTRATOR = "Website Administrator";

    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private String email;

    private boolean needToChangePassword;

    private List<Group> groups = new ArrayList<Group>();

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

    public boolean isNeedToChangePassword() {
        return needToChangePassword;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return getLogin() + "/" + getFirstName() + "/" + getLastName() + "/"
            + getPassword() + "/" + getEmail();
    }

    public boolean hasRoleOnObject(Role role, Class<?> clazz) {
        return hasRoleOnObject(role, clazz, null);
    }

    public boolean hasRoleOnObject(Role role, Class<?> clazz, String objectId) {
        String objectName;
        if (ModelWrapper.class.isAssignableFrom(clazz)) {
            ModelWrapper<?> wrapper = null;
            try {
                Constructor<?> constructor = clazz
                    .getConstructor(WritableApplicationService.class);
                wrapper = (ModelWrapper<?>) constructor
                    .newInstance((WritableApplicationService) null);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            objectName = wrapper.getWrappedClass().getName();
        } else {
            objectName = clazz.getName();
        }
        return hasRoleOnObject(role, objectName, objectId);
    }

    public boolean hasRoleOnObject(Role role, String objectName) {
        return hasRoleOnObject(role, objectName, null);
    }

    private boolean hasRoleOnObject(Role role, String objectName,
        String objectId) {
        for (Group group : groups) {
            if (group.hasRoleOnObject(role, objectName, objectId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isContainerAdministrator() {
        return hasRoleOnObject(Role.CREATE, CONTAINER_ADMINISTRATION_STRING);
    }

    public boolean isWebsiteAdministrator() {
        for (Group group : groups) {
            if (group.getName().equals(GROUP_WEBSITE_ADMINISTRATOR)) {
                return true;
            }
        }
        return false;
    }

}
