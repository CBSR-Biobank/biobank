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
public enum ShipmentState implements Serializable {
    CREATED("C"),
    PACKED("P"),
    SENT("S"),
    RECEIVED("R"),
    UNPACKED("U"),
    LOST("L");

    private String id;

    private ShipmentState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
