package edu.ualberta.med.biobank.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.SiteUtils;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ContainerAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerAdd";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        List<ContainerType> top = (List<ContainerType>) SiteUtils
            .getTopContainerTypesInSite(SessionManager.getAppService(),
                SessionManager.getInstance().getCurrentSiteWrapper());
        if (top.size() == 0) {
            MessageDialog
                .openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getShell(), "Unable to create container",
                    "You must define a top-level container type before initializing storage.");
            return null;
        }

        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        SiteAdapter siteAdapter = (SiteAdapter) sessionAdapter
            .accept(new NodeSearchVisitor(Site.class, SessionManager
                .getInstance().getCurrentSiteWrapper().getId()));
        Assert.isNotNull(siteAdapter);

        ContainerWrapper containerWrapper = new ContainerWrapper(SessionManager
            .getAppService(), new Container());
        containerWrapper.setSite(siteAdapter.getWrapper());
        ContainerAdapter containerNode = new ContainerAdapter(siteAdapter
            .getContainerTypesGroupNode(), containerWrapper);

        FormInput input = new FormInput(containerNode);
        try {
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, ContainerEntryForm.ID, true);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }
}
