package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class Site extends Center {
    private static final long serialVersionUID = 1L;

    private Collection<Study> studyCollection = new HashSet<Study>();
    private Collection<ContainerType> containerTypeCollection =
        new HashSet<ContainerType>();
    private Collection<Container> containerCollection =
        new HashSet<Container>();

    public Collection<Study> getStudyCollection() {
        return studyCollection;
    }

    public void setStudyCollection(Collection<Study> studyCollection) {
        this.studyCollection = studyCollection;
    }

    public Collection<ContainerType> getContainerTypeCollection() {
        return containerTypeCollection;
    }

    public void setContainerTypeCollection(
        Collection<ContainerType> containerTypeCollection) {
        this.containerTypeCollection = containerTypeCollection;
    }

    public Collection<Container> getContainerCollection() {
        return containerCollection;
    }

    public void setContainerCollection(Collection<Container> containerCollection) {
        this.containerCollection = containerCollection;
    }

}