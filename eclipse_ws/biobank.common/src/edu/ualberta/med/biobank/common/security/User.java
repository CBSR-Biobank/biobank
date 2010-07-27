package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public void addGroups(List<Group> groups) {
        this.groups.addAll(groups);
    }

    public void removeGroups(List<Group> groups) {
        this.groups.removeAll(groups);
    }

    public List<Group> getGroups() {
        return groups;
    }

}
