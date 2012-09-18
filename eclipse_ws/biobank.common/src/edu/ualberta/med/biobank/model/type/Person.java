package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

import edu.ualberta.med.biobank.model.User;

@Embeddable
public class Person
    implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private User user;

    @Length(max = 63, message = "{Person.name.Length}")
    @Column(name = "PERSON_NAME", length = 63)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
