package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "CONTAINER_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "PARENT_CONTAINER_ID", "ROW", "COL" }) })
@Unique(properties = { "parentContainer", "row", "col" }, groups = PrePersist.class)
public class ContainerPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container parentContainer;
    private Container container;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PARENT_CONTAINER_ID", referencedColumnName = "CONTAINER_ID"),
        @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName = "CONTAINER_TYPE_ID")
    })
    ContainerContainerType getParentContainerContainerType() {
        return parentContainer.getContainerContainerType();
    }

    void setParentContainerContainerType(ContainerContainerType pcct) {
        this.parentContainer = pcct.getContainer();
    }

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CONTAINER_ID", referencedColumnName = "CONTAINER_ID"),
        @JoinColumn(name = "CONTAINER_TYPE_ID", referencedColumnName = "CONTAINER_TYPE_ID")
    })
    ContainerContainerType getContainerContainerType() {
        return container.getContainerContainerType();
    }

    void setContainerContainerType(ContainerContainerType cct) {
        this.container = cct.getContainer();
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.parentContainer.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CONTAINER_ID", nullable = false, insertable = false, updatable = false)
    public Container getParentContainer() {
        return parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.container.NotNull}")
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "position")
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull(message = "TODO: a better message")
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CONTAINER_TYPE_ID", referencedColumnName = "CHILD_CONTAINER_TYPE_ID", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName = "PARENT_CONTAINER_TYPE_ID", nullable = false, insertable = false, updatable = false)
    })
    ContainerTypeContainerType getContainerTypeContainerType() {
        ContainerType parentCt = getParentContainer().getContainerType();
        ContainerType childCt = getContainer().getContainerType();
        for (ContainerTypeContainerType ctCt : parentCt
            .getChild2ContainerTypeContainerTypes()) {
            if (ctCt.getParent().equals(parentCt)
                && ctCt.getChild().equals(childCt)) {
                return ctCt;
            }
        }
        return null;
    }

    void setContainerTypeContainerType(ContainerTypeContainerType ctct) {
    }
}
