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

@Entity
@Table(name = "ACTIVITY")
public class Activity extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private Long createdAt;
    private Center center;
    private Study study;
    private Integer activityTypeId;
    private List<Arg> args = new ArrayList<Arg>(0);

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

        public Arg(String label, Integer objectId) {
            this.label = label;
            this.objectId = objectId;
        }

        public Arg(String label, HasId<Integer> model) {
            this.label = label;
            this.objectId = model.getId();
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
