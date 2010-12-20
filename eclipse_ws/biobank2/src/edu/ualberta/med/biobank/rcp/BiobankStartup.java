package edu.ualberta.med.biobank.rcp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.dialogs.startup.ActivityLogLocationDialog;
import edu.ualberta.med.biobank.dialogs.startup.LoginDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;

public class BiobankStartup implements IStartup {

    private static final String ABOUT_MAPPINGS_FILE = "about.mappings";

    private static final String ABOUT_MAPPINGS_VERSION_ENTRY = "0";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BiobankStartup.class.getName());

    @Override
    public void earlyStartup() {
        try {
            // check the about version is the same that the application.
            // the file is modified is the version is not correct
            // The result is displayed on the Help > About dialog

            String manifestVersion = Platform.getProduct().getDefiningBundle()
                .getVersion().toString();

            // FIXME this doesn't work when the application is exported into a
            // final product.
            // to retrieve a file, a temporary folder is created to unzip the
            // file. The modification of this file doesn't affect the real file
            // used for the about dialog
            URL urlAbout = FileLocator.find(BioBankPlugin.getDefault()
                .getBundle(), new Path(ABOUT_MAPPINGS_FILE), null);
            String fileAbout = FileLocator.toFileURL(urlAbout).getFile();
            // Read properties file.
            Properties properties = new Properties();
            properties.load(new FileInputStream(fileAbout));
            String aboutVersion = properties
                .getProperty(ABOUT_MAPPINGS_VERSION_ENTRY);
            if (!manifestVersion.equals(aboutVersion)) {
                properties.setProperty(ABOUT_MAPPINGS_VERSION_ENTRY,
                    manifestVersion);
                properties.store(new FileOutputStream(fileAbout), null);
            }
            final IWorkbench workbench = PlatformUI.getWorkbench();
            workbench.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    IWorkbenchWindow window = workbench
                        .getActiveWorkbenchWindow();
                    if (window != null) {

                        boolean logSave = BioBankPlugin
                            .getDefault()
                            .getPreferenceStore()
                            .getBoolean(
                                PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE);
                        String logPath = BioBankPlugin
                            .getDefault()
                            .getPreferenceStore()
                            .getString(
                                PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH);

                        if (logSave && logPath.equals("")) {
                            ActivityLogLocationDialog dlg2 = new ActivityLogLocationDialog(
                                window.getShell());
                            dlg2.open();
                        }
                        LoginDialog dlg = new LoginDialog(window.getShell());
                        dlg.open();
                    }
                }
            });

        } catch (Exception e) {
            logger.debug("Error while checking and/or modifying the "
                + ABOUT_MAPPINGS_FILE + " file.", e);
        }
    }
}
