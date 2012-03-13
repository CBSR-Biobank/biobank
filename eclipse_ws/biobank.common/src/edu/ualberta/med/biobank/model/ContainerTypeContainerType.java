package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "CONTAINER_TYPE_CONTAINER_TYPE")
@NotUsed(by = ContainerPosition.class, property = "containerTypeContainerType", groups = PreDelete.class)
public class ContainerTypeContainerType {
    private Id id = new Id();
    private ContainerType parentContainerType;
    private ContainerType childContainerType;

    @EmbeddedId
    Id getId() {
        return id;
    }

    void setId(Id id) {
        this.id = id;
    }

    @MapsId("parentContainerTypeId")
    @ManyToOne
    @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", insertable = false, updatable = false)
    public ContainerType getParentContainerType() {
        return parentContainerType;
    }

    public void setParentContainerType(ContainerType parentContainerType) {
        this.parentContainerType = parentContainerType;
    }

    @MapsId("childContainerTypeId")
    @ManyToOne
    @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", insertable = false, updatable = false)
    public ContainerType getChildContainerType() {
        return childContainerType;
    }

    public void setChildContainerType(ContainerType childContainerType) {
        this.childContainerType = childContainerType;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Id) {
            ContainerTypeContainerType that = (ContainerTypeContainerType) o;
            return getId().equals(that.getId()) && getId().equals(that.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Embeddable
    public static class Id implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer parentContainerTypeId = 0;
        private Integer childContainerTypeId = 0;

        @Column(name = "PARENT_CONTAINER_TYPE_ID")
        public Integer getParentContainerTypeId() {
            return parentContainerTypeId;
        }

        public void setParentContainerTypeId(Integer parentContainerTypeId) {
            this.parentContainerTypeId = parentContainerTypeId;
        }

        @Column(name = "CHILD_CONTAINER_TYPE_ID")
        public Integer getChildContainerTypeId() {
            return childContainerTypeId;
        }

        public void setChildContainerTypeId(Integer childContainerTypeId) {
            this.childContainerTypeId = childContainerTypeId;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Id) {
                Id that = (Id) o;
                return this.parentContainerTypeId
                    .equals(that.parentContainerTypeId)
                    && this.childContainerTypeId
                        .equals(that.childContainerTypeId);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return parentContainerTypeId.hashCode()
                + childContainerTypeId.hashCode();
        }
    }
}
