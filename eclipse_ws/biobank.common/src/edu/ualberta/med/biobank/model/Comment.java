package edu.ualberta.med.biobank.model;

import java.util.Date;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

public class Comment extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String message;
    private Date createdAt;
    private User user;

    @NotEmpty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
