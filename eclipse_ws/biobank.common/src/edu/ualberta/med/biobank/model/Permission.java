package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;

public class Permission extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    public String className;

    @NotEmpty
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
