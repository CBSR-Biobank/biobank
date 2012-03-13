package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@Table(name = "CONTAINER_TYPE_CONTAINER_TYPE")
@NotUsed(by = ContainerPosition.class, property = "containerTypeContainerType", groups = PreDelete.class)
public class ContainerTypeContainerType
    extends AbstractParentChildRelation<ContainerType, ContainerType> {
}
