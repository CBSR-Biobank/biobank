package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "CONTAINER_TYPE_CONTAINER_TYPE")
@NotUsed(by = ContainerPosition.class, property = "containerTypeContainerType", groups = PreDelete.class)
public class ContainerTypeContainerType implements Serializable {
    private static final long serialVersionUID = 1L;

    private ContainerType parent;
    private ContainerType child;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID")
    public ContainerType getParent() {
        return parent;
    }

    public void setParent(ContainerType parent) {
        this.parent = parent;
    }

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID")
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
