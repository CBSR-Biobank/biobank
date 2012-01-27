package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;

// TODO: should be an Enum
@Entity
@Table(name = "PERMISSION")
public class Permission extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String className;

    @NotEmpty
    @Column(name = "CLASS_NAME", unique = true, nullable = false)
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
