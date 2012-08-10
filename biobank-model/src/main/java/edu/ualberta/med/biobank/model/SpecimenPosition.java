package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "SPECIMEN_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CONTAINER_ID", "ROW", "COL" }) })
@Unique(properties = { "container", "row", "col" }, groups = PrePersist.class)
public class SpecimenPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container container;
    private Specimen specimen;
    private String positionString;

    @NotNull(message = "{SpecimenPosition.container.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_SpecimenPosition_container")
    @JoinColumn(name = "CONTAINER_ID", nullable = false)
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * Read-only property (the corresponding setter does nothing) to get data
     * for a foreign key constraint to the container, ensuring that as long as
     * this {@link SpecimenPosition} exists, the {@link Container} has the same
     * {@link ContainerType}.
     * 
     * @return
     */
    @ManyToOne
    @ForeignKey(name = "none")
    @JoinColumn(name = "CONTAINER_TYPE_ID", nullable = false)
    ContainerType getContainerType() {
        return getContainer() != null
            ? getContainer().getContainerType()
            : null;
    }

    void setContainerType(ContainerType containerType) {
    }

    @NotNull(message = "{SpecimenPosition.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @ForeignKey(name = "none")
    @JoinColumn(name = "SPECIMEN_ID", nullable = false, unique = true)
    public Specimen getSpecimen() {
        return this.specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NotNull
    @Column(name = "POSITION_STRING", length = 255, nullable = false)
    public String getPositionString() {
        return this.positionString;
    }

    public void setPositionString(String positionString) {
        this.positionString = positionString;
    }

    /**
     * Read-only property (the corresponding setter does nothing) to get data
     * for a foreign key constraint to the container, ensuring that as long as
     * this {@link SpecimenPosition} exists, the {@link Specimen} has the same
     * {@link SpecimenType}.
     * 
     * @return
     */
    @ManyToOne
    @ForeignKey(name = "none")
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    SpecimenType getSpecimenType() {
        return getSpecimen() != null
            ? getSpecimen().getSpecimenType()
            : null;
    }

    void setSpecimenType(SpecimenType specimenType) {
    }

    @Override
    @Transient
    public Container getHoldingContainer() {
        return getContainer();
    }
}
