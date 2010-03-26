package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientVisitAddHandler extends AbstractHandler {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientVisitAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientAdapter patientAdapter = PatientAdministrationView
                .getCurrentPatientAdapter();
            PatientVisitWrapper pvWrapper = new PatientVisitWrapper(
                SessionManager.getAppService());
            PatientVisitAdapter adapter = new PatientVisitAdapter(
                patientAdapter, pvWrapper);
            adapter.getWrapper().setPatient(patientAdapter.getWrapper());
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error("Error while opening the patient visit entry form",
                exp);
        }
        return null;
    }
}