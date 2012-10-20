package edu.ualberta.med.biobank.model.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.LongIdModel;

@Entity
@Table(name = "PROPERTY_MODIFIER")
public class PropertyModifier extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String propertyModifier;

    @NotEmpty(message = "{PropertyModifier.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PROPERTY_MODIFIER", columnDefinition = "TEXT")
    public String getPropertyModifier() {
        return this.propertyModifier;
    }

    public void setPropertyModifier(String propertyModifier) {
        this.propertyModifier = propertyModifier;
    }
}
