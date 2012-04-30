package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenAssignEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.processing.SpecimenAssignAdapter;

public class SpecimenAssignHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (!SpecimenLinkHandler.checkActivityLogSavePathValid()) {
            BgcPlugin.openAsyncError(Messages.SpecimenAssignHandler_log_location,
                Messages.SpecimenAssignHandler_error_message);
            return null;
        }

        openLinkAssignPerspective(SpecimenAssignEntryForm.ID,
            new SpecimenAssignAdapter(null, 0,
                Messages.SpecimenAssignHandler_specimenAssign_label,
                false));
        return null;
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
