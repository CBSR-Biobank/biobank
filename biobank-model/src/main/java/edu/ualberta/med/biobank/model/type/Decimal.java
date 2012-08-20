package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Allows a {@link BigDecimal} to be saved along with its scale. The
 * {@link #getScale()} property represents the number of digits after the
 * decimal point (see {@link BigDecimal#getScale()}), except that this class
 * only allows non-negative scale values to be persisted.
 * 
 * @author Jonathan Ferland
 */
@Embeddable
public class Decimal implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal value;
    private Integer scale;

    @NotNull(message = "{Decimal.value.NotNull}")
    @Digits(integer = 10, fraction = 10, message = "{Decimal.value.Digits}")
    @Column(name = "VALUE", nullable = false, precision = 10, scale = 10)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal bigDecimal) {
        this.value = bigDecimal;
        update();
    }

    /**
     * @return the number of digits recorded after the decimal place.
     */
    @NotNull(message = "{Decimal.scale.NotNull}")
    @Min(value = 0, message = "{Decimal.scale.Min}")
    @Column(name = "SCALE", nullable = false)
    Integer getScale() {
        return (value != null) ? value.scale() : scale;
    }

    void setScale(Integer scale) {
        this.scale = scale;
        update();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((scale == null) ? 0 : scale.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Decimal other = (Decimal) obj;
        if (scale == null) {
            if (other.scale != null) return false;
        } else if (!scale.equals(other.scale)) return false;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }

    private void update() {
        if (value != null && scale != null) {
            value = value.setScale(scale, RoundingMode.HALF_EVEN);
        }
    }
}
