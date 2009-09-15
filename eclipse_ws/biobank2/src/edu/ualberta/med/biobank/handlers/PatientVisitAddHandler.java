package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientVisitAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientAdapter patientAdapter = PatientAdministrationView
                .getCurrentPatientAdapter();
            PatientVisitAdapter adapter = new PatientVisitAdapter(
                patientAdapter, new PatientVisit());
            adapter.getWrapper().setPatientWrapper(patientAdapter.getWrapper());
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(new FormInput(adapter), PatientVisitEntryForm.ID,
                    true);
        } catch (Exception exp) {
            SessionManager.getLogger().error(
                "Error while opening the patient visit entry form", exp);
        }
        return null;
    }
}