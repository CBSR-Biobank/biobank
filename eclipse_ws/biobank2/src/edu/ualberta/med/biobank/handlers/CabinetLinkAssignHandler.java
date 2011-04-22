package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm;
import edu.ualberta.med.biobank.treeview.CabinetLinkAssignAdapter;

public class CabinetLinkAssignHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(CabinetLinkAssignEntryForm.ID,
            new CabinetLinkAssignAdapter(SessionManager.getInstance()
                .getSession(), 0, "Cabinet Link/Assign", false, false));
        return null;
    }

    @Override
    protected boolean canUserPerformAction(User user) {
        return user.getCurrentWorkingSite() != null
            && user.canPerformActions(SecurityFeature.ASSIGN,
                SecurityFeature.LINK);
    }
}
