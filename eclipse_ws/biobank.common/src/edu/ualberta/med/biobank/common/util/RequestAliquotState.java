package edu.ualberta.med.biobank.common.util;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum RequestAliquotState {
    NONPROCESSED_STATE(0, "Non-Processed"), PROCESSED_STATE(1, "Processed"),
    UNAVAILABLE_STATE(2, "Unavailable");

    private Integer id;
    private String label;

    private RequestAliquotState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestAliquotState getState(Integer state) {
        for (RequestAliquotState das : values()) {
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
