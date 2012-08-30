package edu.ualberta.med.biobank.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;

@Audited
@Entity
@Table(name = "CONTAINER_TREE")
public class ContainerTree
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Center center;
    private Center owner;
    private Decimal temperature;

    @Valid
    @NotNull(message = "{ContainerTree.temperature.NotNull}")
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "TEMPERATURE_VALUE")),
        @AttributeOverride(name = "scale", column = @Column(name = "TEMPERATURE_SCALE"))
    })
    public Decimal getTemperature() {
        return temperature;
    }

    public void setTemperature(Decimal temperature) {
        this.temperature = temperature;
    }
}
