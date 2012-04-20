package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@SuppressWarnings("nls")
public class ShipmentInfoI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trnc NAME = bundle.trnc(
        "model",
        "Shipment Information",
        "Shipment Information");

    public static class Property {
        public static final LString PACKED_AT = bundle.trc(
            "model",
            "Time Packed").format();
        public static final LString SHIPPING_METHOD = bundle.trc(
            "model",
            "Shipping Method").format();
        public static final LString WAYBILL = bundle.trc(
            "model",
            "Waybill").format();
    }
}
