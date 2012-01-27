package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.validator.NotNull;

public class Clinic extends Center {
    private static final long serialVersionUID = 1L;

    private Boolean sendsShipments = false;
    private Collection<Contact> contactCollection = new HashSet<Contact>();

    @NotNull
    public Boolean getSendsShipments() {
        return sendsShipments;
    }

    public void setSendsShipments(Boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    public Collection<Contact> getContactCollection() {
        return contactCollection;
    }

    public void setContactCollection(Collection<Contact> contactCollection) {
        this.contactCollection = contactCollection;
    }
}