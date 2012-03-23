package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.patient.PatientCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientAddHandler extends LogoutSensitiveHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(PatientAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientAdapter adapter = new PatientAdapter(null, null);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error(Messages.PatientAddHandler_patient_open_error, exp);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (createAllowed == null)
                createAllowed =
                    SessionManager.getAppService().isAllowed(
                        new PatientCreatePermission(null));
            return SessionManager.getInstance().getSession() != null &&
                createAllowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(Messages.HandlerPermission_error,
                Messages.HandlerPermission_message);
            return false;
        }
    }
}
