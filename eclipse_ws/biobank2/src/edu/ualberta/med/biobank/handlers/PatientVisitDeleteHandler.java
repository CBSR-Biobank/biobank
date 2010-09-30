package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.treeview.patient.PatientVisitAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientVisitDeleteHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Object pvAdapter = PatientAdministrationView.getCurrent()
            .getSelectedNode();
        if (pvAdapter instanceof PatientVisitAdapter) {
            try {
                ((PatientVisitAdapter) pvAdapter)
                    .delete("Are you sure you want to delete this patient visit?");
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Delete Failed", e);
            }
        }
        return null;
    }
}