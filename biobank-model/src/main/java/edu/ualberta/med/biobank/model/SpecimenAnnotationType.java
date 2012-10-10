package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Audited
@Entity
@DiscriminatorValue("SP")
@NotUsed.List({
    @NotUsed(by = SpecimenAnnotation.class, property = "type", groups = PreDelete.class)
})
public class SpecimenAnnotationType
    extends AbstractAnnotationType {
    private static final long serialVersionUID = 1L;

    // private Set<SpecimenGroup> groups;
    //
    // /**
    // * @return the {@link SpecimenGroup}s that this
    // * {@link SpecimenAnnotationType} is meant to be collected for.
    // */
    // @OnDelete(action = OnDeleteAction.CASCADE)
    // public Set<SpecimenGroup> getGroups() {
    // return groups;
    // }
    //
    // public void setGroups(Set<SpecimenGroup> groups) {
    // this.groups = groups;
    // }
}