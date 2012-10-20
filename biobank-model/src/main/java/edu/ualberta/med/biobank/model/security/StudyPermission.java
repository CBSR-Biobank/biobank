package edu.ualberta.med.biobank.model.security;

public enum StudyPermission
    implements Permission {
    CREATE_SPECIMENS(1),
    CREATE_COLLECTION_EVENTS(2),
    CREATE_PATIENTS(3),

    CREATE_REQUESTS(9),
    SUBMIT_REQUESTS(10),

    CONFIGURE_DESCRIPTION(11),
    CONFIGURE_CENTERS(4),
    CONFIGURE_SPECIMEN_GROUPS(5),
    CONFIGURE_ANNOTATION_TYPES(6),
    CONFIGURE_COLLECTING(7),
    CONFIGURE_PROCESSING(8);

    private final Integer id;

    private StudyPermission(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
