package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.dialogs.startup.ActivityLogLocationDialog;
import edu.ualberta.med.biobank.handlers.LoginHandler;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;

public class BiobankStartup implements IStartup {

    @Override
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window != null) {
                    boolean logSave = BiobankPlugin
                        .getDefault()
                        .getPreferenceStore()
                        .getBoolean(
                            PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE);
                    String logPath = BiobankPlugin
                        .getDefault()
                        .getPreferenceStore()
                        .getString(
                            PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH);
                    // check if the activity logs path is set...
                    if (logSave && logPath.equals("")) { //$NON-NLS-1$
                        ActivityLogLocationDialog dlg2 = new ActivityLogLocationDialog(
                            window.getShell());
                        dlg2.open();
                    }
                    IHandlerService handlerService = (IHandlerService) window
                        .getService(IHandlerService.class);
                    try {
                        handlerService.executeCommand(LoginHandler.ID, null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
