package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicAddHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(ClinicAddHandler.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.commands.addClinic";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter session = SessionManager.getInstance().getSession();
        Assert.isNotNull(session);
        session.addClinic();
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new ClinicCreatePermission());
            return SessionManager.isSuperAdminMode()
                && SessionManager.getInstance().getSession() != null &&
                allowed;
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
