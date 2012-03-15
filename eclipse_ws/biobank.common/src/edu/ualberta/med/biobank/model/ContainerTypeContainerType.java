package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Dummy class that should never actually be saved, it's only used to create a
 * reference from {@link ContainerPosition} to the {@link ContainerType}-
 * {@link ContainerType} relation.
 * 
 * @author Jonathan Ferland
 */
@Entity
@Table(name = "CONTAINER_TYPE_CONTAINER_TYPE")
public class ContainerTypeContainerType implements Serializable {
    private static final long serialVersionUID = 1L;

    private ContainerType parent;
    private ContainerType child;

    public ContainerTypeContainerType() {
    }

    public ContainerTypeContainerType(ContainerType parent, ContainerType child) {
        this.parent = parent;
        this.child = child;
    }

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", insertable = false, updatable = false)
    public ContainerType getParent() {
        return parent;
    }

    public void setParent(ContainerType parent) {
        this.parent = parent;
    }

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", insertable = false, updatable = false)
    public ContainerType getChild() {
        return child;
    }

    public void setChild(ContainerType child) {
        this.child = child;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((child == null) ? 0 : child.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ContainerTypeContainerType other = (ContainerTypeContainerType) obj;
        if (child == null) {
            if (other.child != null) return false;
        } else if (!child.equals(other.child)) return false;
        if (parent == null) {
            if (other.parent != null) return false;
        } else if (!parent.equals(other.parent)) return false;
        return true;
    }
}
