package edu.ualberta.med.biobank.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;

import edu.ualberta.med.biobank.model.type.Decimal;
import edu.ualberta.med.biobank.model.type.Temperature;
import edu.ualberta.med.biobank.model.util.HashCodeBuilderProvider;

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
    private static final HashCodeBuilderProvider hashCodeBuilderProvider =
        new HashCodeBuilderProvider(Decimal.class, 29, 31);

    private Temperature temp;
    private PreservationType type;

    /**
     * @return the temperature, in degrees centigrade, that the associated
     *         object must be stored at, or null for no specific requirements
     */
    @Valid
    @Embedded
    public Temperature getStorageTemperature() {
        return temp;
    }

    public void setStorageTemperature(Temperature storageTemperature) {
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
        return hashCodeBuilderProvider.get()
            .append(temp)
            .append(type)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Preservation that = (Preservation) obj;
        return new EqualsBuilder()
            .append(getStorageTemperature(), that.getStorageTemperature())
            .append(getType(), that.getType())
            .isEquals();
    }
}
