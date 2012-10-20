package edu.ualberta.med.biobank.model.study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.center.Center;

/**
 * Allows a {@link Study} to specify {@link Center}s that it works with as well
 * as extra information about the {@link Center}, specific to the {@link Study}
 * that uses it.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "STUDY_CENTER")
public class StudyCenter
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Center center;

    @NotNull(message = "{StudyCenter.study.NotNull}")
    @Column(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull(message = "{StudyCenter.center.NotNull}")
    @Column(name = "CENTER_ID")
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    // TODO: add a list of preferred center-contacts and study-contacts for the
    // relationship
}
