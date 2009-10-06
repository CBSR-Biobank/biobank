package edu.ualberta.med.biobank.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ContainerAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerAdd";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            Collection<ContainerTypeWrapper> top = ContainerTypeWrapper
                .getTopContainerTypesInSite(SessionManager.getAppService(),
                    SessionManager.getInstance().getCurrentSiteWrapper());
            if (top.size() == 0) {
                MessageDialog
                    .openError(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Unable to create container",
                        "You must define a top-level container type before initializing storage.");
                return null;
            }

            SiteAdapter siteAdapter = (SiteAdapter) SessionManager
                .getInstance().searchNode(
                    SessionManager.getInstance().getCurrentSiteWrapper());
            Assert.isNotNull(siteAdapter);

            ContainerWrapper containerWrapper = new ContainerWrapper(
                SessionManager.getAppService());
            containerWrapper.setSite(siteAdapter.getWrapper());
            ContainerAdapter containerNode = new ContainerAdapter(siteAdapter
                .getContainerTypesGroupNode(), containerWrapper);

            FormInput input = new FormInput(containerNode);
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, ContainerEntryForm.ID, true);
        } catch (Exception e) {
            throw new ExecutionException("Error on action Add Container", e);
        }
        return null;
    }
}
