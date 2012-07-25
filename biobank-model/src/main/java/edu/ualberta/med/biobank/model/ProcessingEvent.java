package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "PROCESSING_EVENT")
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "processingEvent", groups = PreDelete.class),
    @NotUsed(by = Specimen.class, property = "parentSpecimen.processingEvent", groups = PreDelete.class)
})
@Unique(properties = "worksheet", groups = PrePersist.class)
public class ProcessingEvent extends AbstractBiobankModel
    implements HasCreatedAt, HasActivityStatus, HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Processing Event",
        "Processing Events");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString CREATED_AT = bundle.trc(
            "model",
            "Created At").format();
        public static final LString WORKSHEET = bundle.trc(
            "model",
            "Worksheet").format();
    }

    private String worksheet;
    private Date createdAt;
    private Center center;
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.ProcessingEvent.worksheet.NotEmpty}")
    @Column(name = "WORKSHEET", length = 150, unique = true)
    public String getWorksheet() {
        return this.worksheet;
    }

    public void setWorksheet(String worksheet) {
        this.worksheet = worksheet;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.ProcessingEvent.createdAt.NotNull}")
    @Column(name = "CREATED_AT", nullable = false)
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.ProcessingEvent.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.ProcessingEvent.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "PROCESSING_EVENT_COMMENT",
        joinColumns = { @JoinColumn(name = "PROCESSING_EVENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
