package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import org.apache.commons.lang.builder.EqualsBuilder;

import edu.ualberta.med.biobank.model.util.HashCodeBuilderProvider;

@Embeddable
public class Temperature
    implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final HashCodeBuilderProvider hashCodeBuilderProvider =
        new HashCodeBuilderProvider(Decimal.class, 19, 23);

    private static final int MAX_DIGITS = 5;
    private static final int DIGITS_AFTER_DECIMAL = 2;

    private BigDecimal temp;

    /**
     * @return the temperature, in degrees centigrade, that the associated
     *         object must be stored at, or null for no specific requirements
     */
    @Digits(integer = MAX_DIGITS, fraction = DIGITS_AFTER_DECIMAL, message = "{Preservation.storageTemperature.Digits}")
    @DecimalMin(value = "-273.15", message = "{Preservation.storageTemperature.DecimalMin}")
    @Column(name = "TEMPERATURE", nullable = false, precision = MAX_DIGITS, scale = DIGITS_AFTER_DECIMAL)
    public BigDecimal getTemperature() {
        return temp;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temp = temperature;
    }

    @Override
    public int hashCode() {
        return hashCodeBuilderProvider.get()
            .append(temp)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Temperature that = (Temperature) obj;
        return new EqualsBuilder()
            .append(getTemperature(), that.getTemperature())
            .isEquals();
    }
}
