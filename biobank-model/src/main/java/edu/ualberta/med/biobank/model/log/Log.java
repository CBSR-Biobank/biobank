package edu.ualberta.med.biobank.model.log;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.AbstractModel;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

@Entity
@Table(name = "LOG")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
// TODO: just add a log listener that, after inserting, adds it to the revision?
public abstract class Log<T extends Enum<T> & ILogType>
    extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private Long createdAt = System.currentTimeMillis();
    private Integer studyId;
    private Integer centerId;
    private T logType;

    protected Log() {
    }

    protected Log(T logType) {
        this.logType = logType;
    }

    @NotNull(message = "{Log.user.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotNull(message = "{Log.createdAt.NotNull}")
    @Column(name = "CREATED_AT")
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "STUDY_ID")
    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    public void setStudyId(Study study) {
        this.studyId = (study != null) ? study.getId() : null;
    }

    @Column(name = "CENTER_ID")
    public Integer getCenterId() {
        return centerId;
    }

    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }

    public void setCenterId(Center center) {
        this.centerId = (center != null) ? center.getId() : null;
    }

    @Transient
    public T getLogType() {
        return logType;
    }

    public void setLogType(T event) {
        this.logType = event;
    }

    @NotNull(message = "{Log.logTypeId.NotNull}")
    @Column(name = "LOG_TYPE_ID")
    Integer getLogTypeId() {
        return (logType != null) ? logType.getId() : null;
    }

    void setLogTypeId(Integer logTypeId) {
        logType = getLogTypeResolver().get(logTypeId);
    }

    protected abstract Class<T> getLogTypeClass();

    private Map<Integer, T> getLogTypeResolver() {
        // TODO: implement this!
        return null;
    }
}
