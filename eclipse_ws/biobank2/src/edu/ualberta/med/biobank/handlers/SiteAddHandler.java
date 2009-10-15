package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SiteAddHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);

        SiteWrapper site = new SiteWrapper(SessionManager.getAppService(),
            new Site());
        SiteAdapter siteNode = new SiteAdapter(sessionAdapter, site);

        FormInput input = new FormInput(siteNode);
        try {
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, SiteEntryForm.ID, true);
        } catch (Exception exp) {
            throw new ExecutionException("Error opening form "
                + SiteEntryForm.ID, exp);
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}
