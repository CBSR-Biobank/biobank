package edu.ualberta.med.biobank;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.AliquotAdapter;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.ContainerGroup;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.ContainerTypeGroup;
import edu.ualberta.med.biobank.treeview.DateNode;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.SiteGroup;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.treeview.clinicShipment.ClinicShipmentAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.InCreationDispatchShipmentGroup;
import edu.ualberta.med.biobank.treeview.dispatch.IncomingNode;
import edu.ualberta.med.biobank.treeview.dispatch.OutgoingNode;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingDispatchShipmentGroup;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingInTransitDispatchShipmentGroup;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingWithErrorsDispatchShipmentGroup;
import edu.ualberta.med.biobank.treeview.dispatch.SentInTransitDispatchShipmentGroup;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientVisitAdapter;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class BioBankPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "biobank2";

    public static final String IMAGE_ID = "biobank2.image";

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
    public static final String IMG_COMPUTER_KEY = "computerKey";
    public static final String IMG_CONFIRM_FORM = "confirmForm";
    public static final String IMG_CONTAINERS = "containers";
    public static final String IMG_CONTAINER_TYPES = "containerTypes";
    public static final String IMG_DELETE = "delete";
    public static final String IMG_DRAWER = "drawer";
    public static final String IMG_EDIT_FORM = "editForm";
    public static final String IMG_FORM_BG = "formBg";
    public static final String IMG_FREEZER = "freezer";
    public static final String IMG_HOTEL = "hotel";
    public static final String IMG_LOGIN = "login";
    public static final String IMG_LOGOUT = "logout";
    public static final String IMG_MAIN_PERSPECTIVE = "mainPerspective";
    public static final String IMG_PALLET = "pallet";
    public static final String IMG_PATIENT = "patient";
    public static final String IMG_PATIENT_VISIT = "patientVisit";
    public static final String IMG_PRINTER = "patientVisit";
    public static final String IMG_RELOAD_FORM = "reloadForm";
    public static final String IMG_REPORTS = "reports";
    public static final String IMG_RESET_FORM = "resetForm";
    public static final String IMG_RESULTSET_FIRST = "resultsetFirst";
    public static final String IMG_RESULTSET_LAST = "resultsetLast";
    public static final String IMG_RESULTSET_NEXT = "resultsetNext";
    public static final String IMG_RESULTSET_PREV = "resultsetPrev";
    public static final String IMG_SCAN_ASSIGN = "scanAssign";
    public static final String IMG_SCAN_LINK = "scanLink";
    public static final String IMG_SESSIONS = "sessions";
    public static final String IMG_CLINIC_SHIPMENT = "clinicShipment";
    public static final String IMG_DISPATCH_SHIPMENT = "dispatchShipment";
    public static final String IMG_DISPATCH_SHIPMENT_CREATION = "dispatchShipmentCreation";
    public static final String IMG_DISPATCH_SHIPMENT_TRANSIT = "dispatchShipmentTransit";
    public static final String IMG_DISPATCH_SHIPMENT_RECEIVING = "dispatchShipmentReceiving";
    public static final String IMG_DISPATCH_SHIPMENT_ERROR = "dispatchShipmentError";
    public static final String IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT = "dispatchShipmentAddAliquot";
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
    public static final String IMG_ALIQUOT = "aliquot";

    //
    // ContainerTypeAdapter and Container missing on purpose.
    //
    private static Map<String, String> classToImageKey;
    static {
        classToImageKey = new HashMap<String, String>();
        classToImageKey.put(SessionAdapter.class.getName(),
            BioBankPlugin.IMG_SESSIONS);
        classToImageKey
            .put(SiteAdapter.class.getName(), BioBankPlugin.IMG_SITE);
        classToImageKey.put(SiteGroup.class.getName(), BioBankPlugin.IMG_SITES);
        classToImageKey.put(AbstractClinicGroup.class.getName(),
            BioBankPlugin.IMG_CLINICS);
        classToImageKey.put(AbstractStudyGroup.class.getName(),
            BioBankPlugin.IMG_STUDIES);
        classToImageKey.put(ContainerTypeGroup.class.getName(),
            BioBankPlugin.IMG_CONTAINER_TYPES);
        classToImageKey.put(ContainerGroup.class.getName(),
            BioBankPlugin.IMG_CONTAINERS);
        classToImageKey.put(ClinicAdapter.class.getName(),
            BioBankPlugin.IMG_CLINIC);
        classToImageKey.put(StudyAdapter.class.getName(),
            BioBankPlugin.IMG_STUDY);
        classToImageKey.put(PatientAdapter.class.getName(),
            BioBankPlugin.IMG_PATIENT);
        classToImageKey.put(PatientVisitAdapter.class.getName(),
            BioBankPlugin.IMG_PATIENT_VISIT);
        classToImageKey.put(ClinicShipmentAdapter.class.getName(),
            BioBankPlugin.IMG_CLINIC_SHIPMENT);
        classToImageKey.put(AbstractSearchedNode.class.getName(),
            BioBankPlugin.IMG_SEARCH);
        classToImageKey.put(AbstractTodayNode.class.getName(),
            BioBankPlugin.IMG_TODAY);
        classToImageKey.put(DateNode.class.getName(),
            BioBankPlugin.IMG_CALENDAR);
        classToImageKey.put(OutgoingNode.class.getName(),
            BioBankPlugin.IMG_SENT);
        classToImageKey.put(IncomingNode.class.getName(),
            BioBankPlugin.IMG_RECEIVED);
        classToImageKey.put(InCreationDispatchShipmentGroup.class.getName(),
            BioBankPlugin.IMG_DISPATCH_SHIPMENT_CREATION);
        classToImageKey.put(
            ReceivingInTransitDispatchShipmentGroup.class.getName(),
            BioBankPlugin.IMG_DISPATCH_SHIPMENT_TRANSIT);
        classToImageKey.put(SentInTransitDispatchShipmentGroup.class.getName(),
            BioBankPlugin.IMG_DISPATCH_SHIPMENT_TRANSIT);
        classToImageKey.put(ReceivingDispatchShipmentGroup.class.getName(),
            BioBankPlugin.IMG_DISPATCH_SHIPMENT_RECEIVING);
        classToImageKey.put(
            ReceivingWithErrorsDispatchShipmentGroup.class.getName(),
            BioBankPlugin.IMG_DISPATCH_SHIPMENT_ERROR);
        classToImageKey.put(DispatchShipmentAdapter.class.getName(),
            BioBankPlugin.IMG_DISPATCH_SHIPMENT);
        classToImageKey.put(AliquotAdapter.class.getName(),
            BioBankPlugin.IMG_ALIQUOT);
    };

    private static final String[] CONTAINER_TYPE_IMAGE_KEYS = new String[] {
        BioBankPlugin.IMG_BIN, BioBankPlugin.IMG_BOX,
        BioBankPlugin.IMG_CABINET, BioBankPlugin.IMG_DRAWER,
        BioBankPlugin.IMG_FREEZER, BioBankPlugin.IMG_HOTEL,
        BioBankPlugin.IMG_PALLET, };

    public static final String BARCODES_FILE = BioBankPlugin.class.getPackage()
        .getName() + ".barcode";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BioBankPlugin.class.getName());

    // The shared instance
    private static BioBankPlugin plugin;

    /**
     * The constructor
     */
    public BioBankPlugin() {
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
        SessionManager.getInstance();
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
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
        registerImage(registry, IMG_COMPUTER_KEY, "computer_key.png");
        registerImage(registry, IMG_CONFIRM_FORM, "confirm.png");
        registerImage(registry, IMG_CONTAINERS, "containers.png");
        registerImage(registry, IMG_CONTAINER_TYPES, "containerTypes.png");
        registerImage(registry, IMG_DELETE, "delete.png");
        registerImage(registry, IMG_DRAWER, "drawer.png");
        registerImage(registry, IMG_EDIT_FORM, "edit.png");
        registerImage(registry, IMG_FORM_BG, "form_banner.bmp");
        registerImage(registry, IMG_FREEZER, "freezer.png");
        registerImage(registry, IMG_HOTEL, "hotel.png");
        registerImage(registry, IMG_LOGIN, "computer.png");
        registerImage(registry, IMG_LOGOUT, "computer_delete.png");
        registerImage(registry, IMG_MAIN_PERSPECTIVE, "mainPerspective.png");
        registerImage(registry, IMG_PALLET, "pallet.png");
        registerImage(registry, IMG_PATIENT, "patient.png");
        registerImage(registry, IMG_PATIENT_VISIT, "patientVisit.png");
        registerImage(registry, IMG_PRINTER, "printer.png");
        registerImage(registry, IMG_RELOAD_FORM, "reload.png");
        registerImage(registry, IMG_REPORTS, "reports.png");
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
        registerImage(registry, IMG_CLINIC_SHIPMENT, "clinicShipment.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT, "dispatchShipment.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_CREATION,
            "dispatchShipment_creation.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_TRANSIT,
            "dispatchShipment_transit.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_RECEIVING,
            "dispatchShipment_receiving.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_ERROR,
            "dispatchShipment_error.png");
        registerImage(registry, IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT,
            "dispatchScanAdd.png");
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
        registerImage(registry, IMG_ALIQUOT, "aliquot.png");
    }

    private void registerImage(ImageRegistry registry, String key,
        String fileName) {
        try {
            IPath path = new Path("icons/" + fileName);
            URL url = FileLocator.find(getBundle(), path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
        }
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
    public static BioBankPlugin getDefault() {
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
        MessageDialog.openError(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, message);
    }

    /**
     * Display an error message with exception message and log the exception
     */
    public static void openError(String title, Exception e) {
        String msg = e.getMessage();
        if (((msg == null) || msg.isEmpty()) && (e.getCause() != null)) {
            msg = e.getCause().getMessage();
        }
        openError(title, e.getMessage());
        logger.error(title, e);
    }

    public static void openAsyncError(String title, Exception e,
        String secondMessage) {
        String msg = e.getMessage();
        if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
            msg = e.getCause().getMessage();
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
        openAsyncError(title, msg);
        logger.error(title, e);
    }

    public static void openAsyncError(String title, Exception e) {
        openAsyncError(title, e, null);
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
     * Display an error message asynchronously
     */
    public static void openAsyncError(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(PlatformUI.getWorkbench()
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

    public boolean isCancelBarcode(String code) {
        return getPreferenceStore().getString(
            PreferenceConstants.GENERAL_CANCEL).equals(code);
    }

    public boolean isConfirmBarcode(String code) {
        return getPreferenceStore().getString(
            PreferenceConstants.GENERAL_CONFIRM).equals(code);
    }

    public int getPlateNumber(String barcode) {
        for (int i = 0; i < PreferenceConstants.SCANNER_PLATE_BARCODES.length; i++) {
            if (isRealScanEnabled()
                && !ScannerConfigPlugin.getDefault().getPlateEnabled(i + 1))
                continue;

            String pref = getPreferenceStore().getString(
                PreferenceConstants.SCANNER_PLATE_BARCODES[i]);
            Assert.isTrue(!pref.isEmpty(), "preference not assigned");
            if (pref.equals(barcode)) {
                return i + 1;
            }
        }
        return -1;
    }

    public static int getPlatesEnabledCount() {
        int count = 0;
        for (int i = 0; i < PreferenceConstants.SCANNER_PLATE_BARCODES.length; i++) {
            if (!isRealScanEnabled()
                || ScannerConfigPlugin.getDefault().getPlateEnabled(i + 1))
                count++;
        }
        return count;
    }

    public boolean isValidPlateBarcode(String value) {
        return (!value.isEmpty() && (getPlateNumber(value) != -1));
    }

    public static String getActivityLogPath() {
        IPreferenceStore store = getDefault().getPreferenceStore();
        boolean logToFile = store
            .getBoolean(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE);
        if (logToFile) {
            return store
                .getString(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH);
        }
        return null;
    }

    public static boolean isAskPrintActivityLog() {
        IPreferenceStore store = getDefault().getPreferenceStore();
        return store
            .getBoolean(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_ASK_PRINT);
    }

    public static boolean isRealScanEnabled() {
        String realScan = Platform.getDebugOption(BioBankPlugin.PLUGIN_ID
            + "/realScan");
        if (realScan != null) {
            return Boolean.valueOf(realScan);
        }
        return true;
    }

    public Image getImage(Object object) {
        String imageKey = null;
        if (object == null)
            return null;
        if (object instanceof AdapterBase) {
            Class<?> objectClass = object.getClass();
            while (imageKey == null && !objectClass.equals(AdapterBase.class)) {
                imageKey = classToImageKey.get(objectClass.getName());
                objectClass = objectClass.getSuperclass();
            }
            if ((imageKey == null)
                && ((object instanceof ContainerAdapter) || (object instanceof ContainerTypeAdapter))) {
                String ctName;
                if (object instanceof ContainerAdapter) {
                    ContainerWrapper container = ((ContainerAdapter) object)
                        .getContainer();
                    if (container == null
                        || container.getContainerType() == null)
                        return null;
                    ctName = container.getContainerType().getName();
                } else {
                    ctName = ((ContainerTypeAdapter) object).getLabel();
                }
                return getIconForTypeName(ctName);
            }
        } else {
            if (object instanceof String) {
                imageKey = (String) object;
            }
        }
        return BioBankPlugin.getDefault().getImageRegistry().get(imageKey);
    }

    public static ImageDescriptor getImageDescriptor(String key) {
        return BioBankPlugin.getDefault().getImageRegistry().getDescriptor(key);
    }

    private Image getIconForTypeName(String typeName) {
        if (typeName == null) {
            return null;
        }
        if (classToImageKey.containsKey(typeName)) {
            return BioBankPlugin.getDefault().getImageRegistry()
                .get(classToImageKey.get(typeName));
        }

        String imageKey = null;
        for (String name : CONTAINER_TYPE_IMAGE_KEYS) {
            if (typeName.toLowerCase().contains(name)) {
                imageKey = name;
                break;
            }
        }

        if (imageKey == null)
            imageKey = BioBankPlugin.IMG_FREEZER;

        classToImageKey.put(typeName, imageKey);
        return BioBankPlugin.getDefault().getImageRegistry().get(imageKey);
    }

}
