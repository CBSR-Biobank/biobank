package edu.ualberta.med.biobank.common.scanprocess;

import edu.ualberta.med.biobank.common.util.NotAProxy;

public enum CellStatus implements NotAProxy {
    NOT_INITIALIZED("Not Initialized"), INITIALIZED("Initialized"), FULL("Full"), FREE_LOCATIONS(
        "Free locations"), EMPTY("Empty"), FILLED("Filled"), NEW("New"), MOVED(
        "Moved"), MISSING("Missing"), ERROR("Error"), NO_TYPE("No type"), TYPE(
        "Type"), IN_SHIPMENT_EXPECTED("Expected"), IN_SHIPMENT_ADDED("Added"), DUPLICATE_SCAN(
        "Already Scanned"), IN_SHIPMENT_RECEIVED("Received"), EXTRA("Extra");

    private String legend;

    private CellStatus(String legend) {
        this.legend = legend;
    }

    public String getLegend() {
        return legend;
    }

    public CellStatus mergeWith(CellStatus newStatus) {
        switch (this) {
        case EMPTY:
            return newStatus;
        case FILLED:
        case MOVED:
            if (newStatus == MISSING || newStatus == ERROR) {
                return newStatus;
            }
            return this;
        case ERROR:
            return ERROR;
        case MISSING:
            if (newStatus == ERROR) {
                return ERROR;
            }
            return MISSING;
        default:
            break;
        }
        return EMPTY;
    }

}
