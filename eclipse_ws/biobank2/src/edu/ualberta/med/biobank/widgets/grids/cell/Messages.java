package edu.ualberta.med.biobank.widgets.grids.cell;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.grids.cell.messages"; //$NON-NLS-1$
    public static String UICellStatus_added;
    public static String UICellStatus_empty;
    public static String UICellStatus_error;
    public static String UICellStatus_expected;
    public static String UICellStatus_extra;
    public static String UICellStatus_filled;
    public static String UICellStatus_free;
    public static String UICellStatus_full;
    public static String UICellStatus_initialized;
    public static String UICellStatus_missing;
    public static String UICellStatus_moved;
    public static String UICellStatus_new;
    public static String UICellStatus_notInitialized;
    public static String UICellStatus_notype;
    public static String UICellStatus_profiled;
    public static String UICellStatus_received;
    public static String UICellStatus_scanned;
    public static String UICellStatus_type;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
