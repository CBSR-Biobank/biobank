package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Represents an amount of something coupled with a unit of measurement. The
 * {@link #getScale()} property represents the number of digits after the
 * decimal point (see {@link BigDecimal#getScale}), except that only
 * non-negative scale values are allowed to be persisted.
 * 
 * @author Jonathan Ferland
 */
@Embeddable
public class Amount implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal value;
    private Integer scale;
    private Unit unit;

    @NotNull(message = "{Amount.value.NotNull}")
    @Digits(integer = 10, fraction = 10, message = "{Amount.value.Digits}")
    @Column(name = "AMOUNT", nullable = false, precision = 10, scale = 10)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
        scaleValue();
    }

    /**
     * @return the number of digits recorded after the decimal place.
     */
    @NotNull(message = "{Amount.scale.NotNull}")
    @Min(value = 0, message = "{Amount.scale.Min}")
    @Column(name = "SCALE", nullable = false)
    public Integer getScale() {
        return (value != null) ? value.scale() : scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
        scaleValue();
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
        result = prime * result + ((scale == null) ? 0 : scale.hashCode());
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
        if (scale == null) {
            if (other.scale != null) return false;
        } else if (!scale.equals(other.scale)) return false;
        if (unit == null) {
            if (other.unit != null) return false;
        } else if (!unit.equals(other.unit)) return false;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }

    private void scaleValue() {
        if (value != null && scale != null) {
            value = value.setScale(scale, RoundingMode.HALF_EVEN);
        }
    }
}
