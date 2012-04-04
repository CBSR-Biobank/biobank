package edu.ualberta.med.biobank.common.util;

public enum RequestState {
    NEW(0, "New"), SUBMITTED(1, "Submitted"), APPROVED(2, "Approved"), CLOSED(   
        3, "Closed"); 

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
