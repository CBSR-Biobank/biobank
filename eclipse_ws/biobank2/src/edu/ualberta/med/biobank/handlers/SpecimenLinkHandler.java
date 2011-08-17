package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkEntryForm;
import edu.ualberta.med.biobank.treeview.processing.SpecimenLinkAdapter;

public class SpecimenLinkHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(SpecimenLinkEntryForm.ID,
            new SpecimenLinkAdapter(SessionManager.getInstance().getSession(),
                0, Messages.SpecimenLinkHandler_specimen_link_label, false,
                false));
        return null;
    }

    @Override
    protected boolean canUserPerformAction(UserWrapper user) {
        return user.canPerformActions(SecurityFeature.LINK);
    }
}
