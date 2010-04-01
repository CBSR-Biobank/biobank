package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class ReloadTodayPatientHandler extends AbstractHandler implements
    IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        PatientAdministrationView.currentInstance.reloadTodayPatients();
        return null;
    }
}
