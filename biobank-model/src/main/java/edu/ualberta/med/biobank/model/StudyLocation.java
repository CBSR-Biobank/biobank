package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * Allows a {@link Study} to specify {@link Location}s that it works with as
 * well as extra information about the {@link Location}, specific to the
 * {@link Study} that uses it.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "STUDY_LOCATION")
public class StudyLocation extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Location location;

    @NotNull(message = "{StudyLocation.study.NotNull}")
    @Column(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull(message = "{StudyLocation.location.NotNull}")
    @Column(name = "LOCATION_ID")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
