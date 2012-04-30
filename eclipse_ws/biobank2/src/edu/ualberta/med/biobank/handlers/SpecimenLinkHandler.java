package edu.ualberta.med.biobank.handlers;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.dialogs.startup.ActivityLogLocationDialog;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.treeview.processing.SpecimenLinkAdapter;

public class SpecimenLinkHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (!checkActivityLogSavePathValid()) {
            BgcPlugin.openAsyncError(Messages.SpecimenLinkHandler_log_location,
                Messages.SpecimenLinkHandler_error_message);
            return null;
        }

        openLinkAssignPerspective(SpecimenLinkEntryForm.ID,
            new SpecimenLinkAdapter(null, 0,
                Messages.SpecimenLinkHandler_specimen_link_label, false));
        return null;
    }

    public static boolean checkActivityLogSavePathValid() {
        IWorkbenchWindow window =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        boolean logSave =
            BiobankPlugin.getDefault().getPreferenceStore().getBoolean(
                PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE);

        File dir = new File(BiobankPlugin.getDefault().getPreferenceStore()
            .getString(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH));

        // check if the activity logs path is set...
        if (logSave && (!dir.isDirectory() || !dir.canWrite())) {
            ActivityLogLocationDialog logsDlg = new ActivityLogLocationDialog(
                window.getShell());
            logsDlg.open();
        }

        dir = new File(BiobankPlugin.getDefault().getPreferenceStore()
            .getString(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH));

        return (!logSave || (dir.isDirectory() && dir.canWrite()));
    }

    @Override
    protected boolean canUserPerformAction(UserWrapper user) {
        if (allowed == null)
            try {
                if (!SessionManager.getInstance().isConnected()
                    || user.getCurrentWorkingSite() == null)
                    return false;
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new SpecimenAssignPermission(user
                            .getCurrentWorkingSite().getId()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return allowed;
    }
}
