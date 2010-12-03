package edu.ualberta.med.biobank.common.util;

public enum OrderState {
    CREATION(0, "Creation"), RECEIVED(1, "Received"), PROCESSING(2,
        "Processing"), FILLED(3, "Filled"), SHIPPED(4, "Shipped"), CLOSED(5,
        "Closed"), LOST(6, "Lost");

    private Integer id;
    private String label;

    private OrderState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static OrderState getState(Integer state) {
        if (state == null)
            return CREATION;
        for (OrderState dss : values()) {
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
