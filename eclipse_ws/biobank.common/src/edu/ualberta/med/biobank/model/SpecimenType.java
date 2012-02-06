package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "SPECIMEN_TYPE")
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class),
    @Unique(properties = "nameShort", groups = PrePersist.class)
})
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "specimenType", groups = PreDelete.class),
    @NotUsed(by = SourceSpecimen.class, property = "specimenType", groups = PreDelete.class),
    @NotUsed(by = AliquotedSpecimen.class, property = "specimenType", groups = PreDelete.class),
    @NotUsed(by = SpecimenType.class, property = "parentSpecimenTypeCollection", groups = PreDelete.class),
    @NotUsed(by = SpecimenType.class, property = "childSpecimenTypeCollection", groups = PreDelete.class)
})
public class SpecimenType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Set<ContainerType> containerTypeCollection =
        new HashSet<ContainerType>(0);
    private Set<SpecimenType> parentSpecimenTypeCollection =
        new HashSet<SpecimenType>(0);
    private Set<SpecimenType> childSpecimenTypeCollection =
        new HashSet<SpecimenType>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.SpecimenType.name.NotEmpty}")
    @Column(name = "NAME", unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.SpecimenType.nameShort.NotEmpty}")
    @Column(name = "NAME_SHORT", unique = true)
    public String getNameShort() {
        return this.nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "specimenTypeCollection")
    public Set<ContainerType> getContainerTypeCollection() {
        return this.containerTypeCollection;
    }

    public void setContainerTypeCollection(
        Set<ContainerType> containerTypeCollection) {
        this.containerTypeCollection = containerTypeCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "childSpecimenTypeCollection")
    public Set<SpecimenType> getParentSpecimenTypeCollection() {
        return this.parentSpecimenTypeCollection;
    }

    public void setParentSpecimenTypeCollection(
        Set<SpecimenType> parentSpecimenTypeCollection) {
        this.parentSpecimenTypeCollection = parentSpecimenTypeCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SPECIMEN_TYPE_SPECIMEN_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_SPECIMEN_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_SPECIMEN_TYPE_ID", nullable = false, updatable = false) })
    public Set<SpecimenType> getChildSpecimenTypeCollection() {
        return this.childSpecimenTypeCollection;
    }

    public void setChildSpecimenTypeCollection(
        Set<SpecimenType> childSpecimenTypeCollection) {
        this.childSpecimenTypeCollection = childSpecimenTypeCollection;
    }
}
