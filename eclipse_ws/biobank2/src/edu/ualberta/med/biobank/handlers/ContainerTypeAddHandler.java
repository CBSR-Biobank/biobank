package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;

public class ContainerTypeAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerTypeAdd";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager
            .searchNode(SessionManager.getCurrentSite());
        Assert.isNotNull(siteAdapter);
        siteAdapter.getContainerTypesGroupNode().addContainerType(siteAdapter,
            false);
        return null;
    }

    @Override
    public boolean isEnabled() {
        boolean can = SessionManager.canCreate(ContainerTypeWrapper.class,
            SessionManager.getCurrentSite());
        return can;
    }
}
