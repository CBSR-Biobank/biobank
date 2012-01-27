package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

public class ContainerType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Boolean topLevel = false;
    private Double defaultTemperature;
    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private Collection<SpecimenType> specimenTypeCollection =
        new HashSet<SpecimenType>();
    private Collection<ContainerType> childContainerTypeCollection =
        new HashSet<ContainerType>();
    private ActivityStatus activityStatus;
    private Capacity capacity;
    private Site site;
    private ContainerLabelingScheme childLabelingScheme;
    private Collection<ContainerType> parentContainerTypeCollection =
        new HashSet<ContainerType>();

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @NotNull
    public Boolean getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    public Double getDefaultTemperature() {
        return defaultTemperature;
    }

    public void setDefaultTemperature(Double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> comments) {
        this.commentCollection = comments;
    }

    public Collection<SpecimenType> getSpecimenTypeCollection() {
        return specimenTypeCollection;
    }

    public void setSpecimenTypeCollection(
        Collection<SpecimenType> specimenTypeCollection) {
        this.specimenTypeCollection = specimenTypeCollection;
    }

    public Collection<ContainerType> getChildContainerTypeCollection() {
        return childContainerTypeCollection;
    }

    public void setChildContainerTypeCollection(
        Collection<ContainerType> childContainerTypeCollection) {
        this.childContainerTypeCollection = childContainerTypeCollection;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public ContainerLabelingScheme getChildLabelingScheme() {
        return childLabelingScheme;
    }

    public void setChildLabelingScheme(
        ContainerLabelingScheme childLabelingScheme) {
        this.childLabelingScheme = childLabelingScheme;
    }

    public Collection<ContainerType> getParentContainerTypeCollection() {
        return parentContainerTypeCollection;
    }

    public void setParentContainerTypeCollection(
        Collection<ContainerType> parentContainerTypeCollection) {
        this.parentContainerTypeCollection = parentContainerTypeCollection;
    }
}
