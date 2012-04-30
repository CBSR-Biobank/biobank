package edu.ualberta.med.biobank.treeview.shipment;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.shipment.messages"; //$NON-NLS-1$
    public static String ShipmentAdapter_delete_confirm;
    public static String ShipmentAdapter_noShipment_error_msg;
    public static String ShipmentAdapter_shipment_label;
    public static String ShipmentAdapter_tooltip_no_origin;
    public static String ShipmentTodayNode_0;
    public static String ShipmentTodayNode_1;
    public static String ShipmentTodayNode_add_label;
    public static String ShipmentTodayNode_today_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
