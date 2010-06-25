package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ClinicAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addClinic";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager
            .searchNode(SessionManager.getInstance().getCurrentSite());
        Assert.isNotNull(siteAdapter);

        ClinicWrapper clinic = new ClinicWrapper(siteAdapter.getAppService());
        clinic.setSite(siteAdapter.getWrapper());
        ClinicAdapter clinicNode = new ClinicAdapter(siteAdapter
            .getClinicGroupNode(), clinic);
        clinicNode.openEntryForm();

        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(ClinicWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }
}
