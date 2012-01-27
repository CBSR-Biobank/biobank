package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class Container extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String productBarcode;
    private String label;
    private Double temperature;
    private String path;
    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private Collection<ContainerPosition> childPositionCollection =
        new HashSet<ContainerPosition>();
    private Container topContainer;
    private Collection<SpecimenPosition> specimenPositionCollection =
        new HashSet<SpecimenPosition>();
    private ContainerType containerType;
    private ContainerPosition position;
    private Site site;
    private ActivityStatus activityStatus;

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    @NotEmpty
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public Collection<ContainerPosition> getChildPositionCollection() {
        return childPositionCollection;
    }

    public void setChildPositionCollection(
        Collection<ContainerPosition> childPositionCollection) {
        this.childPositionCollection = childPositionCollection;
    }

    public Container getTopContainer() {
        return topContainer;
    }

    public void setTopContainer(Container topContainer) {
        this.topContainer = topContainer;
    }

    public Collection<SpecimenPosition> getSpecimenPositionCollection() {
        return specimenPositionCollection;
    }

    public void setSpecimenPositionCollection(
        Collection<SpecimenPosition> specimenPositionCollection) {
        this.specimenPositionCollection = specimenPositionCollection;
    }

    @NotNull
    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    @NotNull
    public ContainerPosition getPosition() {
        return position;
    }

    public void setPosition(ContainerPosition position) {
        this.position = position;
    }

    @NotNull
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @NotNull
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
