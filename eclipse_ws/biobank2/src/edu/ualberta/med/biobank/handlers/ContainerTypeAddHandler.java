package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ContainerTypeAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerTypeAdd";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager.getInstance()
            .searchNode(SessionManager.getInstance().getCurrentSiteWrapper());
        Assert.isNotNull(siteAdapter);

        ContainerTypeWrapper containerType = new ContainerTypeWrapper(
            SessionManager.getAppService());
        containerType.setSite(siteAdapter.getWrapper());
        ContainerTypeAdapter containerTypeNode = new ContainerTypeAdapter(
            siteAdapter.getContainerTypesGroupNode(), containerType);

        FormInput input = new FormInput(containerTypeNode);
        try {
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, ContainerTypeEntryForm.ID, true);
        } catch (Exception exp) {
            throw new ExecutionException("Error opening form "
                + ContainerTypeEntryForm.ID, exp);
        }
        return null;
    }
}
