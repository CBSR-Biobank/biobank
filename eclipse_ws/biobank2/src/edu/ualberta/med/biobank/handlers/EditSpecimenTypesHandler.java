package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeCreatePermission;
import edu.ualberta.med.biobank.forms.SpecimenTypesViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class EditSpecimenTypesHandler extends AbstractHandler {
    public static final String ID =
        "edu.ualberta.med.biobank.commands.editSpecimenTypes"; //$NON-NLS-1$
    private Boolean createAllowed;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        try {
            PlatformUI
                .getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage()
                .openEditor(new FormInput(sessionAdapter),
                    SpecimenTypesViewForm.ID, false, 0);
        } catch (Exception e) {
            throw new ExecutionException(
                Messages.EditSpecimenTypesHandler_handler_error_msg, e);
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (createAllowed == null)
                createAllowed = SessionManager.getAppService().isAllowed(
                    new SpecimenTypeCreatePermission());
            return SessionManager.getUser().isInSuperAdminMode()
                && createAllowed
                && (SessionManager.getInstance().getSession() != null);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
            return false;
        }
    }
}
