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
    @JoinColumn(name = "PARENT_CONTAINER_ID", nullable = false)
    public Container getParentContainer() {
        return this.parentContainer;
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
        @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", referencedColumnName = "CHILD_ID", nullable = false),
        @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", referencedColumnName = "PARENT_ID", nullable = false)
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
