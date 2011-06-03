package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkEntryForm;
import edu.ualberta.med.biobank.treeview.processing.AssignAdapter;

public class SpecimenLinkHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(SpecimenLinkEntryForm.ID, new AssignAdapter(
            SessionManager.getInstance().getSession(), 0, "Specimen Link",
            false, false));
        return null;
    }

    @Override
    protected boolean canUserPerformAction(User user) {
        return user.canPerformActions(SecurityFeature.LINK);
    }
}
