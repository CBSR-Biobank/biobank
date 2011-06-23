package edu.ualberta.med.biobank.gui.common;

import java.net.URL;

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

/**
 * The activator class controls the plug-in life cycle
 */
public class BgcPlugin extends AbstractUIPlugin {

    public static final String IMG_DIALOGS = "dialogs";
    public static final String IMG_ADD = "add";
    public static final String IMG_ARROW_LEFT = "arrow_left";
    public static final String IMG_ARROW_LEFT2 = "arrow_left2";
    public static final String IMG_ARROW_RIGHT = "arrow_right";
    public static final String IMG_2_ARROW_LEFT = "2_arrow_left";
    public static final String IMG_2_ARROW_RIGHT = "2_arrow_right";
    public static final String IMG_BIN = "bin";
    public static final String IMG_BOX = "box";
    public static final String IMG_CABINET = "cabinet";
    public static final String IMG_CABINET_LINK_ASSIGN = "cabinetLinkAssign";
    public static final String IMG_CANCEL_FORM = "cancelForm";
    public static final String IMG_CLINIC = "clinic";
    public static final String IMG_CLINICS = "clinics";
    public static final String IMG_LOGINWIZ = "computerKey";
    public static final String IMG_CONFIRM_FORM = "confirmForm";
    public static final String IMG_CONTAINERS = "containers";
    public static final String IMG_CONTAINER_TYPES = "containerTypes";
    public static final String IMG_DELETE = "delete";
    public static final String IMG_DRAWER = "drawer";
    public static final String IMG_EDIT_FORM = "editForm";
    public static final String IMG_FORM_BG = "formBg";
    public static final String IMG_FREEZER = "freezer";
    public static final String IMG_HOTEL = "hotel";
    public static final String IMG_MAIN_PERSPECTIVE = "mainPerspective";
    public static final String IMG_PALLET = "pallet";
    public static final String IMG_PATIENT = "patient";
    public static final String IMG_COLLECTION_EVENT = "collectionEvent";
    public static final String IMG_PRINTER = "printer";
    public static final String IMG_RELOAD_FORM = "reloadForm";
    public static final String IMG_RESET_FORM = "resetForm";
    public static final String IMG_RESULTSET_FIRST = "resultsetFirst";
    public static final String IMG_RESULTSET_LAST = "resultsetLast";
    public static final String IMG_RESULTSET_NEXT = "resultsetNext";
    public static final String IMG_RESULTSET_PREV = "resultsetPrev";
    public static final String IMG_SCAN_ASSIGN = "scanAssign";
    public static final String IMG_SCAN_LINK = "scanLink";
    public static final String IMG_SESSIONS = "sessions";
    public static final String IMG_CLINIC_SHIPMENT = "shipment";
    public static final String IMG_DISPATCH_SHIPMENT = "dispatch";
    public static final String IMG_DISPATCH_SHIPMENT_CREATION = "dispatchCreation";
    public static final String IMG_DISPATCH_SHIPMENT_TRANSIT = "dispatchTransit";
    public static final String IMG_DISPATCH_SHIPMENT_RECEIVING = "dispatchReceiving";
    public static final String IMG_DISPATCH_SHIPMENT_ERROR = "dispatchError";
    public static final String IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN = "dispatchAddSpecimen";
    public static final String IMG_SITE = "site";
    public static final String IMG_SITES = "sites";
    public static final String IMG_STUDIES = "studies";
    public static final String IMG_STUDY = "study";
    public static final String IMG_USER_ADD = "userAdd";
    public static final String IMG_EMAIL = "email";
    public static final String IMG_EMAIL_BANNER = "emailBanner";
    public static final String IMG_SEARCH = "search";
    public static final String IMG_TODAY = "today";
    public static final String IMG_CALENDAR = "calendar";
    public static final String IMG_BULLET = "bullet";
    public static final String IMG_SCAN_EDIT = "scanEdit";
    public static final String IMG_SCAN_CLOSE_EDIT = "scanCloseEdit";
    public static final String IMG_RECEIVED = "received";
    public static final String IMG_SENT = "sent";
    public static final String IMG_REQUEST = "request";
    public static final String IMG_REQUEST_EDIT = "request_edit";
    public static final String IMG_REQUEST_SHIPPED = "request_shipped";
    public static final String IMG_REQUEST_FILLED = "request_filled";
    public static final String IMG_SPECIMEN = "specimen";
    public static final String IMG_LOCK = "lock";
    public static final String IMG_UP = "bullet_arrow_up";
    public static final String IMG_DOWN = "bullet_arrow_down";
    public static final String IMG_REMOVE = "remove";
    public static final String IMG_WAND = "wand";
    public static final String IMG_HOURGLASS = "hourglass";
    public static final String IMG_LOGGING = "logging";
    public static final String IMG_PROCESSING = "processing";
    public static final String IMG_SAVE_AS_NEW = "saveAsNew";
    public static final String IMG_PROCESSING_EVENT = "processingEvent";
    public static final String IMG_CHECK = "check";
    public static final String IMG_UNCHECK = "uncheck";

    private static BgcLogger logger = BgcLogger.getLogger(BgcPlugin.class
        .getName());

    // The plug-in ID
    public static final String PLUGIN_ID = "biobank.gui.common"; //$NON-NLS-1$

    // The shared instance
    private static BgcPlugin plugin;

    /**
     * The constructor
     */
    public BgcPlugin() {
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        registerImage(registry, IMG_DIALOGS, "dialogs.png");
        registerImage(registry, IMG_ADD, "add.png");
        registerImage(registry, IMG_ARROW_LEFT, "arrow_left.png");
        registerImage(registry, IMG_ARROW_LEFT2, "arrow_left2.png");
        registerImage(registry, IMG_ARROW_RIGHT, "arrow_right.png");
        registerImage(registry, IMG_2_ARROW_LEFT, "2left_arrow.png");
        registerImage(registry, IMG_2_ARROW_RIGHT, "2right_arrow.png");
        registerImage(registry, IMG_BIN, "bin.png");
        registerImage(registry, IMG_BULLET, "bullet.png");
        registerImage(registry, IMG_BOX, "bin.png");
        registerImage(registry, IMG_CABINET, "cabinet.png");
        registerImage(registry, IMG_CABINET_LINK_ASSIGN,
            "cabinetLinkAssign.png");
        registerImage(registry, IMG_CALENDAR, "calendar.png");
        registerImage(registry, IMG_CANCEL_FORM, "cancel.png");
        registerImage(registry, IMG_CLINIC, "clinic.png");
        registerImage(registry, IMG_CLINICS, "clinics.png");
        registerImage(registry, IMG_LOGINWIZ, "loginWiz.png");
        registerImage(registry, IMG_CONFIRM_FORM, "confirm.png");
        registerImage(registry, IMG_CONTAINERS, "containers.png");
        registerImage(registry, IMG_CONTAINER_TYPES, "containerTypes.png");
        registerImage(registry, IMG_DELETE, "delete.png");
        registerImage(registry, IMG_DRAWER, "drawer.png");
        registerImage(registry, IMG_EDIT_FORM, "edit.png");
        registerImage(registry, IMG_FORM_BG, "form_banner.bmp");
        registerImage(registry, IMG_FREEZER, "freezer.png");
        registerImage(registry, IMG_HOTEL, "hotel.png");
        registerImage(registry, IMG_MAIN_PERSPECTIVE, "mainPerspective.png");
        registerImage(registry, IMG_PALLET, "pallet.png");
        registerImage(registry, IMG_PATIENT, "patient.png");
        registerImage(registry, IMG_COLLECTION_EVENT, "collectionEvent.png");
        registerImage(registry, IMG_PRINTER, "printer.png");
        registerImage(registry, IMG_RELOAD_FORM, "reload.png");
        registerImage(registry, IMG_RESET_FORM, "reset.png");
        registerImage(registry, IMG_RESULTSET_FIRST, "resultset_first.png");
        registerImage(registry, IMG_RESULTSET_LAST, "resultset_last.png");
        registerImage(registry, IMG_RESULTSET_NEXT, "resultset_next.png");
        registerImage(registry, IMG_RESULTSET_PREV, "resultset_previous.png");
        registerImage(registry, IMG_SCAN_ASSIGN, "scanAssign.png");
        registerImage(registry, IMG_SCAN_LINK, "scanLink.png");
        registerImage(registry, IMG_SCAN_EDIT, "scan_edit.png");
        registerImage(registry, IMG_SCAN_CLOSE_EDIT, "scan_close_edit.png");
        registerImage(registry, IMG_SESSIONS, "sessions.png");
        registerImage(registry, IMG_CLINIC_SHIPMENT, "shipment.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT, "dispatch.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_CREATION,
            "dispatch_creation.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_TRANSIT,
            "dispatch_transit.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_RECEIVING,
            "dispatch_receiving.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_ERROR,
            "dispatch_error.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN,
            "dispatchScanAdd.png");
        registerImage(registry, IMG_REQUEST, "request.png");
        registerImage(registry, IMG_REQUEST_EDIT, "request_edit.png");
        registerImage(registry, IMG_REQUEST_SHIPPED, "request_shipped.png");
        registerImage(registry, IMG_REQUEST_FILLED, "request_filled.png");
        registerImage(registry, IMG_SITE, "site.png");
        registerImage(registry, IMG_SITES, "sites.png");
        registerImage(registry, IMG_STUDIES, "studies.png");
        registerImage(registry, IMG_STUDY, "study.png");
        registerImage(registry, IMG_EMAIL, "email.png");
        registerImage(registry, IMG_EMAIL_BANNER, "email_banner.png");
        registerImage(registry, IMG_SEARCH, "search.png");
        registerImage(registry, IMG_TODAY, "today.png");
        registerImage(registry, IMG_RECEIVED, "received.png");
        registerImage(registry, IMG_SENT, "sent.png");
        registerImage(registry, IMG_SPECIMEN, "specimen.png");
        registerImage(registry, IMG_LOCK, "lock.png");
        registerImage(registry, IMG_UP, "bullet_arrow_up.png");
        registerImage(registry, IMG_DOWN, "bullet_arrow_down.png");
        registerImage(registry, IMG_REMOVE, "remove.png");
        registerImage(registry, IMG_WAND, "wand.png");
        registerImage(registry, IMG_HOURGLASS, "hourglass.png");
        registerImage(registry, IMG_LOGGING, "table_row_delete.png");
        registerImage(registry, IMG_PROCESSING, "processingView.png");
        registerImage(registry, IMG_PROCESSING_EVENT,
            "processingPerspective.png");
        registerImage(registry, IMG_SAVE_AS_NEW, "application_form_add.png");
        registerImage(registry, IMG_CHECK, "checked.gif");
        registerImage(registry, IMG_UNCHECK, "unchecked.gif");
    }

    public void registerImage(ImageRegistry registry, String key,
        String fileName) {
        try {
            IPath path = new Path("icons/" + fileName);
            URL url = FileLocator.find(getBundle(), path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            } else {
                logger.error("Could not get URL for image: key" + key
                    + ", filname " + fileName);
            }
        } catch (Exception e) {
            logger.error("Error registering an image", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
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
    public static void openError(String title, String message, Exception e,
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
        final Exception e, final String secondMessage) {
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

    public static void openAsyncError(final String title, final String message,
        final Exception e) {
        openAsyncError(title, message, e, null);
    }

    public static void openAsyncError(String title, Exception e,
        String secondMessage) {
        openAsyncError(title, null, e, secondMessage);
    }

    public static void openAsyncError(String title, Exception e) {
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
            "Connection Attempt Failed",
            "Could not perform database operation. Make sure server is running correct version.");
        if (ex != null) {
            logger.error("Connection Attempt Failed", ex);
        }
    }

    /**
     * Display remote connect error message
     */
    public static void openRemoteConnectErrorMessage(Throwable ex) {
        openAsyncError("Connection Attempt Failed",
            "Could not connect to server. Make sure server is running.");
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
        openAsyncError("Access Denied",
            "You don't have rights to do this action.");
        if (ex != null) {
            logger.error("Connection Attempt Failed", ex);
        }
    }

    public static BgcSessionState getSessionStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        return (BgcSessionState) service
            .getSourceProvider(BgcSessionState.SESSION_STATE_SOURCE_NAME);
    }

}
