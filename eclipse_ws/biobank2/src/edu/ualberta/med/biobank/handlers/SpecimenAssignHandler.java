package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.SessionSecurityHelper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenAssignEntryForm;
import edu.ualberta.med.biobank.treeview.processing.SpecimenAssignAdapter;

public class SpecimenAssignHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(SpecimenAssignEntryForm.ID,
            new SpecimenAssignAdapter(null, 0,
                Messages.SpecimenAssignHandler_specimenAssign_label,
                false));
        return null;
    }

    @Override
    protected boolean canUserPerformAction(UserWrapper user) {
        try {
            return user.getCurrentWorkingSite() != null
                && SessionManager
                    .isAllowed(SessionSecurityHelper.SPECIMEN_ASSIGN_KEY_DESC);
        } catch (Exception ae) {
            throw new RuntimeException(ae);
        }
    }
}
