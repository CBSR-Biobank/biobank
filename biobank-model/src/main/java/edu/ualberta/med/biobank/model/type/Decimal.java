package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;

import edu.ualberta.med.biobank.model.util.HashCodeBuilderProvider;
import edu.ualberta.med.biobank.model.util.ProxyUtil;

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
    private static final HashCodeBuilderProvider hashCodeBuilderProvider =
        new HashCodeBuilderProvider(Decimal.class, 11, 17);

    private BigDecimal value;
    private Integer scale;

    @NotNull(message = "{Decimal.value.NotNull}")
    @Digits(integer = 10, fraction = 10, message = "{Decimal.value.Digits}")
    @Column(name = "DECIMAL_VALUE", nullable = false, precision = 10, scale = 10)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
        update();
    }

    /**
     * @return the number of digits recorded after the decimal place.
     */
    @NotNull(message = "{Decimal.scale.NotNull}")
    @Min(value = 0, message = "{Decimal.scale.Min}")
    @Column(name = "DECIMAL_SCALE", nullable = false)
    Integer getScale() {
        return (value != null) ? value.scale() : scale;
    }

    void setScale(Integer scale) {
        this.scale = scale;
        update();
    }

    @Override
    public int hashCode() {
        return hashCodeBuilderProvider.get()
            .append(value)
            .append(scale)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!ProxyUtil.sameClass(this, obj)) return false;
        Decimal that = (Decimal) obj;
        return new EqualsBuilder()
            .append(getValue(), that.getValue())
            .append(getScale(), that.getScale())
            .isEquals();
    }

    private void update() {
        if (value != null && scale != null) {
            value = value.setScale(scale, RoundingMode.HALF_EVEN);
        }
    }
}
