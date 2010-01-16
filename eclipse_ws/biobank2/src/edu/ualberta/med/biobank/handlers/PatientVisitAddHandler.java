package edu.ualberta.med.biobank.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientVisitAddHandler extends AbstractHandler {

    private static Logger LOGGER = Logger
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
            AdapterBase.openForm(new FormInput(adapter),
                PatientVisitEntryForm.ID);
        } catch (Exception exp) {
            LOGGER.error("Error while opening the patient visit entry form",
                exp);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(PatientVisitWrapper.class);
    }
}