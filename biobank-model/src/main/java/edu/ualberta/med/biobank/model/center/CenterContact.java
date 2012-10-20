package edu.ualberta.med.biobank.model.center;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "CENTER_CONTACT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CENTER_ID", "EMAIL" }),
        @UniqueConstraint(columnNames = { "CENTER_ID", "USER" })
    })
@Unique.List({
    @Unique(properties = { "center", "contact.email" }, groups = PrePersist.class),
    @Unique(properties = { "center", "user" }, groups = PrePersist.class)
})
public class CenterContact
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Center center;
    private Contact contact;
    private User user;

    @NotNull(message = "{CenterLocation.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @Valid
    @Embedded
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    // TODO: require that either a contact or a user is selected, exclusively.
    // Write an @XOR annotation for a list of properties?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
