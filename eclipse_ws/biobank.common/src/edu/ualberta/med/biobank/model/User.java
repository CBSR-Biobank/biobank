package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;

@Entity
@DiscriminatorValue("User")
@Unique.List({ @Unique(properties = { "login" }) })
public class User extends Principal {
    private static final long serialVersionUID = 1L;

    private String login;
    private Long csmUserId;
    private boolean recvBulkEmails = true;
    private String fullName;
    private String email;
    private boolean needPwdChange = true;
    private Set<Comment> commentCollection = new HashSet<Comment>(0);
    private Set<BbGroup> groupCollection = new HashSet<BbGroup>(0);

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", updatable = false)
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userCollection")
    public Set<BbGroup> getGroupCollection() {
        return this.groupCollection;
    }

    public void setGroupCollection(Set<BbGroup> groupCollection) {
        this.groupCollection = groupCollection;
    }
}
