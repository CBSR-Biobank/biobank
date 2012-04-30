package edu.ualberta.med.biobank.common.util;

public enum RequestState {
    NEW(0, Messages.getString("RequestState.new.label")), SUBMITTED(1, Messages.getString("RequestState.submitted.label")), APPROVED(2, Messages.getString("RequestState.approved.label")), CLOSED( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        3, Messages.getString("RequestState.closed.label")); //$NON-NLS-1$

    private Integer id;
    private String label;

    private RequestState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestState getState(Integer state) {
        if (state == null)
            return values()[0];
        for (RequestState dss : values()) {
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
