package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Embeddable
public class DecimalRange implements Serializable {
    private static final long serialVersionUID = 1L;

    private Decimal min;
    private Boolean minInclusive;
    private Decimal max;
    private Boolean maxInclusive;

    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "MIN_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "MIN_VALUE_SCALE"))
    })
    public Decimal getMinValue() {
        return min;
    }

    public void setMinValue(Decimal minValue) {
        this.min = minValue;
    }

    @NotNull(message = "{DecimalRange.minInclusive.NotNull}")
    @Column(name = "IS_MIN_INCLUSIVE", nullable = false)
    public Boolean isMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(Boolean minInclusive) {
        this.minInclusive = minInclusive;
    }

    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "MAX_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "MAX_VALUE_SCALE"))
    })
    public Decimal getMaxValue() {
        return max;
    }

    public void setMaxValue(Decimal maxValue) {
        this.max = maxValue;
    }

    @NotNull(message = "{DecimalRange.maxInclusive.NotNull}")
    @Column(name = "IS_MAX_INCLUSIVE", nullable = false)
    public Boolean getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(Boolean maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result
            + ((maxInclusive == null) ? 0 : maxInclusive.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        result = prime * result
            + ((minInclusive == null) ? 0 : minInclusive.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DecimalRange other = (DecimalRange) obj;
        if (max == null) {
            if (other.max != null) return false;
        } else if (!max.equals(other.max)) return false;
        if (maxInclusive == null) {
            if (other.maxInclusive != null) return false;
        } else if (!maxInclusive.equals(other.maxInclusive)) return false;
        if (min == null) {
            if (other.min != null) return false;
        } else if (!min.equals(other.min)) return false;
        if (minInclusive == null) {
            if (other.minInclusive != null) return false;
        } else if (!minInclusive.equals(other.minInclusive)) return false;
        return true;
    }

}
