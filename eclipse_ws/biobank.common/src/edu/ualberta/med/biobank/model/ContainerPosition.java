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

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.parentContainer.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "PARENT_CONTAINER_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName = "CONTAINER_TYPE_ID")
    })
    public Container getParentContainer() {
        return parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ContainerPosition.container.NotNull}")
    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "CONTAINER_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "CONTAINER_TYPE_ID", referencedColumnName = "CONTAINER_TYPE_ID")
    })
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull(message = "TODO: a better message")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "CONTAINER_TYPE_ID", referencedColumnName = "CHILD_CONTAINER_TYPE_ID", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName = "PARENT_CONTAINER_TYPE_ID", nullable = false, insertable = false, updatable = false)
    })
    ContainerTypeContainerType getContainerTypeContainerType() {
        ContainerTypeContainerType ctct = null;
        ContainerType parentCt = getParentContainer().getContainerType();
        ContainerType ct = getContainer().getContainerType();
        for (ContainerType childCt : parentCt.getChildContainerTypes()) {
            if (childCt.equals(ct)) {
                ctct = new ContainerTypeContainerType(parentCt, childCt);
                break;
            }
        }
        return ctct;
    }

    void setContainerTypeContainerType(ContainerTypeContainerType ctct) {
    }
}
