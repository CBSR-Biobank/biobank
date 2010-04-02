package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.PatientAdapter;

public class PatientAddHandler extends AbstractHandler {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientWrapper patientWrapper = new PatientWrapper(SessionManager
                .getInstance().getSession().getAppService());
            PatientAdapter adapter = new PatientAdapter(null, patientWrapper);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error("Error while opening the patient entry form", exp);
        }
        return null;
    }

}
