package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import edu.ualberta.med.biobank.model.constraint.HasValidTimeUpdated;

@MappedSuperclass
public abstract class AbstractVersionedModel
    extends AbstractModel
    implements HasValidTimeUpdated {
    private static final long serialVersionUID = 1L;

    private Integer version;
    private Date timeUpdated;

    @Version
    @Column(name = "VERSION", nullable = false)
    public Integer getVersion() {
        return this.version;
    }

    /**
     * DO NOT CALL this method unless, maybe, for tests. Hibernate manages
     * setting this value.
     * 
     * @param version
     */
    void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Date getTimeUpdated() {
        return timeUpdated;
    }

    @Override
    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

}
