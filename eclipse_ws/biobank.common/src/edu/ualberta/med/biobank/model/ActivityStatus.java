package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;

// TODO: should be an enum
@Entity
@Table(name = "ACTIVITY_STATUS")
public class ActivityStatus extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;

    @NotEmpty
    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
