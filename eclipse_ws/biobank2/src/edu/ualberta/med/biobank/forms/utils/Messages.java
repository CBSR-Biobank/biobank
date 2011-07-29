package edu.ualberta.med.biobank.forms.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.forms.utils.messages"; //$NON-NLS-1$

    public static String DispatchTableGroup_added_label;

    public static String DispatchTableGroup_nonprocessed_label;

    public static String PalletScanManagement_dialog_scanError_title;

    public static String PalletScanManagement_error_title;

    public static String PalletScanManagement_launching;

    public static String PalletScanManagement_rescan_differnt_msg;

    public static String PalletScanManagement_scan_error_msg_2dScanner;

    public static String PalletScanManagement_scan_error_msg_notenabled;

    public static String PalletScanManagement_scan_error_previous_different_msg;

    public static String PalletScanManagement_scan_error_title;

    public static String PalletScanManagement_scan_progress;

    public static String PalletScanManagement_tube_error_title;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String format(String key, Object... params) {
        return NLS.bind(key, params);
    }
}
