package edu.ualberta.med.biobank.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ACTIVITY")
public class Activity extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private Integer activityType;
    private final List<ActivityArg> args = new ArrayList<ActivityArg>();

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

    public static class ActivityArg extends AbstractModel {
        private static final long serialVersionUID = 1L;

    }
}
