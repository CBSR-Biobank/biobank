package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import edu.ualberta.med.biobank.model.type.ActivityType;

@Entity
@Table(name = "ACTIVITY")
public class Activity extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private Long createdAt = System.currentTimeMillis();
    private Center center;
    private Study study;
    private ActivityType activityType;
    private List<Arg> args = new ArrayList<Arg>(0);

    @Column(name = "CREATED_AT")
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "CENTER_ID")
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @Column(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Activity.activityType.NotNull}")
    @Column(name = "ACTIVITY_TYPE_ID")
    @Type(type = "activityType")
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    /**
     * The {@link User} responsible for this {@link Activity}. Note that this
     * could be null, in cases such as, periodic server or maintenance actions.
     * 
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ElementCollection
    @CollectionTable(name = "ACTIVITY_ARG", joinColumns = @JoinColumn(name = "ACTIVITY_ID"))
    @OrderColumn(name = "LIST_INDEX")
    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }

    @Embeddable
    public static class Arg implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer index;
        private String label;
        private Integer objectId;

        public Arg() {
        }

        public Arg(String label) {
            this.label = label;
        }

        public Arg(String label, HasId<Integer> model) {
            this.label = label;
            this.objectId = model.getId();
        }

        public Arg(Container container) {
            this(container.getLabel(), container);
        }

        public Arg(Patient patient) {
            this(patient.getPnumber(), patient);
        }

        public <T extends HasNameShort & HasId<Integer>> Arg(T o) {
            this(o.getNameShort(), o);
        }

        @Column(name = "LIST_INDEX")
        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        @Column(name = "LABEL")
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Column(name = "OBJECT_ID")
        public Integer getObjectId() {
            return objectId;
        }

        public void setObjectId(Integer objectId) {
            this.objectId = objectId;
        }
    }
}
