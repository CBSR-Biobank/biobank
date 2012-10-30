package edu.ualberta.med.biobank.model.security;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@DiscriminatorValue("GRUP")
@Unique(properties = "name", groups = PrePersist.class)
public class Group extends Principal
    implements HasName {
    private static final long serialVersionUID = 1L;

    private String name;

    @Override
    @NotEmpty(message = "{Group.name.NotEmpty}")
    @Column(name = "NAME", nullable = false, unique = true)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
