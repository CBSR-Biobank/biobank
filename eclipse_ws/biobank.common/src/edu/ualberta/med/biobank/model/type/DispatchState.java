package edu.ualberta.med.biobank.model.type;

public enum DispatchState {
    CREATION(0, "Creation"), IN_TRANSIT(1, "In Transit"), RECEIVED(2,  
        "Received"), CLOSED(3, "Closed"), LOST(4, "Lost");   

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
