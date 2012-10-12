package edu.ualberta.med.biobank.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Defines which types of specimens (i.e. which {@link SpecimenGroup}s) need to
 * be collected as part of a {@link CollectionEventType}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_COLLECTION_EVENT_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "COLLECTION_EVENT_TYPE_ID",
        "SPECIMEN_GROUP_ID"
    })
})
@Unique(properties = { "type", "group" }, groups = PrePersist.class)
public class SpecimenCollectionEventType
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private CollectionEventType type;
    private SpecimenGroup group;
    private Integer count;
    private Decimal amount;
    private SpecimenContainerType containerType;

    @NotNull(message = "{SpecimenCollectionEventType.type.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_TYPE_ID", nullable = false)
    public CollectionEventType getType() {
        return type;
    }

    public void setType(CollectionEventType type) {
        this.type = type;
    }

    @NotNull(message = "{SpecimenCollectionEventType.group.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getGroup() {
        return group;
    }

    public void setGroup(SpecimenGroup group) {
        this.group = group;
    }

    @Column(name = "`COUNT`")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * @return the amount of substance that is expected to be in each collected
     *         {@link Specimen}, or null if this value has no default and each
     *         {@link Specimen} should have its {@link Specimen#getAmount()}
     *         read and entered without default).
     */
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "AMOUNT_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "AMOUNT_SCALE"))
    })
    public Decimal getAmount() {
        return amount;
    }

    public void setAmount(Decimal amount) {
        this.amount = amount;
    }

    /**
     * @return true if a {@link Specimen#getAmount()} should be read
     *         individually for each {@link Specimen}, otherwise false if this
     *         {@link #getAmount()} should be used as a default.
     */
    @Transient
    public boolean isAmountInividuallyRead() {
        return getAmount() != null;
    }

    /**
     * @return the {@link SpecimenContainerType} that collected {@link Specimen}
     *         s should be put into, or null if unspecified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_CONTAINER_TYPE_ID")
    public SpecimenContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(SpecimenContainerType containerType) {
        this.containerType = containerType;
    }
}
