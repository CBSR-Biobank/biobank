package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PreInsert;
import edu.ualberta.med.biobank.validator.group.PreUpdate;

@Entity
@Table(name = "CONTAINER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "SITE_ID", "CONTAINER_TYPE_ID",
            "LABEL" }),
        @UniqueConstraint(columnNames = { "SITE_ID", "PRODUCT_BARCODE" }) })
// TODO: consider pulling @UniqueConstraint into this @Unique annotation,
// because this is a total repeating of constraints. Would then need to figure
// out how to add DDL constraints from our annotations and how to get a bean's
// value of a specific column.
@Unique.List({
    @Unique(properties = { "site.id", "containerType.id", "label" },
        groups = { PreInsert.class, PreUpdate.class },
        message = "{edu.ualberta.med.biobank.model.Container.label.Unique}"),
    @Unique(properties = { "site.id", "productBarcode" },
        groups = { PreInsert.class, PreUpdate.class },
        message = "{edu.ualberta.med.biobank.model.Container.productBarcode.Unique}")
})
@Empty.List({
    @Empty(property = "specimenPositionCollection", groups = PreDelete.class,
        message = "edu.ualberta.med.biobank.model.Container.Empty.specimenPositionCollection"),
    @Empty(property = "childPositionCollection", groups = PreDelete.class,
        message = "edu.ualberta.med.biobank.model.Container.Empty.childPositionCollection")
})
public class Container extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String productBarcode;
    private String label;
    private Double temperature;
    private String path;
    private Set<Comment> commentCollection = new HashSet<Comment>(0);
    private Set<ContainerPosition> childPositionCollection =
        new HashSet<ContainerPosition>(0);
    private Container topContainer;
    private Set<SpecimenPosition> specimenPositionCollection =
        new HashSet<SpecimenPosition>(0);
    private ContainerType containerType;
    private ContainerPosition position;
    private Site site;
    private ActivityStatus activityStatus;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Container.productBarcode.Unique}")
    @Column(name = "PRODUCT_BARCODE")
    public String getProductBarcode() {
        return this.productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Container.label.NotEmpty}")
    @Column(name = "LABEL", nullable = false)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // TODO: should be decimal?
    @Column(name = "TEMPERATURE")
    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.path.NotEmpty}")
    @Column(name = "PATH", nullable = false)
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_COMMENT",
        joinColumns = { @JoinColumn(name = "CONTAINER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentContainer")
    public Set<ContainerPosition> getChildPositionCollection() {
        return this.childPositionCollection;
    }

    public void setChildPositionCollection(
        Set<ContainerPosition> childPositionCollection) {
        this.childPositionCollection = childPositionCollection;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_CONTAINER_ID")
    public Container getTopContainer() {
        return this.topContainer;
    }

    public void setTopContainer(Container topContainer) {
        this.topContainer = topContainer;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "container")
    public Set<SpecimenPosition> getSpecimenPositionCollection() {
        return this.specimenPositionCollection;
    }

    public void setSpecimenPositionCollection(
        Set<SpecimenPosition> specimenPositionCollection) {
        this.specimenPositionCollection = specimenPositionCollection;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.containerType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false)
    public ContainerType getContainerType() {
        return this.containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID", unique = true)
    public ContainerPosition getPosition() {
        return this.position;
    }

    public void setPosition(ContainerPosition position) {
        this.position = position;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.site.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", nullable = false)
    public Site getSite() {
        return this.site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Container.activityStatus.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
