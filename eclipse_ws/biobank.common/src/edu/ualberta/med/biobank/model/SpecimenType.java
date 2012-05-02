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

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
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
    @NotUsed(by = AliquotedSpecimen.class, property = "specimenType", groups = PreDelete.class)
})
@Empty.List({
    @Empty(property = "childSpecimenTypes", groups = PreDelete.class),
    @Empty(property = "parentSpecimenTypes", groups = PreDelete.class)
})
public class SpecimenType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Specimen Type",
        "Specimen Types");

    private String name;
    private String nameShort;
    private Set<ContainerType> containerTypes = new HashSet<ContainerType>(0);
    private Set<SpecimenType> parentSpecimenTypes =
        new HashSet<SpecimenType>(0);
    private Set<SpecimenType> childSpecimenTypes = new HashSet<SpecimenType>(0);

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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "specimenTypes")
    public Set<ContainerType> getContainerTypes() {
        return this.containerTypes;
    }

    public void setContainerTypes(Set<ContainerType> containerTypes) {
        this.containerTypes = containerTypes;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "childSpecimenTypes")
    public Set<SpecimenType> getParentSpecimenTypes() {
        return this.parentSpecimenTypes;
    }

    public void setParentSpecimenTypes(Set<SpecimenType> parentSpecimenTypes) {
        this.parentSpecimenTypes = parentSpecimenTypes;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SPECIMEN_TYPE_SPECIMEN_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_SPECIMEN_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_SPECIMEN_TYPE_ID", nullable = false, updatable = false) })
    public Set<SpecimenType> getChildSpecimenTypes() {
        return this.childSpecimenTypes;
    }

    public void setChildSpecimenTypes(Set<SpecimenType> childSpecimenTypes) {
        this.childSpecimenTypes = childSpecimenTypes;
    }
}
