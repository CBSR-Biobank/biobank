package edu.ualberta.med.biobank.model.center;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.HasDescription;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents an organisation with a physical location that stores, processes,
 * or collects {@link Specimen}s.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "CENTER")
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = ProcessingEvent.class, property = "center", groups = PreDelete.class),
    @NotUsed(by = Container.class, property = "center", groups = PreDelete.class),
    @NotUsed(by = ContainerType.class, property = "center", groups = PreDelete.class)
})
public class Center
    extends VersionedLongIdModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    private String name;
    private String description;
    private Boolean enabled;

    @Override
    @NotNull(message = "{Center.description.NotNull}")
    @Column(name = "DESCRIPTION", nullable = false, length = MAX_NAME_LENGTH)
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @NotEmpty(message = "{Center.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false, length = MAX_NAME_LENGTH)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "{Center.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
