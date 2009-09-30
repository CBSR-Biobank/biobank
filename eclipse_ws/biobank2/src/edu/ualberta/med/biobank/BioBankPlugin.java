package edu.ualberta.med.biobank;

import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
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
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class BioBankPlugin extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "biobank2";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public SimpleDateFormat dateFormatter;

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public SimpleDateFormat dateTimeFormatter;

    public static final String IMG_FORM_BG = "formBg";

    public static final String BARCODES_FILE = BioBankPlugin.class.getPackage()
        .getName()
        + ".barcode";

    static Logger log4j = Logger.getLogger(BioBankPlugin.class.getName());

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
        log4j.debug(PLUGIN_ID + " started");
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        registerImage(registry, IMG_FORM_BG, "form_banner.bmp");
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

        log4j.debug(PLUGIN_ID + " stopped");
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
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static Image getImage(String path) {
        // FIXME should add the image in the registry and create it only once !
        return getImageDescriptor(path).createImage();
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
        if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
            msg = e.getCause().getMessage();
        }
        openError(title, e.getMessage());
        log4j.error(e.getMessage(), e);
    }

    /**
     * Display an info message
     */
    public static void openInformation(String title, String message) {
        MessageDialog.openInformation(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, message);
    }

    /**
     * Display an error message asynchronously
     */
    public static void openAsyncError(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), title, message);
            }
        });
    }

    /**
     * Display remote access error message
     */
    public static void openRemoteAccessErrorMessage() {
        openAsyncError(
            "Connection Attempt Failed",
            "Could not perform database operation. Make sure server is running correct version.");
    }

    /**
     * Display remote connect error message
     */
    public static void openRemoteConnectErrorMessage() {
        openAsyncError("Connection Attempt Failed",
            "Could not connect to server. Make sure server is running.");
    }

    /**
     * Display remote access error message
     */
    public static void openAccessDeniedErrorMessage() {
        openAsyncError("Access Denied",
            "You don't have rights to do this action.");
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
            if (!ScannerConfigPlugin.getDefault().getPalletEnabled(i))
                continue;

            String pref = getPreferenceStore().getString(
                PreferenceConstants.SCANNER_PLATE_BARCODES[i]);
            Assert.isTrue(!pref.isEmpty(), "preference not assigned");
            if (pref.equals(barcode)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isValidPlateBarcode(String value) {
        return !value.isEmpty() && getPlateNumber(value) != -1;
    }

    public static boolean isAskPrint() {
        IPreferenceStore store = getDefault().getPreferenceStore();
        return store.getBoolean(PreferenceConstants.GENERAL_ASK_PRINT);
    }

    public static boolean isRealScanEnabled() {
        String realScan = Platform.getDebugOption(BioBankPlugin.PLUGIN_ID
            + "/realScan");
        if (realScan != null) {
            return Boolean.valueOf(realScan);
        }
        return true;
    }

    public static SimpleDateFormat getDateFormatter() {
        if (getDefault().dateFormatter == null) {
            getDefault().dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        }
        return getDefault().dateFormatter;
    }

    public static SimpleDateFormat getDateTimeFormatter() {
        if (getDefault().dateTimeFormatter == null) {
            getDefault().dateTimeFormatter = new SimpleDateFormat(
                DATE_TIME_FORMAT);
        }
        return getDefault().dateTimeFormatter;
    }

}
