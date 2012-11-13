package edu.ualberta.med.biobank.model.security;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.LongIdModel;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@MappedSuperclass
@Unique(properties = "name", groups = PrePersist.class)
public abstract class Role<T extends Permission>
    extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private String name;

    @NotEmpty(message = "{Role.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Set<T> getPermissions();

    public abstract void setPermissions(Set<T> roles);
}
