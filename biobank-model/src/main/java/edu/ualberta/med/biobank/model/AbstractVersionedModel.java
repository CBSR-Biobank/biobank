package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractVersionedModel
    extends AbstractModel
    implements HasTimeUpdated, HasUpdatedBy {
    private static final long serialVersionUID = 1L;

    private Integer version;
    private Date timeUpdated;
    private User updatedBy;

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
    @Column(name = "TIME_UPDATED", nullable = false)
    public Date getTimeUpdated() {
        return timeUpdated;
    }

    @Override
    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPDATED_BY_USER_ID", nullable = false)
    public User getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}
