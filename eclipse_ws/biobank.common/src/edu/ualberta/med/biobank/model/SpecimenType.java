package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

public class SpecimenType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Collection<ContainerType> containerTypeCollection =
        new HashSet<ContainerType>();
    private Collection<SpecimenType> parentSpecimenTypeCollection =
        new HashSet<SpecimenType>();
    private Collection<SpecimenType> childSpecimenTypeCollection =
        new HashSet<SpecimenType>();

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public Collection<ContainerType> getContainerTypeCollection() {
        return containerTypeCollection;
    }

    public void setContainerTypeCollection(
        Collection<ContainerType> containerTypeCollection) {
        this.containerTypeCollection = containerTypeCollection;
    }

    public Collection<SpecimenType> getParentSpecimenTypeCollection() {
        return parentSpecimenTypeCollection;
    }

    public void setParentSpecimenTypeCollection(
        Collection<SpecimenType> parentSpecimenTypeCollection) {
        this.parentSpecimenTypeCollection = parentSpecimenTypeCollection;
    }

    public Collection<SpecimenType> getChildSpecimenTypeCollection() {
        return childSpecimenTypeCollection;
    }

    public void setChildSpecimenTypeCollection(
        Collection<SpecimenType> childSpecimenTypeCollection) {
        this.childSpecimenTypeCollection = childSpecimenTypeCollection;
    }
}
