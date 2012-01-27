package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;


public class Membership extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Collection<Permission> permissionCollection =
        new HashSet<Permission>();
    private Center center;
    private Collection<Role> roleCollection = new HashSet<Role>();
    private Study study;
    private Principal principal;

    public Collection<Permission> getPermissionCollection() {
        return permissionCollection;
    }

    public void setPermissionCollection(
        Collection<Permission> permissionCollection) {
        this.permissionCollection = permissionCollection;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Collection<Role> getRoleCollection() {
        return roleCollection;
    }

    public void setRoleCollection(Collection<Role> roleCollection) {
        this.roleCollection = roleCollection;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
