package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Optional information for a {@link Container} to reference that restricts the
 * types of {@link AnatomicalSource}s, {@link SpecimenType}s, and
 * {@link PreservationType}s. An empty set means that any of that type is
 * allowed in the associated {@link Container} and its children, but a non-empty
 * set means only the specified types are allowed.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "CONTAINER_CONSTRAINTS")
public class ContainerConstraints
    extends AbstractVersionedModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;

    private Center center;
    private String name;
    private String description;
    private Set<AnatomicalSource> anatomicalSources =
        new HashSet<AnatomicalSource>(0);
    private Set<SpecimenType> specimenTypes =
        new HashSet<SpecimenType>(0);
    private Set<PreservationType> preservationTypes =
        new HashSet<PreservationType>(0);

    @NotNull(message = "{ContainerConstraints.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @Override
    @NotEmpty(message = "{ContainerConstraints.name.NotEmpty}")
    @Size(max = 50, message = "{ContainerConstraints.name.Size}")
    @Column(name = "NAME", length = 50, nullable = false)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Size(max = 5000, message = "{ContainerConstraints.description.Size}")
    @Column(name = "DESCRIPTION", length = 5000)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the {@link AnatomicalSource}s that an associated
     *         {@link Container} and it's children are allowed to hold, or an
     *         empty set if no restrictions.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_CONSTRAINTS_ANATOMICAL_SOURCE",
        joinColumns = { @JoinColumn(name = "CONTAINER_CONSTRAINTS_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "ANATOMICAL_SOURCE_ID", nullable = false, updatable = false) })
    public Set<AnatomicalSource> getAnatomicalSources() {
        return anatomicalSources;
    }

    public void setAnatomicalSources(Set<AnatomicalSource> anatomicalSources) {
        this.anatomicalSources = anatomicalSources;
    }

    /**
     * @return the {@link SpecimenType}s that an associated {@link Container}
     *         and it's children are allowed to hold, or an empty set if no
     *         restrictions.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_CONSTRAINTS_SPECIMEN_TYPE",
        joinColumns = { @JoinColumn(name = "CONTAINER_CONSTRAINTS_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false, updatable = false) })
    public Set<SpecimenType> getSpecimenTypes() {
        return specimenTypes;
    }

    public void setSpecimenTypes(Set<SpecimenType> specimenTypes) {
        this.specimenTypes = specimenTypes;
    }

    /**
     * @return the {@link PreservationType}s that an associated
     *         {@link Container} and it's children are allowed to hold, or an
     *         empty set if no restrictions.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CONTAINER_CONSTRAINTS_PRESERVATION_TYPE",
        joinColumns = { @JoinColumn(name = "CONTAINER_CONSTRAINTS_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "PRESERVATION_TYPE_ID", nullable = false, updatable = false) })
    public Set<PreservationType> getPreservationTypes() {
        return preservationTypes;
    }

    public void setPreservationTypes(Set<PreservationType> preservationTypes) {
        this.preservationTypes = preservationTypes;
    }
}
