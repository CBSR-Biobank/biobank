package edu.ualberta.med.biobank.model.security;

public enum GlobalPermission
    implements Permission {
    CREATE_SITES(1),
    CREATE_STUDIES(2),

    CONFIGURE_ANATOMICAL_SOURCES(3),
    CONFIGURE_PRESERVATION_TYPES(4),
    CONFIGURE_SPECIMEN_TYPES(5),
    CONFIGURE_SHIPPING_METHODS(6),
    CONFIGURE_ROLES(7);

    private final Integer id;

    private GlobalPermission(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
