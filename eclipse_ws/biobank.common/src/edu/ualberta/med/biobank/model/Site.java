package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import edu.ualberta.med.biobank.validator.constraint.Empty;

@Entity
@DiscriminatorValue("Site")
@Empty.List({
    @Empty(property = "containerCollection",
        message = "edu.ualberta.med.biobank.model.Site.Empty.containerCollection"),
    @Empty(property = "containerTypeCollection",
        message = "edu.ualberta.med.biobank.model.Site.Empty.containerTypeCollection"),
    @Empty(property = "processingEventCollection",
        message = "edu.ualberta.med.biobank.model.Site.Empty.processingEventCollection")
})
public class Site extends Center {
    private static final long serialVersionUID = 1L;

    private Set<Study> studyCollection = new HashSet<Study>(0);
    private Set<ContainerType> containerTypeCollection =
        new HashSet<ContainerType>(0);
    private Set<Container> containerCollection = new HashSet<Container>(
        0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SITE_STUDY",
        joinColumns = { @JoinColumn(name = "SITE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) })
    public Set<Study> getStudyCollection() {
        return this.studyCollection;
    }

    public void setStudyCollection(Set<Study> studyCollection) {
        this.studyCollection = studyCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "site")
    public Set<ContainerType> getContainerTypeCollection() {
        return this.containerTypeCollection;
    }

    public void setContainerTypeCollection(
        Set<ContainerType> containerTypeCollection) {
        this.containerTypeCollection = containerTypeCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "site")
    public Set<Container> getContainerCollection() {
        return this.containerCollection;
    }

    public void setContainerCollection(Set<Container> containerCollection) {
        this.containerCollection = containerCollection;
    }
}
