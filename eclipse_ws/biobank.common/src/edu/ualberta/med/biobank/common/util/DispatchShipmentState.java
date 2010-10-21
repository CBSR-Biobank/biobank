package edu.ualberta.med.biobank.common.util;

public enum DispatchShipmentState {
    CREATION(0, "Creation"), IN_TRANSIT(1, "In Transit"), RECEIVED(2,
        "Received"), CLOSED(3, "Closed"), LOST(4, "Lost");

    private Integer id;
    private String label;

    private DispatchShipmentState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static DispatchShipmentState getState(Integer state) {
        return values()[state];
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
