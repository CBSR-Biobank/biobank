package edu.ualberta.med.biobank.common.util;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum DispatchAliquotState {
    NONE_STATE(0, "Ok"), RECEIVED_STATE(1, "Received"), MISSING(2, "Missing"), EXTRA(
        3, "Extra");

    private Integer id;
    private String label;

    private DispatchAliquotState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static DispatchAliquotState getState(Integer state) {
        return values()[state];
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
