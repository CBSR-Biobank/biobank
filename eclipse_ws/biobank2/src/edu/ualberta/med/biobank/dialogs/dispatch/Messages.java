package edu.ualberta.med.biobank.dialogs.dispatch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.dialogs.dispatch.messages"; //$NON-NLS-1$
    public static String AbstractScanDialog_cancelPallet_label;
    public static String AbstractScanDialog_fakeScan_label;
    public static String AbstractScanDialog_launchScan_label;
    public static String AbstractScanDialog_plateToScan_label;
    public static String AbstractScanDialog_plateToScan_validationMsg;
    public static String AbstractScanDialog_proceedError_title;
    public static String AbstractScanDialog_processCell_task_position;
    public static String AbstractScanDialog_retryScan_label;
    public static String AbstractScanDialog_scan_validation_msg;
    public static String AbstractScanDialog_scanResults_errors_msg;
    public static String AbstractScanDialog_startPallet_label;
    public static String AbstractScanDialog_title;
    public static String DispatchCreateScanDialog_description;
    public static String DispatchCreateScanDialog_noposision_text;
    public static String DispatchCreateScanDialog_productBarcode_label;
    public static String DispatchCreateScanDialog_productBarcode_validationMsg;
    public static String DispatchCreateScanDialog_pallet_search_error_msg;
    public static String DispatchCreateScanDialog_pallet_search_error_title;
    public static String DispatchCreateScanDialog_proceed_button_label;
    public static String DispatchCreateScanDialog_with_position_radio_text;
    public static String DispatchCreateScanDialog_without_position_radio_text;
    public static String DispatchReceiveScanDialog_flagging_error_title;
    public static String DispatchReceiveScanDialog_notInDispatch_error_msg;
    public static String DispatchReceiveScanDialog_notInDispatch_error_title;
    public static String ReceiveScanDialog_description;
    public static String ReceiveScanDialog_proceed_button_label;
    public static String ModifyStateDispatchDialog_comment_label;
    public static String ModifyStateDispatchDialog_comment_validator_msg;
    public static String ModifyStateDispatchDialog_description_edit;
    public static String ModifyStateDispatchDialog_description_newState;
    public static String ModifyStateDispatchDialog_title_comment_only;
    public static String ModifyStateDispatchDialog_title_state;
    public static String RequestReceiveScanDialog_extra_msg;
    public static String RequestReceiveScanDialog_extra_title;
    public static String RequestReceiveScanDialog_receiveError_title;
    public static String SendDispatchDialog_description;
    public static String SendDispatchDialog_shippingMethod_label;
    public static String SendDispatchDialog_timePacked_label;
    public static String SendDispatchDialog_timePacked_validator_msg;
    public static String SendDispatchDialog_title;
    public static String SendDispatchDialog_waybill_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
