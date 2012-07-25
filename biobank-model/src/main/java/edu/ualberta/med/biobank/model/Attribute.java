package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
// TODO: custom validator to check the type against the value
public abstract class Attribute<T extends AttributeType<U>, U extends AttributeOption>
    extends AbstractModel
    implements HasCreatedAt {
    private static final long serialVersionUID = 1L;

    private T attributeType;
    private Date createdAt;
    private String value;
    private Set<U> options = new HashSet<U>(0);

    @NotNull(message = "{edu.ualberta.med.biobank.model.Attribute.attributeType.NotNull")
    @Column(name = "ATTRIBUTE_TYPE")
    public T getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(T attributeType) {
        this.attributeType = attributeType;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Attribute.createdAt.NotNull")
    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // TODO: add a custom FK from ATTRIBUTE_ATTRIBUTE_OPTION to
    // ATTRIBUTE_OPTION;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = { @JoinColumn(name = "ATTRIBUTE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "ATTRIBUTE_OPTION_ID", unique = true, nullable = false, updatable = false) })
    public Set<U> getOptions() {
        return options;
    }

    public void setOptions(Set<U> options) {
        this.options = options;
    }
}
