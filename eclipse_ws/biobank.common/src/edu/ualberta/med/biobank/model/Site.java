package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("Site")
public class Site extends Center {
    private static final long serialVersionUID = 1L;

    private Collection<Study> studyCollection = new HashSet<Study>(0);
    private Collection<ContainerType> containerTypeCollection =
        new HashSet<ContainerType>(0);
    private Collection<Container> containerCollection = new HashSet<Container>(
        0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SITE_STUDY",
        joinColumns = { @JoinColumn(name = "SITE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) })
    public Collection<Study> getStudyCollection() {
        return this.studyCollection;
    }

    public void setStudyCollection(Collection<Study> studyCollection) {
        this.studyCollection = studyCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "site")
    public Collection<ContainerType> getContainerTypeCollection() {
        return this.containerTypeCollection;
    }

    public void setContainerTypeCollection(
        Collection<ContainerType> containerTypeCollection) {
        this.containerTypeCollection = containerTypeCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "site")
    public Collection<Container> getContainerCollection() {
        return this.containerCollection;
    }

    public void setContainerCollection(Collection<Container> containerCollection) {
        this.containerCollection = containerCollection;
    }
}
