package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.validator.Email;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@DiscriminatorValue("User")
public class User extends Principal {
    private static final long serialVersionUID = 1L;

    private String login;
    private Long csmUserId;
    private boolean recvBulkEmails = true;
    private String fullName;
    private String email;
    private boolean needPwdChange = true;
    private ActivityStatus activityStatus;
    private Collection<Comment> commentCollection = new HashSet<Comment>(0);
    private Collection<BbGroup> groupCollection = new HashSet<BbGroup>(0);

    @NotEmpty
    @Column(name = "LOGIN", unique = true)
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull
    @Column(name = "CSM_USER_ID")
    public Long getCsmUserId() {
        return this.csmUserId;
    }

    public void setCsmUserId(Long csmUserId) {
        this.csmUserId = csmUserId;
    }

    @Column(name = "RECV_BULK_EMAILS")
    // TODO: rename to isRecvBulkEmails
    public boolean getRecvBulkEmails() {
        return this.recvBulkEmails;
    }

    public void setRecvBulkEmails(boolean recvBulkEmails) {
        this.recvBulkEmails = recvBulkEmails;
    }

    @Column(name = "FULL_NAME")
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // TODO: write an email check that allows null @Email
    @Column(name = "EMAIL")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "NEED_PWD_CHANGE")
    // TODO: rename to isRecvBulkEmails
    public boolean getNeedPwdChange() {
        return this.needPwdChange;
    }

    public void setNeedPwdChange(boolean needPwdChange) {
        this.needPwdChange = needPwdChange;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", updatable = false)
    public Collection<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userCollection")
    public Collection<BbGroup> getGroupCollection() {
        return this.groupCollection;
    }

    public void setGroupCollection(Collection<BbGroup> groupCollection) {
        this.groupCollection = groupCollection;
    }
}
