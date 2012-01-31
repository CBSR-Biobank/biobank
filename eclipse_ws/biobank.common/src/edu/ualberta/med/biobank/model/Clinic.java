package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("Clinic")
public class Clinic extends Center {
    private static final long serialVersionUID = 1L;
    
    private boolean sendsShipments = false;
    private Set<Contact> contactCollection = new HashSet<Contact>(0);

    @Column(name = "SENDS_SHIPMENTS")
    // TODO: rename to isSendsShipments
    public boolean getSendsShipments() {
        return this.sendsShipments;
    }

    public void setSendsShipments(boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "clinic")
    public Set<Contact> getContactCollection() {
        return this.contactCollection;
    }

    public void setContactCollection(Set<Contact> contactCollection) {
        this.contactCollection = contactCollection;
    }
}
