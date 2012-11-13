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
public enum RequestSpecimenState implements Serializable {
    AVAILABLE_STATE(0),
    PULLED_STATE(1),
    UNAVAILABLE_STATE(2),
    DISPATCHED_STATE(3);

    private Integer id;

    private RequestSpecimenState(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
