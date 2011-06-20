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

    public static final String IMG_DIALOGS = "dialogs"; //$NON-NLS-1$
    public static final String IMG_ADD = "add"; //$NON-NLS-1$
    public static final String IMG_ARROW_LEFT = "arrow_left"; //$NON-NLS-1$
    public static final String IMG_ARROW_LEFT2 = "arrow_left2"; //$NON-NLS-1$
    public static final String IMG_ARROW_RIGHT = "arrow_right"; //$NON-NLS-1$
    public static final String IMG_2_ARROW_LEFT = "2_arrow_left"; //$NON-NLS-1$
    public static final String IMG_2_ARROW_RIGHT = "2_arrow_right"; //$NON-NLS-1$
    public static final String IMG_BIN = "bin"; //$NON-NLS-1$
    public static final String IMG_BOX = "box"; //$NON-NLS-1$
    public static final String IMG_CABINET = "cabinet"; //$NON-NLS-1$
    public static final String IMG_CABINET_LINK_ASSIGN = "cabinetLinkAssign"; //$NON-NLS-1$
    public static final String IMG_CANCEL_FORM = "cancelForm"; //$NON-NLS-1$
    public static final String IMG_CLINIC = "clinic"; //$NON-NLS-1$
    public static final String IMG_CLINICS = "clinics"; //$NON-NLS-1$
    public static final String IMG_LOGINWIZ = "computerKey"; //$NON-NLS-1$
    public static final String IMG_CONFIRM_FORM = "confirmForm"; //$NON-NLS-1$
    public static final String IMG_CONTAINERS = "containers"; //$NON-NLS-1$
    public static final String IMG_CONTAINER_TYPES = "containerTypes"; //$NON-NLS-1$
    public static final String IMG_DELETE = "delete"; //$NON-NLS-1$
    public static final String IMG_DRAWER = "drawer"; //$NON-NLS-1$
    public static final String IMG_EDIT_FORM = "editForm"; //$NON-NLS-1$
    public static final String IMG_FORM_BG = "formBg"; //$NON-NLS-1$
    public static final String IMG_FREEZER = "freezer"; //$NON-NLS-1$
    public static final String IMG_HOTEL = "hotel"; //$NON-NLS-1$
    public static final String IMG_LOGIN = "login"; //$NON-NLS-1$
    public static final String IMG_LOGOUT = "logout"; //$NON-NLS-1$
    public static final String IMG_MAIN_PERSPECTIVE = "mainPerspective"; //$NON-NLS-1$
    public static final String IMG_PALLET = "pallet"; //$NON-NLS-1$
    public static final String IMG_PATIENT = "patient"; //$NON-NLS-1$
    public static final String IMG_PATIENT_VISIT = "patientVisit"; //$NON-NLS-1$
    public static final String IMG_PRINTER = "patientVisit"; //$NON-NLS-1$
    public static final String IMG_RELOAD_FORM = "reloadForm"; //$NON-NLS-1$
    public static final String IMG_REPORTS = "reports"; //$NON-NLS-1$
    public static final String IMG_RESET_FORM = "resetForm"; //$NON-NLS-1$
    public static final String IMG_RESULTSET_FIRST = "resultsetFirst"; //$NON-NLS-1$
    public static final String IMG_RESULTSET_LAST = "resultsetLast"; //$NON-NLS-1$
    public static final String IMG_RESULTSET_NEXT = "resultsetNext"; //$NON-NLS-1$
    public static final String IMG_RESULTSET_PREV = "resultsetPrev"; //$NON-NLS-1$
    public static final String IMG_SCAN_ASSIGN = "scanAssign"; //$NON-NLS-1$
    public static final String IMG_SCAN_LINK = "scanLink"; //$NON-NLS-1$
    public static final String IMG_SESSIONS = "sessions"; //$NON-NLS-1$
    public static final String IMG_CLINIC_SHIPMENT = "shipment"; //$NON-NLS-1$
    public static final String IMG_DISPATCH_SHIPMENT = "dispatch"; //$NON-NLS-1$
    public static final String IMG_DISPATCH_SHIPMENT_CREATION = "dispatchCreation"; //$NON-NLS-1$
    public static final String IMG_DISPATCH_SHIPMENT_TRANSIT = "dispatchTransit"; //$NON-NLS-1$
    public static final String IMG_DISPATCH_SHIPMENT_RECEIVING = "dispatchReceiving"; //$NON-NLS-1$
    public static final String IMG_DISPATCH_SHIPMENT_ERROR = "dispatchError"; //$NON-NLS-1$
    public static final String IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN = "dispatchAddSpecimen"; //$NON-NLS-1$
    public static final String IMG_SITE = "site"; //$NON-NLS-1$
    public static final String IMG_SITES = "sites"; //$NON-NLS-1$
    public static final String IMG_STUDIES = "studies"; //$NON-NLS-1$
    public static final String IMG_STUDY = "study"; //$NON-NLS-1$
    public static final String IMG_USER_ADD = "userAdd"; //$NON-NLS-1$
    public static final String IMG_EMAIL = "email"; //$NON-NLS-1$
    public static final String IMG_EMAIL_BANNER = "emailBanner"; //$NON-NLS-1$
    public static final String IMG_SEARCH = "search"; //$NON-NLS-1$
    public static final String IMG_TODAY = "today"; //$NON-NLS-1$
    public static final String IMG_CALENDAR = "calendar"; //$NON-NLS-1$
    public static final String IMG_BULLET = "bullet"; //$NON-NLS-1$
    public static final String IMG_SCAN_EDIT = "scanEdit"; //$NON-NLS-1$
    public static final String IMG_SCAN_CLOSE_EDIT = "scanCloseEdit"; //$NON-NLS-1$
    public static final String IMG_RECEIVED = "received"; //$NON-NLS-1$
    public static final String IMG_SENT = "sent"; //$NON-NLS-1$
    public static final String IMG_REQUEST = "request"; //$NON-NLS-1$
    public static final String IMG_REQUEST_EDIT = "request_edit"; //$NON-NLS-1$
    public static final String IMG_REQUEST_SHIPPED = "request_shipped"; //$NON-NLS-1$
    public static final String IMG_REQUEST_FILLED = "request_filled"; //$NON-NLS-1$
    public static final String IMG_SPECIMEN = "specimen"; //$NON-NLS-1$
    public static final String IMG_LOCK = "lock"; //$NON-NLS-1$
    public static final String IMG_UP = "bullet_arrow_up"; //$NON-NLS-1$
    public static final String IMG_DOWN = "bullet_arrow_down"; //$NON-NLS-1$
    public static final String IMG_REMOVE = "remove"; //$NON-NLS-1$
    public static final String IMG_WAND = "wand"; //$NON-NLS-1$
    public static final String IMG_HOURGLASS = "hourglass"; //$NON-NLS-1$
    public static final String IMG_LOGGING = "logging"; //$NON-NLS-1$
    public static final String IMG_PROCESSING = "processing"; //$NON-NLS-1$
    public static final String IMG_SAVE_AS_NEW = "saveAsNew"; //$NON-NLS-1$
    public static final String IMG_PROCESSING_EVENT = "processingEvent"; //$NON-NLS-1$
    public static final String IMG_CHECK = "check"; //$NON-NLS-1$
    public static final String IMG_UNCHECK = "uncheck"; //$NON-NLS-1$

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
        registerImage(registry, IMG_DIALOGS, "dialogs.png"); //$NON-NLS-1$
        registerImage(registry, IMG_ADD, "add.png"); //$NON-NLS-1$
        registerImage(registry, IMG_ARROW_LEFT, "arrow_left.png"); //$NON-NLS-1$
        registerImage(registry, IMG_ARROW_LEFT2, "arrow_left2.png"); //$NON-NLS-1$
        registerImage(registry, IMG_ARROW_RIGHT, "arrow_right.png"); //$NON-NLS-1$
        registerImage(registry, IMG_2_ARROW_LEFT, "2left_arrow.png"); //$NON-NLS-1$
        registerImage(registry, IMG_2_ARROW_RIGHT, "2right_arrow.png"); //$NON-NLS-1$
        registerImage(registry, IMG_BIN, "bin.png"); //$NON-NLS-1$
        registerImage(registry, IMG_BULLET, "bullet.png"); //$NON-NLS-1$
        registerImage(registry, IMG_BOX, "bin.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CABINET, "cabinet.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CABINET_LINK_ASSIGN,
            "cabinetLinkAssign.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CALENDAR, "calendar.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CANCEL_FORM, "cancel.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CLINIC, "clinic.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CLINICS, "clinics.png"); //$NON-NLS-1$
        registerImage(registry, IMG_LOGINWIZ, "loginWiz.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CONFIRM_FORM, "confirm.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CONTAINERS, "containers.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CONTAINER_TYPES, "containerTypes.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DELETE, "delete.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DRAWER, "drawer.png"); //$NON-NLS-1$
        registerImage(registry, IMG_EDIT_FORM, "edit.png"); //$NON-NLS-1$
        registerImage(registry, IMG_FORM_BG, "form_banner.bmp"); //$NON-NLS-1$
        registerImage(registry, IMG_FREEZER, "freezer.png"); //$NON-NLS-1$
        registerImage(registry, IMG_HOTEL, "hotel.png"); //$NON-NLS-1$
        registerImage(registry, IMG_LOGIN, "computer.png"); //$NON-NLS-1$
        registerImage(registry, IMG_LOGOUT, "computer_delete.png"); //$NON-NLS-1$
        registerImage(registry, IMG_MAIN_PERSPECTIVE, "mainPerspective.png"); //$NON-NLS-1$
        registerImage(registry, IMG_PALLET, "pallet.png"); //$NON-NLS-1$
        registerImage(registry, IMG_PATIENT, "patient.png"); //$NON-NLS-1$
        registerImage(registry, IMG_PATIENT_VISIT, "patientVisit.png"); //$NON-NLS-1$
        registerImage(registry, IMG_PRINTER, "printer.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RELOAD_FORM, "reload.png"); //$NON-NLS-1$
        registerImage(registry, IMG_REPORTS, "reports.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RESET_FORM, "reset.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RESULTSET_FIRST, "resultset_first.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RESULTSET_LAST, "resultset_last.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RESULTSET_NEXT, "resultset_next.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RESULTSET_PREV, "resultset_previous.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SCAN_ASSIGN, "scanAssign.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SCAN_LINK, "scanLink.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SCAN_EDIT, "scan_edit.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SCAN_CLOSE_EDIT, "scan_close_edit.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SESSIONS, "sessions.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CLINIC_SHIPMENT, "shipment.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DISPATCH_SHIPMENT, "dispatch.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DISPATCH_SHIPMENT_CREATION,
            "dispatch_creation.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DISPATCH_SHIPMENT_TRANSIT,
            "dispatch_transit.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DISPATCH_SHIPMENT_RECEIVING,
            "dispatch_receiving.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DISPATCH_SHIPMENT_ERROR,
            "dispatch_error.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN,
            "dispatchScanAdd.png"); //$NON-NLS-1$
        registerImage(registry, IMG_REQUEST, "request.png"); //$NON-NLS-1$
        registerImage(registry, IMG_REQUEST_EDIT, "request_edit.png"); //$NON-NLS-1$
        registerImage(registry, IMG_REQUEST_SHIPPED, "request_shipped.png"); //$NON-NLS-1$
        registerImage(registry, IMG_REQUEST_FILLED, "request_filled.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SITE, "site.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SITES, "sites.png"); //$NON-NLS-1$
        registerImage(registry, IMG_STUDIES, "studies.png"); //$NON-NLS-1$
        registerImage(registry, IMG_STUDY, "study.png"); //$NON-NLS-1$
        registerImage(registry, IMG_EMAIL, "email.png"); //$NON-NLS-1$
        registerImage(registry, IMG_EMAIL_BANNER, "email_banner.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SEARCH, "search.png"); //$NON-NLS-1$
        registerImage(registry, IMG_TODAY, "today.png"); //$NON-NLS-1$
        registerImage(registry, IMG_RECEIVED, "received.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SENT, "sent.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SPECIMEN, "specimen.png"); //$NON-NLS-1$
        registerImage(registry, IMG_LOCK, "lock.png"); //$NON-NLS-1$
        registerImage(registry, IMG_UP, "bullet_arrow_up.png"); //$NON-NLS-1$
        registerImage(registry, IMG_DOWN, "bullet_arrow_down.png"); //$NON-NLS-1$
        registerImage(registry, IMG_REMOVE, "remove.png"); //$NON-NLS-1$
        registerImage(registry, IMG_WAND, "wand.png"); //$NON-NLS-1$
        registerImage(registry, IMG_HOURGLASS, "hourglass.png"); //$NON-NLS-1$
        registerImage(registry, IMG_LOGGING, "table_row_delete.png"); //$NON-NLS-1$
        registerImage(registry, IMG_PROCESSING, "processingView.png"); //$NON-NLS-1$
        registerImage(registry, IMG_PROCESSING_EVENT,
            "processingPerspective.png"); //$NON-NLS-1$
        registerImage(registry, IMG_SAVE_AS_NEW, "application_form_add.png"); //$NON-NLS-1$
        registerImage(registry, IMG_CHECK, "checked.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_UNCHECK, "unchecked.gif"); //$NON-NLS-1$
    }

    public void registerImage(ImageRegistry registry, String key,
        String fileName) {
        try {
            IPath path = new Path("icons/" + fileName); //$NON-NLS-1$
            URL url = FileLocator.find(getBundle(), path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
            logger.error("Error registering an image", e); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
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
        if (msg == null && e != null) {
            msg = e.getMessage();
            if (((msg == null) || msg.isEmpty()) && (e.getCause() != null)) {
                msg = e.getCause().getMessage();
            }
        }
        if (msg == null) {
            msg = ""; //$NON-NLS-1$
        }
        if (secondMessage != null) {
            if (!msg.isEmpty()) {
                msg += "\n"; //$NON-NLS-1$
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
            Messages.BgcPlugin_connection_error_title,
            Messages.BgcPlugin_database_error_msg);
        if (ex != null) {
            logger.error(Messages.BgcPlugin_connection_error_title, ex);
        }
    }

    /**
     * Display remote connect error message
     */
    public static void openRemoteConnectErrorMessage(Throwable ex) {
        openAsyncError(Messages.BgcPlugin_connection_error_title,
            Messages.BgcPlugin_connection_error_msg);
        if (ex != null) {
            logger.error(Messages.BgcPlugin_connection_error_title, ex);
        }
    }

    public static void openAccessDeniedErrorMessage() {
        openAccessDeniedErrorMessage(null);
    }

    /**
     * Display remote access error message
     */
    public static void openAccessDeniedErrorMessage(Throwable ex) {
        openAsyncError(Messages.BgcPlugin_access_denied_error_title,
            Messages.BgcPlugin_access_denied_error_msg);
        if (ex != null) {
            logger.error(Messages.BgcPlugin_connection_error_title, ex);
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
