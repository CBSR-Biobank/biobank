package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchAddHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(DispatchAddHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        DispatchWrapper dispatch = new DispatchWrapper(
            SessionManager.getAppService());
        DispatchAdapter node = new DispatchAdapter(sessionAdapter, dispatch);
        node.openEntryForm();
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed = SessionManager.getAppService().isAllowed(
                    new
                    DispatchCreatePermission(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Error"),
                // dialog message
                i18n.tr("Unable to retrieve permissions"));
            return false;
        }
        return allowed;
    }

}