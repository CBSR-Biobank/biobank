package edu.ualberta.med.biobank.handlers;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.dialogs.startup.ActivityLogLocationDialog;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkAndAssignForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.treeview.processing.SpecimenLinkAdapter;

public class SpecimenLinkAndAssignHandler extends LinkAssignCommonHandler {
    private static final I18n i18n = I18nFactory.getI18n(SpecimenLinkAndAssignHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (!checkActivityLogSavePathValid()) {
            BgcPlugin.openAsyncError(
                i18n.tr("Activity Log Location"),
                i18n.tr("Invalid path selected. Cannot proceed with link assign."));
            return null;
        }

        openLinkAssignPerspective(SpecimenLinkAndAssignForm.ID,
            new SpecimenLinkAdapter(null, 0, i18n.tr("Specimen Link"), false));
        return null;
    }

    public static boolean checkActivityLogSavePathValid() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        boolean logSave = BiobankPlugin
            .getDefault()
            .getPreferenceStore()
            .getBoolean(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE);

        File dir = new File(BiobankPlugin.getDefault()
            .getPreferenceStore()
            .getString(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH));

        // check if the activity logs path is set...
        if (logSave && (!dir.isDirectory() || !dir.canWrite())) {
            ActivityLogLocationDialog logsDlg =
                new ActivityLogLocationDialog(window.getShell());
            logsDlg.open();
        }

        dir = new File(BiobankPlugin.getDefault().getPreferenceStore()
            .getString(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH));

        return (!logSave || (dir.isDirectory() && dir.canWrite()));
    }
}
