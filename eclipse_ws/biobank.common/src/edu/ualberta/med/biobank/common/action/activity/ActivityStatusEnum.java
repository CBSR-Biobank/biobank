package edu.ualberta.med.biobank.common.action.activity;

/**
 * important statuses used very often and referenced in the application
 */
public enum ActivityStatusEnum {
    ACTIVE(1), CLOSED(2), FLAGGED(4);

    // id in database!
    private Integer id;

    private ActivityStatusEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
