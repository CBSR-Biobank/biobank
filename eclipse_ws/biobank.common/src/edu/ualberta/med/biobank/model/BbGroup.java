package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.validator.NotEmpty;

@Entity
@DiscriminatorValue("BbGroup")
public class BbGroup extends Principal {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private Set<User> userCollection = new HashSet<User>(0);

    @NotEmpty
    @Column(name = "NAME", unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "GROUP_USER",
        joinColumns = { @JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "USER_ID", nullable = false, updatable = false) })
    public Set<User> getUserCollection() {
        return this.userCollection;
    }

    public void setUserCollection(Set<User> userCollection) {
        this.userCollection = userCollection;
    }
}
