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

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@DiscriminatorValue("BbGroup")
@Unique(properties = "name", groups = PrePersist.class)
public class Group extends Principal {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Set<User> users = new HashSet<User>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.BbGroup.name.NotEmpty}")
    @Column(name = "NAME", unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.BbGroup.name.NotEmpty}")
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
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
