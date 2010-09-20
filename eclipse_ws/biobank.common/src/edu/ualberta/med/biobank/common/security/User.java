package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private String email;

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

    public boolean isNeedToChangePassword() {
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
        needToChangePassword = user.isNeedToChangePassword();
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

    @Override
    public String toString() {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("login", login);
        properties.put("password", password);
        properties.put("firstName", firstName);
        properties.put("lastName", lastName);
        properties.put("email", email);
        properties.put("groups", groups);
        return properties.toString();
    }
}
