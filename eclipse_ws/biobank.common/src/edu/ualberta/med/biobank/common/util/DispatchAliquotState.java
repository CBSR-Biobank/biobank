package edu.ualberta.med.biobank.common.util;

public enum DispatchAliquotState {
    NONE_STATE("Ok"), RECEIVED_STATE("Received"), MISSING_PENDING_STATE(
        "Missing-Pending"), MISSING("Missing"), EXTRA_PENDING_STATE(
        "Extra-Pending"), EXTRA("Extra");

    private String label;

    private DispatchAliquotState(String label) {
        this.label = label;
    }

    public static DispatchAliquotState getState(Integer state) {
        return values()[state];
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }

}
