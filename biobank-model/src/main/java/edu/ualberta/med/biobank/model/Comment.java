package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@Audited
@Entity
@Table(name = "COMMENT")
public class Comment extends AbstractVersionedModel
    implements HasCreatedAt {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Comment",
        "Comments");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString CREATED_AT = bundle.trc(
            "model",
            "Created At").format();
        public static final LString MESSAGE = bundle.trc(
            "model",
            "Message").format();
    }

    private String message;
    private Date createdAt;
    private User user;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Comment.message.NotNull}")
    @Column(name = "MESSAGE", columnDefinition = "TEXT")
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.Comment.createdAt.NotNull}")
    @Column(name = "CREATED_AT")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // TODO: stop this property from being updated, and test that!
    @NotAudited
    @NotNull(message = "{edu.ualberta.med.biobank.model.Comment.user.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
