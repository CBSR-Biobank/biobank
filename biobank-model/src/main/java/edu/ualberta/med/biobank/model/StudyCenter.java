package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Audited
@Entity
public class StudyCenter extends AbstractModel {
    private static final long serialVersionUID = 1L;

}
