package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientDeleteHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        PatientAdapter patAdapter = PatientAdministrationView
            .getCurrentPatient();
        try {
            patAdapter.delete("Are you sure you want to delete this patient?");
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Delete Failed", e);
        }
        return null;
    }
}