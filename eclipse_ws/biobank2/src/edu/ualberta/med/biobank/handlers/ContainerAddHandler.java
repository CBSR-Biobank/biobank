package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ContainerAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.containerAdd";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        SiteAdapter siteAdapter = (SiteAdapter) sessionAdapter
            .accept(new NodeSearchVisitor(Site.class, SessionManager
                .getInstance().getCurrentSite().getId()));
        Assert.isNotNull(siteAdapter);

        Container container = new Container();
        ContainerAdapter containerNode = new ContainerAdapter(siteAdapter
            .getContainerTypesGroupNode(), container);

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
