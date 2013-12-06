package edu.ualberta.med.biobank.gui.common;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("nls")
public class BgcPlugin extends AbstractUIPlugin {

    /**
     * The icons defined for use in the application. The files for these icons are stored in the
     * "icons" directory for this plugin.
     * 
     * The enum values are defined along with the file's basename.
     * 
     * @author loyola
     * 
     */
    public enum Image {
        ACCEPT("accept.png"),
        ADD("add.png"),
        ADMIN("superAdmin.png"),
        ARROW_LEFT("arrow_left.png"),
        ARROW_LEFT2("arrow_left2.png"),
        ARROW_RIGHT("arrow_right.png"),
        BIN("bin.png"),
        BOX("box.png"),
        BULLET("bullet.png"),
        CABINET("cabinet.png"),
        CABINET_LINK_ASSIGN("cabinetLinkAssign.png"),
        CALENDAR("calendar.png"),
        CANCEL_FORM("cancel.png"),
        CHECK("checked.png"),
        CLINIC("clinic.png"),
        CLINICS("clinics.png"),
        CLINIC_SHIPMENT("shipment.png"),
        COLLECTION_EVENT("collectionEvent.png"),
        CONFIRM_FORM("confirm.png"),
        CONTAINERS("containers.png"),
        CONTAINER_TYPES("containerTypes.png"),
        DATABASE_GO("database_go.png"),
        DELETE("delete.png"),
        DIALOGS("dialogs.png"),
        DISPATCH_SHIPMENT("dispatch.png"),
        DISPATCH_SHIPMENT_ADD_SPECIMEN("dispatchScanAdd.png"),
        DISPATCH_SHIPMENT_CREATION("dispatch_creation.png"),
        DISPATCH_SHIPMENT_ERROR("dispatch_error.png"),
        DISPATCH_SHIPMENT_RECEIVING("dispatch_receiving.png"),
        DISPATCH_SHIPMENT_TRANSIT("dispatch_transit.png"),
        DOWN("bullet_arrow_down.png"),
        DRAWER("drawer.png"),
        EDIT_FORM("edit.png"),
        EMAIL("email.png"),
        EMAIL_BANNER("email_banner.png"),
        ERROR("error.png"),
        FORM_BG("form_banner.bmp"),
        FREEZER("freezer.png"),
        HOTEL("hotel.png"),
        HOURGLASS("hourglass.png"),
        LOCK("lock.png"),
        LOGGING("table_row_delete.png"),
        LOGINWIZ("loginWiz.png"),
        MAIN_PERSPECTIVE("mainPerspective.png"),
        PALLET("pallet.png"),
        PATIENT("patient.png"),
        PRINTER("printer.png"),
        PROCESSING("processingView.png"),
        PROCESSING_EVENT("processingPerspective.png"),
        RECEIVED("received.png"),
        RELOAD_FORM("reload.png"),
        REMOVE("remove.png"),
        REQUEST("request.png"),
        REQUEST_EDIT("request_edit.png"),
        REQUEST_FILLED("request_filled.png"),
        REQUEST_SHIPPED("request_shipped.png"),
        RESEARCH_GROUP("research_group.png"),
        RESEARCH_GROUPS("research_groups.png"),
        RESET_FORM("reset.png"),
        RESULTSET_FIRST("resultset_first.png"),
        RESULTSET_LAST("resultset_last.png"),
        RESULTSET_NEXT("resultset_next.png"),
        RESULTSET_PREV("resultset_previous.png"),
        SAVE_AS_NEW("application_form_add.png"),
        SCAN_ASSIGN("scanAssign.png"),
        SCAN_CLOSE_EDIT("scan_close_edit.png"),
        SCAN_EDIT("scan_edit.png"),
        SCAN_LINK("scanLink.png"),
        SEARCH("search.png"),
        SENT("sent.png"),
        SESSIONS("sessions.png"),
        SITE("site.png"),
        SITES("sites.png"),
        SPECIMEN("specimen.png"),
        STUDIES("studies.png"),
        STUDY("study.png"),
        TODAY("today.png"),
        TWO_ARROWS_LEFT("2left_arrow.png"),
        TWO_ARROWS_RIGHT("2right_arrow.png"),
        UNCHECK("unchecked.gif"),
        UP("bullet_arrow_up.png"),
        WAND("wand.png");

        private final String filename;

        private static final Map<String, Image> FILENAME_MAP;

        static {
            Map<String, Image> map = new HashMap<String, Image>();

            for (Image enumValue : values()) {
                Image check = map.get(enumValue.getFilename());
                if (check != null) {
                    throw new IllegalStateException("image for filename "
                        + enumValue.getFilename() + " used multiple times");
                }

                map.put(enumValue.filename, enumValue);
            }

            FILENAME_MAP = Collections.unmodifiableMap(map);
        }

        private Image(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }

        public static Map<String, Image> filenamesMap() {
            return FILENAME_MAP;
        }

        public static Image getFromFilename(String filename) {
            Image result = filenamesMap().get(filename);
            if (result == null) {
                throw new IllegalStateException("invalid image filename: " + filename);
            }
            return result;
        }
    }

    private static final I18n i18n = I18nFactory.getI18n(BgcPlugin.class);
    private static BgcLogger logger = BgcLogger.getLogger(BgcPlugin.class
        .getName());

    private static final String CONNECTION_FAILED_TITLE =
        i18n.tr("Connection Attempt Failed");
    private static final String DB_OP_FAILURE =
        i18n.tr("Could not perform database operation. Make sure server is running correct version.");
    private static final String CANNOT_CONNECT_TO_SERVER =
        i18n.tr("Could not connect to server. Make sure server is running.");
    private static final String ACCESS_DENIED_TITLE =
        i18n.tr("Access Denied");
    private static final String ACCESS_DENIED_MESSAGE =
        i18n.tr("You don't have rights to do this action.");

    // The plug-in ID
    public static final String PLUGIN_ID = "biobank.gui.common";

    // The shared instance
    private static BgcPlugin plugin;

    /**
     * The constructor
     */
    public BgcPlugin() {
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        for (Image image : Image.values()) {
            registerImage(registry, image);
        }
    }

    private void registerImage(ImageRegistry registry, Image image) {
        try {
            String filename = image.getFilename();
            IPath path = new Path("icons/" + filename);
            URL url = FileLocator.find(getBundle(), path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(filename, desc);
            } else {
                logger.error("Could not get URL for image: " + filename);
            }
        } catch (Exception e) {
            logger.error("Error registering an image", e);
        }
    }

    public org.eclipse.swt.graphics.Image getImage(Image image) {
        return getImageRegistry().get(image.getFilename());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static BgcPlugin getDefault() {
        return plugin;
    }

    /**
     * Display an information message
     */
    public static void openMessage(String title, String message) {
        MessageDialog.openInformation(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, message);
    }

    /**
     * Display an information message
     */
    public static boolean openConfirm(String title, String message) {
        return MessageDialog.openConfirm(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, message);
    }

    /**
     * Display an error message
     */
    public static void openError(String title, String message) {
        openError(title, message, null, null);
    }

    /**
     * Display an error message with exception message and log the exception
     */
    public static void openError(String title, Exception e) {
        openError(title, null, e, null);
    }

    /**
     * Display an error message with exception message and log the exception
     */
    public static void openError(String title, String message, Exception e) {
        openError(title, message, e, null);
    }

    /**
     * Display an error message with exception message and log the exception
     */
    public static void openError(String title, String message, Throwable e,
        String secondMessage) {
        String msg = message;
        if ((msg == null) && (e != null)) {
            msg = e.getMessage();
            if (((msg == null) || msg.isEmpty()) && (e.getCause() != null)) {
                msg = e.getCause().getMessage();
            }
        }
        if (msg == null) {
            msg = "";
        }
        if (secondMessage != null) {
            if (!msg.isEmpty()) {
                msg += "\n";
            }
            msg += secondMessage;
        }
        MessageDialog.openError(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, msg);
        if (e != null)
            logger.error(title, e);
    }

    /**
     * Display an error message asynchronously
     */
    public static void openAsyncError(final String title, final String message,
        final Throwable e, final String secondMessage) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                openError(title, message, e, secondMessage);
            }
        });
    }

    public static void openAsyncError(final String title, final String message) {
        openAsyncError(title, message, null, null);
    }

    public static void openAsyncError(final String message) {
        openAsyncError(
            // dialog title
            i18n.tr("Error"),
            message, null, null);
    }

    public static void openAsyncError(final String title, final String message,
        final Exception e) {
        openAsyncError(title, message, e, null);
    }

    public static void openAsyncError(String title, Throwable e,
        String secondMessage) {
        openAsyncError(title, null, e, secondMessage);
    }

    public static void openAsyncError(String title, Throwable e) {
        openAsyncError(title, null, e, null);
    }

    /**
     * Display an info message
     */
    public static void openInformation(String title, String message) {
        MessageDialog.openInformation(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, message);
    }

    public static void openAsyncInformation(final String title,
        final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openInformation(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), title, message);
            }
        });
    }

    /**
     * Display remote access error message
     */
    public static void openRemoteAccessErrorMessage(Throwable ex) {
        openAsyncError(
            CONNECTION_FAILED_TITLE,
            DB_OP_FAILURE);
        if (ex != null) {
            logger.error("Connection Attempt Failed", ex);
        }
    }

    /**
     * Display remote connect error message
     */
    public static void openRemoteConnectErrorMessage(Throwable ex) {
        openAsyncError(CONNECTION_FAILED_TITLE, CANNOT_CONNECT_TO_SERVER);
        if (ex != null) {
            logger.error("Connection Attempt Failed", ex);
        }
    }

    public static void openAccessDeniedErrorMessage() {
        openAccessDeniedErrorMessage(null);
    }

    /**
     * Display remote access error message
     */
    public static void openAccessDeniedErrorMessage(Throwable ex) {
        openAsyncError(ACCESS_DENIED_TITLE, ACCESS_DENIED_MESSAGE);
        if (ex != null) {
            logger.error("Access denied", ex);
        }
    }

    public static LoginPermissionSessionState getLoginStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        return (LoginPermissionSessionState) service
            .getSourceProvider(LoginPermissionSessionState.LOGIN_STATE_SOURCE_NAME);
    }

}
