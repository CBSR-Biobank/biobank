package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;

@Entity
@Table(name = "SPECIMEN_TYPE")
public class SpecimenType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Collection<ContainerType> containerTypeCollection =
        new HashSet<ContainerType>(0);
    private Collection<SpecimenType> parentSpecimenTypeCollection =
        new HashSet<SpecimenType>(0);
    private Collection<SpecimenType> childSpecimenTypeCollection =
        new HashSet<SpecimenType>(0);

    @NotEmpty
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    @Column(name = "NAME_SHORT")
    public String getNameShort() {
        return this.nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "specimenTypeCollection")
    public Collection<ContainerType> getContainerTypeCollection() {
        return this.containerTypeCollection;
    }

    public void setContainerTypeCollection(
        Collection<ContainerType> containerTypeCollection) {
        this.containerTypeCollection = containerTypeCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "childSpecimenTypeCollection")
    public Collection<SpecimenType> getParentSpecimenTypeCollection() {
        return this.parentSpecimenTypeCollection;
    }

    public void setParentSpecimenTypeCollection(
        Collection<SpecimenType> parentSpecimenTypeCollection) {
        this.parentSpecimenTypeCollection = parentSpecimenTypeCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SPECIMEN_TYPE_SPECIMEN_TYPE",
        joinColumns = { @JoinColumn(name = "PARENT_SPECIMEN_TYPE_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CHILD_SPECIMEN_TYPE_ID", nullable = false, updatable = false) })
    public Collection<SpecimenType> getChildSpecimenTypeCollection() {
        return this.childSpecimenTypeCollection;
    }

    public void setChildSpecimenTypeCollection(
        Collection<SpecimenType> childSpecimenTypeCollection) {
        this.childSpecimenTypeCollection = childSpecimenTypeCollection;
    }
}
