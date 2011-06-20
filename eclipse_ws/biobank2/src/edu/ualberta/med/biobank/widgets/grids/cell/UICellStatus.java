package edu.ualberta.med.biobank.widgets.grids.cell;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import edu.ualberta.med.biobank.BiobankPlugin;

public enum UICellStatus {
    NOT_INITIALIZED(SWT.COLOR_WHITE, Messages.UICellStatus_notInitialized),
    INITIALIZED(SWT.COLOR_DARK_GRAY, Messages.UICellStatus_initialized),
    FULL(SWT.COLOR_DARK_GRAY, Messages.UICellStatus_full),
    FREE_LOCATIONS(SWT.COLOR_WHITE, Messages.UICellStatus_free),
    EMPTY(SWT.COLOR_WHITE, Messages.UICellStatus_empty),
    FILLED(SWT.COLOR_DARK_GRAY, Messages.UICellStatus_filled),
    NEW(SWT.COLOR_DARK_GREEN, Messages.UICellStatus_new),
    MOVED(217, 161, 65, Messages.UICellStatus_moved),
    MISSING(SWT.COLOR_CYAN, Messages.UICellStatus_missing),
    ERROR(SWT.COLOR_RED, Messages.UICellStatus_error),
    NO_TYPE(SWT.COLOR_DARK_GREEN, Messages.UICellStatus_notype),
    TYPE(SWT.COLOR_DARK_GRAY, Messages.UICellStatus_type),
    IN_SHIPMENT_EXPECTED(SWT.COLOR_DARK_GREEN, Messages.UICellStatus_expected),
    IN_SHIPMENT_ADDED(SWT.COLOR_DARK_GREEN, Messages.UICellStatus_added),
    DUPLICATE_SCAN(SWT.COLOR_DARK_GRAY, Messages.UICellStatus_scanned),
    IN_SHIPMENT_RECEIVED(SWT.COLOR_DARK_GRAY, Messages.UICellStatus_received),
    EXTRA(SWT.COLOR_YELLOW, Messages.UICellStatus_extra),
    SCAN_PROFILE(185, 211, 238, Messages.UICellStatus_profiled);

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

    public static List<UICellStatus> REQUEST_PALLET_STATUS_LIST = Arrays
        .asList(EMPTY, DUPLICATE_SCAN, IN_SHIPMENT_EXPECTED, EXTRA, ERROR);

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
