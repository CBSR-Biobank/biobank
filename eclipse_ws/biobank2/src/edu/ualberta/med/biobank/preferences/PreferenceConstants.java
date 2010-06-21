package edu.ualberta.med.biobank.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

    public static final String GENERAL_CONFIRM = "general.confirm";

    public static final String GENERAL_CANCEL = "general.cancel";

    public static final String SCANNER_DPI = "scanner.dpi";

    public static final String[] SCANNER_PLATE_BARCODES = {
        "scanner.plate.barcode.1", "scanner.plate.barcode.2",
        "scanner.plate.barcode.3", "scanner.plate.barcode.4",
        "scanner.plate.barcode.5" };

    public static final String LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE = "link.assign.activity.log.into.file";

    public static final String LINK_ASSIGN_ACTIVITY_LOG_PATH = "link.assign.activity.log.path";

    public static final String LINK_ASSIGN_ACTIVITY_LOG_ASK_PRINT = "link.assign.activity.log.ask_print";

    public static final String SCAN_LINK_ROW_SELECT_ONLY = "scan.link.row.select.only";

    public static final String PALLET_SCAN_CONTAINER_NAME_CONTAINS = "scan.link.container_name_contains";

    public static final String CABINET_CONTAINER_NAME_CONTAINS = "cabinet.container_name_contains";

    public static final String ISSUE_TRACKER_EMAIL = "issue.tracker.email";

    public static final String ISSUE_TRACKER_SMTP_SERVER = "issue.tracker.smtp.server";

    public static final String ISSUE_TRACKER_SMTP_SERVER_PORT = "issue.tracker.smtp.server.port";

    public static final String ISSUE_TRACKER_SMTP_SERVER_USER = "issue.tracker.smtp.server.user";

    public static final String ISSUE_TRACKER_SMTP_SERVER_PASSWORD = "issue.tracker.smtp.server.password";

    public static final String SERVER_LIST = "server.list";

}
