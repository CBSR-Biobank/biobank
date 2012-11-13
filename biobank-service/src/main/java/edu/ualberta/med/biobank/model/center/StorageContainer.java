package edu.ualberta.med.biobank.model.center;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.study.Specimen;

/**
 * Represents a single physical {@link Container} that directly holds one or
 * more other {@link Container}s, typically for the purpose of storage.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@DiscriminatorValue("ST")
public class StorageContainer
    extends Container<StorageContainerType> {
    private static final long serialVersionUID = 1L;

    private ContainerConstraints constraints;
    private Boolean enabled;

    /**
     * @return optional information about what types of {@link Specimen}s this
     *         {@link Container} and its children can legally contain, or null
     *         if none specified.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_CONSTRAINTS_ID")
    public ContainerConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(ContainerConstraints constraints) {
        this.constraints = constraints;
    }

    /**
     * @return true if this {@link Container} can have new {@link Specimen}s
     *         added to it, otherwise false.
     */
    @NotNull(message = "{Container.enabled.NotNull}")
    @Column(name = "IS_ENABLED")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
