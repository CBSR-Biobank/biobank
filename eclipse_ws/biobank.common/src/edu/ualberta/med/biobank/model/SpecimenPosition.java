package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SPECIMEN_POSITION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CONTAINER_ID", "ROW", "COL" }) })
public class SpecimenPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container container;
    private Specimen specimen;
    private String positionString;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_ID", nullable = false)
    public Container getContainer() {
        return this.container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
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
}
