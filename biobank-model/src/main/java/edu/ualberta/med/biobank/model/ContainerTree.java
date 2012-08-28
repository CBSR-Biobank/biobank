package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Decimal;

@Audited
@Entity
@Table(name = "CONTAINER_TREE")
public class ContainerTree
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Center center;
    private Center owner;
    private Decimal temperature;
}
