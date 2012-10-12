package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * caTissue Term - Aliquot: Pertaining to a portion of the whole; any one of two
 * or more samples of something, of the same volume or weight.
 * 
 * NCI Term - Specimen: A part of a thing, or of several things, taken to
 * demonstrate or to determine the character of the whole, e.g. a substance, or
 * portion of material obtained for use in testing, examination, or study;
 * particularly, a preparation of tissue or bodily fluid taken for examination
 * or diagnosis.
 */
@Audited
@Entity
@Table(name = "SPECIMEN", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "CONTAINER_ID",
        "CONTAINER_SCHEMA_POSITION_ID"
    })
})
@Unique(properties = "inventoryId", groups = PrePersist.class)
@NotUsed.List({
    @NotUsed(by = ShipmentSpecimen.class, property = "specimen", groups = PreDelete.class),
    @NotUsed(by = RequestSpecimen.class, property = "specimen", groups = PreDelete.class)
})
public class Specimen
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private SpecimenContainer container;
    private ContainerSchemaPosition position;
    private Date timeCreated;
    private SpecimenGroup group;
    private Decimal amount;
    private CenterLocation originLocation;
    private CenterLocation location;
    private Boolean usable; // TODO: rename to 'exists' ?

    /**
     * @return the {@link SpecimenContainer} this {@link Specimen} is stored in,
     *         which must exist and cannot be null.
     */
    @NotNull(message = "{Specimen.container.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_ID", nullable = false)
    public SpecimenContainer getContainer() {
        return container;
    }

    public void setContainer(SpecimenContainer container) {
        this.container = container;
    }

    /**
     * @return the {@link ContainerSchemaPosition} (i.e. the position or label)
     *         this {@link Specimen} has in its {@link #container}.
     */
    @NotNull(message = "{Specimen.position.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_SCHEMA_POSITION_ID", nullable = false)
    public ContainerSchemaPosition getPosition() {
        return position;
    }

    public void setPosition(ContainerSchemaPosition position) {
        this.position = position;
    }

    @Valid
    @NotNull(message = "{Specimen.amount.NotNull}")
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

    @NotNull(message = "{Specimen.timeCreated.NotNull}")
    @Column(name = "TIME_CREATED")
    public Date getTimeCreated() {
        return this.timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_GROUP_ID", nullable = false)
    public SpecimenGroup getGroup() {
        return group;
    }

    public void setGroup(SpecimenGroup group) {
        this.group = group;
    }

    /**
     * @return this {@link Specimen}'s current location, or null if no location.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_LOCATION_ID")
    public CenterLocation getLocation() {
        return this.location;
    }

    public void setLocation(CenterLocation location) {
        this.location = location;
    }

    @NotNull(message = "{Specimen.originLocation.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGIN_CENTER_LOCATION_ID", nullable = false)
    public CenterLocation getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(CenterLocation originLocation) {
        this.originLocation = originLocation;
    }

    @NotNull(message = "{Specimen.usable.NotNull}")
    @Column(name = "IS_USABLE", nullable = false)
    public Boolean isUsable() {
        return usable;
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
    }
}
