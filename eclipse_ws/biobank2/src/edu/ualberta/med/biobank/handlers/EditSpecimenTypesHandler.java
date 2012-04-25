package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeCreatePermission;
import edu.ualberta.med.biobank.forms.SpecimenTypesViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class EditSpecimenTypesHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(EditSpecimenTypesHandler.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.commands.editSpecimenTypes";

    @SuppressWarnings("nls")
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
                // exception message
                i18n.tr("Could not execute handler."), e);
        }

        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed = SessionManager.getAppService().isAllowed(
                    new SpecimenTypeCreatePermission());
            return SessionManager.getUser().isInSuperAdminMode()
                && allowed
                && (SessionManager.getInstance().getSession() != null);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Error"),
                // dialog message
                i18n.tr("Unable to retrieve permissions"));
            return false;
        }
    }
}
