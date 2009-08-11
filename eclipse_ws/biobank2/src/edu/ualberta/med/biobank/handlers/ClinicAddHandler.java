package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SessionAdapter;

public class ClinicAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addClinic";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);

        Clinic clinic = new Clinic();
        clinic.setAddress(new Address());
        ClinicAdapter clinicNode = new ClinicAdapter(sessionAdapter, clinic);
        FormInput input = new FormInput(clinicNode);

        try {
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, ClinicEntryForm.ID, true);
        } catch (PartInitException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
