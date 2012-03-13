package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CONTAINER_TYPE_SPECIMEN_TYPE")
// @NotUsed(by = ContainerPosition.class, property =
// "containerTypeSpecimenType", groups = PreDelete.class)
public class ContainerTypeSpecimenType
    extends AbstractParentChildRelation<ContainerType, SpecimenType> {
}
