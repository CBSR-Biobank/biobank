package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trn;

@SuppressWarnings("nls")
// @formatter:off
public class ShipmentInfoI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trn NAME = bundle.trn("Shipment Information", "Shipment Information");

    public static class Property {
        public static final LString PACKED_AT = bundle.trc("ShipmentInfo Property", "Time Packed").format();
        public static final LString SHIPPING_METHOD = ShippingMethodI18n.NAME.format(1);
        public static final LString WAYBILL = bundle.trc("ShipmentInfo Property",   "Waybill").format();
    }
}
