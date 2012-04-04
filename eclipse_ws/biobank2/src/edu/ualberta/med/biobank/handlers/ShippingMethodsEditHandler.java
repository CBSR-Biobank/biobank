package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.GlobalAdminPermission;
import edu.ualberta.med.biobank.forms.ShippingMethodViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShippingMethodsEditHandler extends LogoutSensitiveHandler {
    public static final String ID =
        "edu.ualberta.med.biobank.commands.editShipmentMethods"; 

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
                    ShippingMethodViewForm.ID, false, 0);
        } catch (Exception e) {
            throw new ExecutionException(
                "Could not execute handler.", e);
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed = SessionManager.getAppService().isAllowed(
                    new GlobalAdminPermission());
            return (allowed
            && (SessionManager.getInstance().getSession() != null));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
            return false;
        }
    }
}
