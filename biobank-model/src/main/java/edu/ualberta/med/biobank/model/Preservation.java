package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * Describes how a {@link Specimen} should be preserved/stored by describing
 * temperature requirements, as well as a preservation method (see
 * {@link PreservationType}).
 * 
 * @author Jonathan Ferland
 */
@Embeddable
public class Preservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal temp;
    private PreservationType type;

    /**
     * @return the temperature, in degrees centigrade, that the associated
     *         object must be stored at, or null for no specific requirements
     */
    @Digits(integer = 4, fraction = 2, message = "{Preservation.storageTemperature.Digits}")
    @DecimalMin(value = "-273.15", message = "{Preservation.storageTemperature.DecimalMin}")
    @Column(name = "STORAGE_TEMPERATURE", nullable = false, precision = 4, scale = 2)
    public BigDecimal getStorageTemperature() {
        return temp;
    }

    public void setStorageTemperature(BigDecimal storageTemperature) {
        this.temp = storageTemperature;
    }

    @NotNull(message = "{Preservation.type.NotNull}")
    @Column(name = "PRESERVATION_TYPE_ID", nullable = false)
    public PreservationType getType() {
        return type;
    }

    public void setType(PreservationType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((temp == null) ? 0 : temp.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Preservation other = (Preservation) obj;
        if (temp == null) {
            if (other.temp != null) return false;
        } else if (!temp.equals(other.temp))
            return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }
}
