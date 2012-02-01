package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

// TODO: make enum?
@Entity
@Table(name = "EVENT_ATTR_TYPE")
public class EventAttrType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;

    // TODO: unique
    @NotEmpty
    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
