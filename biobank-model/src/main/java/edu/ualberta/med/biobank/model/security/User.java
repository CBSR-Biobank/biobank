package edu.ualberta.med.biobank.model.security;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@DiscriminatorValue("USER")
@Unique.List({
    @Unique(properties = "email", groups = PrePersist.class),
    @Unique(properties = "login", groups = PrePersist.class)
})
public class User
    extends Principal {
    private static final long serialVersionUID = 1L;

    private String login;
    private String fullName;
    private String email;
    private Boolean passwordChangeNeeded;
    private Boolean mailingListSubscriber;

    @NotEmpty(message = "{User.login.NotEmpty}")
    @Column(name = "LOGIN", nullable = false, unique = true)
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotEmpty(message = "{User.email.NotEmpty}")
    @Email
    @Column(name = "EMAIL", nullable = false, unique = true)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull(message = "{User.passwordChangeNeeded.NotNull}")
    @Column(name = "IS_PASSWORD_CHANGE_NEEDED", nullable = false)
    public Boolean getPasswordChangeNeeded() {
        return passwordChangeNeeded;
    }

    public void setPasswordChangeNeeded(Boolean passwordChangeNeeded) {
        this.passwordChangeNeeded = passwordChangeNeeded;
    }

    @NotNull(message = "{User.mailingListSubscriber.NotNull}")
    @Column(name = "IS_MAILING_LIST_SUBSCRIBER", nullable = false)
    public Boolean getMailingListSubscriber() {
        return mailingListSubscriber;
    }

    public void setMailingListSubscriber(Boolean mailingListSubscriber) {
        this.mailingListSubscriber = mailingListSubscriber;
    }

    @Column(name = "FULL_NAME")
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
