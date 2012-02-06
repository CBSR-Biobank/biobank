/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import java.util.Date;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ShippingMethodBaseWrapper;

public class ShipmentInfoBaseWrapper extends ModelWrapper<ShipmentInfo> {

    public ShipmentInfoBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentInfoBaseWrapper(WritableApplicationService appService,
        ShipmentInfo wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ShipmentInfo> getWrappedClass() {
        return ShipmentInfo.class;
    }

    @Override
    public Property<Integer, ? super ShipmentInfo> getIdProperty() {
        return ShipmentInfoPeer.ID;
    }

    @Override
    protected List<Property<?, ? super ShipmentInfo>> getProperties() {
        return ShipmentInfoPeer.PROPERTIES;
    }

    public String getWaybill() {
        return getProperty(ShipmentInfoPeer.WAYBILL);
    }

    public void setWaybill(String waybill) {
        String trimmed = waybill == null ? null : waybill.trim();
        setProperty(ShipmentInfoPeer.WAYBILL, trimmed);
    }

    public Date getReceivedAt() {
        return getProperty(ShipmentInfoPeer.RECEIVED_AT);
    }

    public void setReceivedAt(Date receivedAt) {
        setProperty(ShipmentInfoPeer.RECEIVED_AT, receivedAt);
    }

    public String getBoxNumber() {
        return getProperty(ShipmentInfoPeer.BOX_NUMBER);
    }

    public void setBoxNumber(String boxNumber) {
        String trimmed = boxNumber == null ? null : boxNumber.trim();
        setProperty(ShipmentInfoPeer.BOX_NUMBER, trimmed);
    }

    public Date getPackedAt() {
        return getProperty(ShipmentInfoPeer.PACKED_AT);
    }

    public void setPackedAt(Date packedAt) {
        setProperty(ShipmentInfoPeer.PACKED_AT, packedAt);
    }

    public ShippingMethodWrapper getShippingMethod() {
        ShippingMethodWrapper shippingMethod = getWrappedProperty(ShipmentInfoPeer.SHIPPING_METHOD, ShippingMethodWrapper.class);
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethodBaseWrapper shippingMethod) {
        setWrappedProperty(ShipmentInfoPeer.SHIPPING_METHOD, shippingMethod);
    }

    void setShippingMethodInternal(ShippingMethodBaseWrapper shippingMethod) {
        setWrappedProperty(ShipmentInfoPeer.SHIPPING_METHOD, shippingMethod);
    }

}
