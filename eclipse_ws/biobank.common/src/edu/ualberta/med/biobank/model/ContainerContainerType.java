package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONTAINER_CONTAINER_TYPE")
public class ContainerContainerType implements Serializable {
    private static final long serialVersionUID = 1L;

    private ContainerType containerType;
    private Container container;

    public ContainerContainerType() {
    }

    public ContainerContainerType(Container container) {
        this.container = container;
    }

    public ContainerContainerType(Container container,
        ContainerType containerType) {
        this.container = container;
        this.containerType = containerType;
    }

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false, updatable = false)
    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "CONTAINER_ID", nullable = false, updatable = false)
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((container == null) ? 0 : container.hashCode());
        result =
            prime * result
                + ((containerType == null) ? 0 : containerType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ContainerContainerType other = (ContainerContainerType) obj;
        if (container == null) {
            if (other.container != null) return false;
        } else if (!container.equals(other.container)) return false;
        if (containerType == null) {
            if (other.containerType != null) return false;
        } else if (!containerType.equals(other.containerType)) return false;
        return true;
    }
}