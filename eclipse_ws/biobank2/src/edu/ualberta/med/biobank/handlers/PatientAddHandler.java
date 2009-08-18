package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientAdapter adapter = new PatientAdapter(
                PatientAdministrationView.getRootNode(), new Patient());
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(new FormInput(adapter), PatientEntryForm.ID, true);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }

}
