package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.forms.ScanLinkEntryForm;

public class ScanLinkHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(ScanLinkEntryForm.ID);
        return null;
    }

    @Override
    protected boolean canUserPerformAction(User user) {
        return user.canPerformActions(SecurityFeature.LINK);
    }
}
