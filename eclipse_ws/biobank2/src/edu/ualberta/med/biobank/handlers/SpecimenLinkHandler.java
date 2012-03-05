package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkEntryForm;
import edu.ualberta.med.biobank.treeview.processing.SpecimenLinkAdapter;

public class SpecimenLinkHandler extends LinkAssignCommonHandler {

    private Boolean linkAllowed;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(SpecimenLinkEntryForm.ID,
            new SpecimenLinkAdapter(null, 0,
                Messages.SpecimenLinkHandler_specimen_link_label,
                false));
        return null;
    }

    @Override
    protected boolean canUserPerformAction(UserWrapper user) {
        if (linkAllowed == null)
            try {
                if (!SessionManager.getInstance().isConnected()
                    || user.getCurrentWorkingSite() == null)
                    return false;
                linkAllowed =
                    SessionManager.getAppService().isAllowed(
                        new SpecimenAssignPermission(user
                            .getCurrentWorkingSite().getId()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return linkAllowed;
    }
}
