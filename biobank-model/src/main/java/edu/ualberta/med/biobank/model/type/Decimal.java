package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
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
public class Decimal
    implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final HashCodeBuilderProvider hashCodeBuilderProvider =
        new HashCodeBuilderProvider(Decimal.class, 11, 17);

    public static final int TOTAL_DIGITS = 27;
    public static final int DIGITS_AFTER_DECIMAL = 9;

    public Decimal() {
    }

    public Decimal(BigDecimal bigDecimal) {
        this.value = bigDecimal;
        this.scale = bigDecimal.scale();
    }

    private BigDecimal value;
    private Integer scale;

    @NotNull(message = "{Decimal.value.NotNull}")
    @Digits(integer = TOTAL_DIGITS, fraction = DIGITS_AFTER_DECIMAL, message = "{Decimal.value.Digits}")
    @Column(name = "DECIMAL_VALUE", nullable = false, precision = TOTAL_DIGITS, scale = DIGITS_AFTER_DECIMAL)
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
    @Max(value = DIGITS_AFTER_DECIMAL, message = "{Decimal.scale.Max}")
    @Column(name = "DECIMAL_SCALE", nullable = false)
    Integer getScale() {
        return scale;
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
