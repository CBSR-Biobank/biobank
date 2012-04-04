package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.permission.patient.PatientCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientAddHandler extends LogoutSensitiveHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(PatientAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            SearchedPatientInfo spi = new SearchedPatientInfo();
            spi.patient = new Patient();
            PatientAdapter adapter = new PatientAdapter(null, spi);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error("Error while opening the patient entry form", exp);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new PatientCreatePermission(null));
            return SessionManager.getInstance().getSession() != null &&
                allowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("",
                "");
            return false;
        }
    }
}
