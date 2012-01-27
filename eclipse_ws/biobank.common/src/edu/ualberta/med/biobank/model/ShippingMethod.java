package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;

public class ShippingMethod extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
