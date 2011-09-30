package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;

public class PatientAddHandler extends AbstractHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(PatientAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            Patient patient = new Patient();
            PatientAdapter adapter = new PatientAdapter(null, patient, null);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error(Messages.PatientAddHandler_patient_open_error, exp);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(PatientWrapper.class);
    }

}
