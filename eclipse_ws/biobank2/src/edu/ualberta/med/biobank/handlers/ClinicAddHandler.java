package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ClinicAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addClinic";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager.getInstance()
            .searchNode(SessionManager.getInstance().getCurrentSiteWrapper());
        Assert.isNotNull(siteAdapter);

        Clinic clinic = new Clinic();
        clinic.setAddress(new Address());
        clinic.setSite(siteAdapter.getSite());
        ClinicAdapter clinicNode = new ClinicAdapter(siteAdapter
            .getClinicGroupNode(), new ClinicWrapper(siteAdapter
            .getAppService(), clinic));
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
        return (SessionManager.getInstance().getSession() != null);
    }
}
