package edu.ualberta.med.biobank.widgets.grids.cell;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import edu.ualberta.med.biobank.BiobankPlugin;

public enum UICellStatus {
    NOT_INITIALIZED(SWT.COLOR_WHITE, "Not Initialized"), INITIALIZED(
        SWT.COLOR_DARK_GRAY, "Initialized"), FULL(SWT.COLOR_DARK_GRAY, "Full"),
    FREE_LOCATIONS(SWT.COLOR_WHITE, "Free locations"), EMPTY(SWT.COLOR_WHITE,
        "Empty"), FILLED(SWT.COLOR_DARK_GRAY, "Filled"), NEW(
        SWT.COLOR_DARK_GREEN, "New"), MOVED(217, 161, 65, "Moved"), MISSING(
        SWT.COLOR_CYAN, "Missing"), ERROR(SWT.COLOR_RED, "Error"), NO_TYPE(
        SWT.COLOR_DARK_GREEN, "No type"), TYPE(SWT.COLOR_DARK_GRAY, "Type"),
    IN_SHIPMENT_EXPECTED(SWT.COLOR_DARK_GREEN, "Expected"), IN_SHIPMENT_ADDED(
        SWT.COLOR_DARK_GREEN, "Added"), DUPLICATE_SCAN(SWT.COLOR_DARK_GRAY,
        "Already Scanned"), IN_SHIPMENT_RECEIVED(SWT.COLOR_DARK_GRAY,
        "Received"), EXTRA(SWT.COLOR_YELLOW, "Extra"), SCAN_PROFILE(185, 211,
        238, "Profiled cell");

    private Color color;
    private String legend;

    public static List<UICellStatus> DEFAULT_CONTAINER_STATUS_LIST = Arrays
        .asList(NOT_INITIALIZED, INITIALIZED);

    public static List<UICellStatus> DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST = Arrays
        .asList(EMPTY, NEW, MOVED, FILLED, MISSING, ERROR);

    public static List<UICellStatus> DEFAULT_PALLET_SCAN_LINK_STATUS_LIST = Arrays
        .asList(EMPTY, SCAN_PROFILE, NO_TYPE, TYPE, ERROR);

    public static List<UICellStatus> DEFAULT_PALLET_DISPATCH_RECEIVE_STATUS_LIST = Arrays
        .asList(EMPTY, IN_SHIPMENT_RECEIVED, IN_SHIPMENT_EXPECTED, EXTRA, ERROR);

    public static List<UICellStatus> DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST = Arrays
        .asList(EMPTY, FILLED, IN_SHIPMENT_ADDED, MISSING, ERROR);

    public static List<UICellStatus> REQUEST_PALLET_STATUS_LIST = Arrays.asList(
        EMPTY, DUPLICATE_SCAN, IN_SHIPMENT_EXPECTED, EXTRA, ERROR);

    private UICellStatus(int color, String legend) {
        this.color = BiobankPlugin.getDefault().getWorkbench().getDisplay()
            .getSystemColor(color);
        this.legend = legend;
    }

    private UICellStatus(int red, int green, int blue, String legend) {
        this.color = new Color(BiobankPlugin.getDefault().getWorkbench()
            .getDisplay(), red, green, blue);
        this.legend = legend;
    }

    public Color getColor() {
        return color;
    }

    public String getLegend() {
        return legend;
    }

    public UICellStatus mergeWith(UICellStatus newStatus) {
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
