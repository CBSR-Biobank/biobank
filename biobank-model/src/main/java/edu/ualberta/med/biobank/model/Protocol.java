package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "PROTOCOL")
@Unique(properties = "name", groups = PrePersist.class)
public class Protocol extends AbstractVersionedModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    // TODO: limit one specimen type per specimen per protocol?

    private String name;
    private String description;

    @Override
    @Column(name = "NAME", unique = true)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
