package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * A collection site that collects biospecimens and transports them to a
 * repository site. Biospecimens are collected from patients that are
 * participating in a study.
 * 
 * NCI Term: Collecting laboratory. The laboratory that collects specimens from
 * a study subject.
 */
@Audited
@Entity
@DiscriminatorValue("Clinic")
@NotUsed.List({
    @NotUsed(by = Study.class, property = "contacts.clinic", groups = PreDelete.class),
    @NotUsed(by = OriginInfo.class, property = "center", groups = PreDelete.class)
})
public class Clinic extends Center {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Clinic",
        "Clinics");

    public static class Property {
        @SuppressWarnings("nls")
        public static final LString SENDS_SHIPMENTS = bundle.trc(
            "model",
            "Sends Shipments").format();
    }

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
