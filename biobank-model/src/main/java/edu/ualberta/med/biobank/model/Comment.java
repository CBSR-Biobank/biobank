package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

@Audited
@MappedSuperclass
public abstract class Comment
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private String message;

    @NotNull(message = "{Comment.user.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotNull(message = "{Comment.message.NotNull}")
    @Length(min = 1, max = 5000, message = "{Comment.message.Length}")
    @Column(name = "MESSAGE", nullable = false, length = 5000)
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
