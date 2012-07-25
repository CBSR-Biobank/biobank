package edu.ualberta.med.biobank.model;

import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.NotEmpty;

@MappedSuperclass
public class AttributeOption extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String value;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.AttributeOption.value.NotEmpty}")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
