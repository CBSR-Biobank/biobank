package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

public class User extends Principal {
    private static final long serialVersionUID = 1L;

    private String login;
    private Long csmUserId;
    private Boolean recvBulkEmails;
    private String email;
    private Boolean needPwdChange;
    private ActivityStatus activityStatus;
    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private Collection<BbGroup> groupCollection = new HashSet<BbGroup>();

    @NotEmpty
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull
    public Long getCsmUserId() {
        return csmUserId;
    }

    public void setCsmUserId(Long csmUserId) {
        this.csmUserId = csmUserId;
    }

    public Boolean getRecvBulkEmails() {
        return recvBulkEmails;
    }

    public void setRecvBulkEmails(Boolean recvBulkEmails) {
        this.recvBulkEmails = recvBulkEmails;
    }

    public String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNeedPwdChange() {
        return needPwdChange;
    }

    public void setNeedPwdChange(Boolean needPwdChange) {
        this.needPwdChange = needPwdChange;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public Collection<BbGroup> getGroupCollection() {
        return groupCollection;
    }

    public void setGroupCollection(Collection<BbGroup> groupCollection) {
        this.groupCollection = groupCollection;
    }
}