package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Entity
@DiscriminatorValue("Clinic")
@NotUsed(by = Contact.class, property = "clinic", groups = PreDelete.class)
public class Clinic extends Center {
    private static final long serialVersionUID = 1L;

    private boolean sendsShipments = false;
    private Set<Contact> contacts = new HashSet<Contact>(0);

    @Column(name = "SENDS_SHIPMENTS")
    // TODO: rename to isSendsShipments
    public boolean getSendsShipments() {
        return this.sendsShipments;
    }

    public void setSendsShipments(boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "clinic")
    public Set<Contact> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }
}
