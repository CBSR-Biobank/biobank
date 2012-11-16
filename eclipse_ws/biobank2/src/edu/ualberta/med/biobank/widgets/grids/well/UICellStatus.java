package edu.ualberta.med.biobank.widgets.grids.well;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;

@SuppressWarnings("nls")
public enum UICellStatus {
    NOT_INITIALIZED(SWT.COLOR_WHITE, Loader.i18n.tr("Not Initialized")),
    INITIALIZED(SWT.COLOR_DARK_GRAY, Loader.i18n.tr("Initialized")),
    FULL(SWT.COLOR_DARK_GRAY, Loader.i18n.tr("Full")),
    FREE_LOCATIONS(SWT.COLOR_WHITE, Loader.i18n.tr("Free locations")),
    EMPTY(SWT.COLOR_WHITE, Loader.i18n.tr("Empty")),
    FILLED(SWT.COLOR_DARK_GRAY, Loader.i18n.tr("Filled")),
    NEW(SWT.COLOR_DARK_GREEN, Loader.i18n.tr("New")),
    MOVED(217, 161, 65, Loader.i18n.tr("Moved")),
    MISSING(SWT.COLOR_CYAN, Loader.i18n.tr("Missing")),
    ERROR(SWT.COLOR_RED, Loader.i18n.tr("Error")),
    NO_TYPE(SWT.COLOR_DARK_GREEN, Loader.i18n.tr("No type")),
    TYPE(SWT.COLOR_DARK_GRAY, Loader.i18n.tr("Type")),
    IN_SHIPMENT_EXPECTED(SWT.COLOR_DARK_GREEN, Loader.i18n.tr("Expected")),
    IN_SHIPMENT_ADDED(SWT.COLOR_DARK_GREEN, Loader.i18n.tr("Added")),
    DUPLICATE_SCAN(SWT.COLOR_DARK_GRAY, Loader.i18n.tr("Already Scanned")),
    IN_SHIPMENT_RECEIVED(SWT.COLOR_DARK_GRAY, Loader.i18n.tr("Received")),
    EXTRA(SWT.COLOR_YELLOW, Loader.i18n.tr("Extra")),
    SCAN_PROFILE(185, 211, 238, Loader.i18n.tr("Profiled cell"));

    private Color color;
    private String legend;

    public static List<UICellStatus> DEFAULT_CONTAINER_STATUS_LIST = Arrays
        .asList(NOT_INITIALIZED, INITIALIZED);

    public static List<UICellStatus> DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST =
        Arrays
            .asList(EMPTY, NEW, MOVED, FILLED, MISSING, ERROR);

    public static List<UICellStatus> DEFAULT_PALLET_SCAN_LINK_STATUS_LIST =
        Arrays
            .asList(EMPTY, NO_TYPE, TYPE, ERROR);

    public static List<UICellStatus> DEFAULT_PALLET_DISPATCH_RECEIVE_STATUS_LIST =
        Arrays
            .asList(EMPTY, IN_SHIPMENT_RECEIVED, IN_SHIPMENT_EXPECTED, EXTRA,
                ERROR);

    public static List<UICellStatus> DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST =
        Arrays
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
        if (this == EMPTY || this == MISSING)
            return newStatus;
        if (this == ERROR || newStatus == ERROR)
            return ERROR;
        if (this == FILLED || this == MOVED) {
            if (newStatus == MISSING || newStatus == ERROR) {
                return newStatus;
            }
            return this;
        }
        return EMPTY;
    }

    private static class Loader {
        public static final I18n i18n = I18nFactory.getI18n(UICellStatus.class);
    }
}
