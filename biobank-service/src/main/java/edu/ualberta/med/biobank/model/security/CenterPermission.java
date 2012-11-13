package edu.ualberta.med.biobank.model.security;

public enum CenterPermission
    implements Permission {
    CREATE_SPECIMENS(3),
    CREATE_COLLECTION_EVENTS(8),
    CREATE_PATIENTS(9),

    CREATE_SHIPMENTS(4),
    RECEIVE_SHIPMENTS(5),

    CONFIGURE_DESCRIPTION(2),
    CONFIGURE_CONTAINER_TYPES(1),
    CONFIGURE_CONTAINER_SCHEMAS(6),
    CONFIGURE_LOCATIONS(7);

    private final Integer id;

    private CenterPermission(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
