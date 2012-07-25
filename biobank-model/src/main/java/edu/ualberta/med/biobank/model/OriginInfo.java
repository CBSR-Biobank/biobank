package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;

@Audited
@Entity
@Table(name = "ORIGIN_INFO")
public class OriginInfo extends AbstractVersionedModel
    implements HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Origin Information",
        "Origin Information");

    private Set<Comment> comments = new HashSet<Comment>(0);
    private Set<Specimen> specimens = new HashSet<Specimen>(0);
    private ShipmentInfo shipmentInfo;
    private Center center;
    private Site receiverSite;

    @Override
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "ORIGIN_INFO_COMMENT",
        joinColumns = { @JoinColumn(name = "ORIGIN_INFO_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originInfo")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<Specimen> getSpecimens() {
        return this.specimens;
    }

    public void setSpecimens(Set<Specimen> specimens) {
        this.specimens = specimens;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_INFO_ID", unique = true)
    public ShipmentInfo getShipmentInfo() {
        return this.shipmentInfo;
    }

    public void setShipmentInfo(ShipmentInfo shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.OriginInfo.center.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", nullable = false)
    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_SITE_ID")
    public Site getReceiverSite() {
        return this.receiverSite;
    }

    public void setReceiverSite(Site receiverSite) {
        this.receiverSite = receiverSite;
    }
}
