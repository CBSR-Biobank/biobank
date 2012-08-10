package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@MappedSuperclass
@Unique(properties = "name", groups = PrePersist.class)
public abstract class AbstractUniquelyNamedModel
    extends AbstractVersionedModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    /**
     * @return a <em>unique</em> name, across all persisted instances of the
     *         descendant type.
     */
    @Override
    @NotEmpty(message = "{AbstractUniquelyNamedModel.name.NotEmpty}")
    @Size(max = 100, message = "{AbstractUniquelyNamedModel.name.Size}")
    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Size(max = 5000, message = "{AbstractUniquelyNamedModel.description.Size}")
    @Column(name = "DESCRIPTION", columnDefinition = "TEXT", length = 5000)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

}
