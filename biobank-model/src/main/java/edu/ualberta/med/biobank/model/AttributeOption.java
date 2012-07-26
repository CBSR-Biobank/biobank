package edu.ualberta.med.biobank.model;

import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.NotEmpty;

@MappedSuperclass
public class AttributeOption extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private String value;

    @NotEmpty(message = "{AttributeOption.value.NotEmpty}")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
