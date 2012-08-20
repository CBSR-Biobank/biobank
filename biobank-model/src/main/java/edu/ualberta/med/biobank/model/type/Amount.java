package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Unit;

/**
 * Represents an amount of something coupled with a unit of measurement.
 * 
 * @author Jonathan Ferland
 * @see Decimal
 */
@Embeddable
public class Amount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Decimal value;
    private Unit unit;

    @NotNull(message = "{Amount.value.NotNull}")
    @Valid
    @Embedded
    public Decimal getValue() {
        return value;
    }

    public void setValue(Decimal value) {
        this.value = value;
    }

    /**
     * @return the {@link Unit}, or null if the {@link #getValue()} is
     *         unit-less, such as, a number or a count.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UNIT_ID", nullable = false)
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Amount other = (Amount) obj;
        if (unit == null) {
            if (other.unit != null) return false;
        } else if (!unit.equals(other.unit)) return false;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }
}
