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

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@Audited
@Entity
@Table(name = "SHIPMENT_INFO")
public class ShipmentInfo extends AbstractModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Shipment Information",
        "Shipment Information");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString BOX_NUMBER = bundle.trc(
            "model",
            "Box Number").format();
        public static final LString PACKED_AT = bundle.trc(
            "model",
            "Time Packed").format();
        public static final LString RECEIVED_AT = bundle.trc(
            "model",
            "Received At").format();
        public static final LString SHIPPING_METHOD = bundle.trc(
            "model",
            "Shipping Method").format();
        public static final LString WAYBILL = bundle.trc(
            "model",
            "Waybill").format();
    }

    private Date receivedAt;
    private Date packedAt;
    private String waybill;
    private String boxNumber;
    private ShippingMethod shippingMethod;

    @Column(name = "RECEIVED_AT")
    public Date getReceivedAt() {
        return this.receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Column(name = "PACKED_AT")
    public Date getPackedAt() {
        return this.packedAt;
    }

    public void setPackedAt(Date packedAt) {
        this.packedAt = packedAt;
    }

    @Column(name = "WAYBILL")
    public String getWaybill() {
        return this.waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    @Column(name = "BOX_NUMBER")
    public String getBoxNumber() {
        return this.boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    @NotNull(message = "{ShipmentInfo.shippingMethod.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_METHOD_ID", nullable = false)
    public ShippingMethod getShippingMethod() {
        return this.shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
}
