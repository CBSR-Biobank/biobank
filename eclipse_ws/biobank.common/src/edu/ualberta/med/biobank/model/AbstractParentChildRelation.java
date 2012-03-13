package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class AbstractParentChildRelation<P, C> {
    private Id id = new Id();
    private P parent;
    private C child;

    @EmbeddedId
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    @NotNull
    @MapsId("parentId")
    @ManyToOne
    @JoinColumn(name = "PARENT_ID", insertable = false, updatable = false)
    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    @NotNull
    @MapsId("childId")
    @ManyToOne
    @JoinColumn(name = "CHILD_ID", insertable = false, updatable = false)
    public C getChild() {
        return child;
    }

    public void setChild(C child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Id) {
            AbstractParentChildRelation<?, ?> that =
                (AbstractParentChildRelation<?, ?>) o;
            return getId().equals(that.getId());
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

        private Integer parentId = 0;
        private Integer childId = 0;

        @Column(name = "PARENT_ID", nullable = false)
        public Integer getParentId() {
            return parentId;
        }

        public void setParentId(Integer parentId) {
            this.parentId = parentId;
        }

        @Column(name = "CHILD_ID", nullable = false)
        public Integer getChildId() {
            return childId;
        }

        public void setChildId(Integer childId) {
            this.childId = childId;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Id) {
                Id that = (Id) o;
                return this.parentId.equals(that.parentId)
                    && this.childId.equals(that.childId);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return parentId.hashCode() + childId.hashCode();
        }
    }
}
