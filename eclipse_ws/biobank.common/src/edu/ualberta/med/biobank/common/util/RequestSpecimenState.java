package edu.ualberta.med.biobank.common.util;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum RequestSpecimenState implements ItemState {
    AVAILABLE_STATE(0, Messages.getString("RequestSpecimenState.available.label")), //$NON-NLS-1$
    PULLED_STATE(1, Messages.getString("RequestSpecimenState.pulled.label")), //$NON-NLS-1$
    UNAVAILABLE_STATE(2, Messages.getString("RequestSpecimenState.unavailable.label")), //$NON-NLS-1$
    DISPATCHED_STATE(3, Messages.getString("RequestSpecimenState.dispatched.label")); //$NON-NLS-1$

    private Integer id;
    private String label;

    private RequestSpecimenState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestSpecimenState getState(Integer state) {
        for (RequestSpecimenState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

}
