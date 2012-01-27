package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PERMISSION")
public class Permission extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String className;

    @Column(name = "CLASS_NAME", unique = true, nullable = false)
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
