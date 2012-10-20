package edu.ualberta.med.biobank.model.center;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.ShippingMethod;

@Embeddable
public class ShipmentData
    implements Serializable {
    private static final long serialVersionUID = 1L;

    private String waybill;
    private String boxNumber;
    private ShippingMethod shippingMethod;

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

    @NotNull(message = "{ShipmentData.shippingMethod.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_METHOD_ID", nullable = false)
    public ShippingMethod getShippingMethod() {
        return this.shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
}
