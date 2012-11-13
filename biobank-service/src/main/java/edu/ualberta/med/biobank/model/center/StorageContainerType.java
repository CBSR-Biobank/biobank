package edu.ualberta.med.biobank.model.center;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;


/**
 * Represents a type or classification of storage containers that holds other
 * storage containers, such as, freezers, dewars, hotels, pallets, etc.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@DiscriminatorValue("ST")
public class StorageContainerType
    extends ContainerType {
    private static final long serialVersionUID = 1L;

    private Boolean topLevel;
    private Set<ContainerType> containerTypes = new HashSet<ContainerType>(0);

    /**
     * @return true if this {@link ContainerType} should <em>not</em> be able to
     *         be the child of another {@link ContainerType}, otherwise false.
     */
    @NotNull(message = "{ContainerType.topLevel.NotNull}")
    @Column(name = "IS_TOP_LEVEL", nullable = false)
    public Boolean isTopLevel() {
        return this.topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    /**
     * @return the {@link ContainerType}s that this {@link ContainerType} can
     *         hold as children.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_TYPE_CONTAINER_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_CONTAINER_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_CONTAINER_TYPE_ID", nullable = false, updatable = false) })
    public Set<ContainerType> getChildContainerTypes() {
        return this.containerTypes;
    }

    public void setChildContainerTypes(Set<ContainerType> childContainerTypes) {
        this.containerTypes = childContainerTypes;
    }
}
