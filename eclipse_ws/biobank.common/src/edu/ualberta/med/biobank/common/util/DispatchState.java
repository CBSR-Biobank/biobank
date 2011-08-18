package edu.ualberta.med.biobank.common.util;

public enum DispatchState {
    CREATION(0, Messages.getString("DispatchState.creation.state")), IN_TRANSIT(1, Messages.getString("DispatchState.intransit.state")), RECEIVED(2, //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("DispatchState.received.state")), CLOSED(3, Messages.getString("DispatchState.closed.state")), LOST(4, Messages.getString("DispatchState.lost.state")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private Integer id;
    private String label;

    private DispatchState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static DispatchState getState(Integer state) {
        if (state == null)
            return CREATION;
        for (DispatchState dss : values()) {
            if (dss.getId().equals(state))
                return dss;
        }
        return null;
    }

    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
