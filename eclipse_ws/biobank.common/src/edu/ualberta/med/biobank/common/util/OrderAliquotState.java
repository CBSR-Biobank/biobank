package edu.ualberta.med.biobank.common.util;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum OrderAliquotState {
    NONPROCESSED_STATE(0, "Non-Processed"), PROCESSED_STATE(1, "Processed"),
    MISSING(2, "Missing");

    private Integer id;
    private String label;

    private OrderAliquotState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static OrderAliquotState getState(Integer state) {
        for (OrderAliquotState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

}
