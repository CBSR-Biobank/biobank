package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.type.AttributeValueType;
import edu.ualberta.med.biobank.validator.constraint.UniqueElements;

@MappedSuperclass
public abstract class AttributeType<T extends AttributeOption>
    extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private String label;
    private Boolean required;
    private Set<T> options = new HashSet<T>(0);
    private AttributeValueType valueType;

    @NotNull(message = "{AttributeType.enabled.NotNull}")
    @Column(name = "ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @NotEmpty(message = "{AttributeType.label.NotEmpty}")
    @Column(name = "LABEL")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @UniqueElements(properties = "value")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "ATTRIBUTE_TYPE")
    public Set<T> getOptions() {
        return options;
    }

    public void setOptions(Set<T> options) {
        this.options = options;
    }

    @NotNull(message = "{AttributeType.required.NotNull}")
    @Column(name = "REQUIRED")
    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @NotNull(message = "{AttributeType.valueType.NotNull}")
    @Type(type = "attributeValueType")
    @Column(name = "VALUE_TYPE")
    public AttributeValueType getValueType() {
        return valueType;
    }

    public void setValueType(AttributeValueType valueType) {
        this.valueType = valueType;
    }
}
