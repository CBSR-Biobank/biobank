package edu.ualberta.med.biobank.common.util;

public enum OrderState {
    NEW(0, "New"), SUBMITTED(1, "Submitted"), APPROVED(2, "Approved"),
    ACCEPTED(3, "Accepted"), FILLED(4, "Filled"), SHIPPED(5, "Shipped"),
    CLOSED(6, "Closed"), LOST(7, "Lost");

    private Integer id;
    private String label;

    private OrderState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static OrderState getState(Integer state) {
        if (state == null)
            return NEW;
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
