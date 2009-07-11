
package edu.ualberta.med.biobank;

import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.ualberta.med.biobank.preferences.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class BioBankPlugin extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "biobank2";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String IMG_FORM_BG = "formBg";

    public static final String BARCODES_FILE = BioBankPlugin.class.getPackage().getName()
        + ".barcode";

    static Logger log4j = Logger.getLogger(BioBankPlugin.class.getName());

    // The shared instance
    private static BioBankPlugin plugin;

    /**
     * The constructor
     */
    public BioBankPlugin() {
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")) {
            System.loadLibrary("scanlib");
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
        }
        catch (Exception e) {}
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
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Display an information message
     */
    public static void openMessage(String title, String message) {
        MessageDialog.openInformation(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            title, message);
    }

    /**
     * Display an error message
     */
    public static void openError(String title, String message) {
        MessageDialog.openError(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            title, message);
    }

    /**
     * Display an error message asynchronously
     */
    public static void openAsyncError(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    title, message);
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

    public boolean isCancelBarcode(String code) {
        return getPreferenceStore().getString(
            PreferenceConstants.GENERAL_CANCEL).equals(code);
    }

    public boolean isConfirmBarcode(String code) {
        return getPreferenceStore().getString(
            PreferenceConstants.GENERAL_CONFIRM).equals(code);
    }

    public int getPlateNumber(String barcode) {
        for (int i = 1; i <= PreferenceConstants.SCANNER_PLATE_NUMBER; i++) {
            String pref = getPreferenceStore().getString(
                PreferenceConstants.SCANNER_PLATE + i);
            if (pref.isEmpty()) {
                // should no be empty
                return -1;
            }
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

}
