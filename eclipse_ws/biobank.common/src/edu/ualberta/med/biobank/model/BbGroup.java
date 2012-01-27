package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.validator.NotEmpty;

public class BbGroup extends Principal {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Collection<User> userCollection = new HashSet<User>();

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }
}