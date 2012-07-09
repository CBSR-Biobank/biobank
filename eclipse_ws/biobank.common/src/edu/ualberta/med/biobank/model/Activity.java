package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ACTIVITY")
public class Activity extends AbstractModel {
    private static final long serialVersionUID = 1L;
}
