package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "PROPERTY_TYPE")
public class PropertyType extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Set<PropertyModifier> propertyModifiers =
        new HashSet<PropertyModifier>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.PropertyType.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROPERTY_TYPE_ID", updatable = false)
    public Set<PropertyModifier> getPropertyModifiers() {
        return this.propertyModifiers;
    }

    public void setPropertyModifiers(Set<PropertyModifier> propertyModifiers) {
        this.propertyModifiers = propertyModifiers;
    }
}
