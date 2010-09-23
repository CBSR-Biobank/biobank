package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ContainerAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerAdd";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager
            .searchNode(SessionManager.getInstance().getCurrentSite());
        Assert.isNotNull(siteAdapter);
        siteAdapter.getContainersGroupNode().addContainer(siteAdapter, false);
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(ContainerWrapper.class)
            && SessionManager.getUser().isContainerAdministrator();
    }
}
