package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ContainerTypeAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerTypeAdd";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager
            .searchNode(SessionManager.getInstance().getCurrentSiteWrapper());
        Assert.isNotNull(siteAdapter);

        ContainerTypeWrapper containerType = new ContainerTypeWrapper(
            SessionManager.getAppService());
        containerType.setSite(siteAdapter.getWrapper());
        ContainerTypeAdapter containerTypeNode = new ContainerTypeAdapter(
            siteAdapter.getContainerTypesGroupNode(), containerType);

        AdapterBase.openForm(new FormInput(containerTypeNode),
            ContainerTypeEntryForm.ID);
        return null;
    }
}
