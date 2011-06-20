package edu.ualberta.med.biobank.common.util;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum DispatchSpecimenState implements ItemState {
    NONE(0, Messages.getString("DispatchSpecimenState.ok_state")), RECEIVED(1, //$NON-NLS-1$
        Messages.getString("DispatchSpecimenState.received_state")), MISSING(2, //$NON-NLS-1$
        Messages.getString("DispatchSpecimenState.missing_state")), EXTRA(3, //$NON-NLS-1$
        Messages.getString("DispatchSpecimenState.extra_state")); //$NON-NLS-1$

    private Integer id;
    private String label;

    private DispatchSpecimenState(Integer id, String label) {
        this.id = id;
        this.label = label;
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
    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static DispatchSpecimenState getState(Integer state) {
        for (DispatchSpecimenState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }
}
