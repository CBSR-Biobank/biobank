package edu.ualberta.med.biobank.common.util;

public enum DispatchShipmentState {
    CREATION(0), IN_TRANSIT(1), RECEIVED(2), CLOSED(3), LOST(4);

    private Integer id;

    private DispatchShipmentState(Integer id) {
        this.id = id;
    }

    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    public Integer getId() {
        return id;
    }
}
