package edu.ualberta.med.biobank.gui.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BiobankGuiCommonPlugin extends AbstractUIPlugin {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BiobankGuiCommonPlugin.class.getName());

    // The plug-in ID
    public static final String PLUGIN_ID = "biobank.gui.common"; //$NON-NLS-1$

    // The shared instance
    private static BiobankGuiCommonPlugin plugin;

    /**
     * The constructor
     */
    public BiobankGuiCommonPlugin() {
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
    public static BiobankGuiCommonPlugin getDefault() {
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

}
