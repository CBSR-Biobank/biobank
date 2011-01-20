package edu.ualberta.med.biobank.widgets.report;

public class ChangeEvent {
    private final boolean isDataChange;

    public ChangeEvent(boolean isDataChange) {
        this.isDataChange = isDataChange;
    }

    public boolean isDataChange() {
        return isDataChange;
    }
}
