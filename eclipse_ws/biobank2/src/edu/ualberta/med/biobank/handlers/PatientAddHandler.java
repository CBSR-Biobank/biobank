package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientWrapper patientWrapper = new PatientWrapper(SessionManager
                .getInstance().getSession().getAppService());
            PatientAdapter adapter = new PatientAdapter(
                PatientAdministrationView.getRootNode(), patientWrapper);
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(new FormInput(adapter), PatientEntryForm.ID, true);
        } catch (Exception exp) {
            SessionManager.getLogger().error(
                "Error while opening the patient entry form", exp);
        }
        return null;
    }

}
