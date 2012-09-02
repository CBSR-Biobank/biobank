package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;

/**
 * The id of these enumerations are saved in the database. Therefore, DO NOT
 * CHANGE THESE ENUM IDS (unless you are prepared to write an upgrade script).
 * However, order and enum name can be modified freely.
 * <p>
 * Also, these enums should probably never be deleted, unless they are not used
 * in <em>any</em> database. Instead, they should be deprecated and probably
 * always return false when checking allow-ability.
 * 
 * @author Jonathan Ferland
 */
@SuppressWarnings("nls")
public enum ShipmentItemState implements Serializable {
    /**
     * The item was recorded as shipped out, but it's not known yet whether it
     * is received, missing, or lost.
     */
    SENT("S"),
    /**
     * The item was recorded as sent from the source {@link CenterLocation} and
     * arrived as expected at the destination {@link CenterLocation}.
     */
    RECEIVED("R"),
    /**
     * The item was recorded as sent but never showed up in a {@link Shipment}.
     */
    MISSING("M"),
    /**
     * The item was never recorded as sent and showed up unexpectedly in a
     * {@link Shipment}.
     */
    EXTRA("E");

    private String id;

    private ShipmentItemState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
