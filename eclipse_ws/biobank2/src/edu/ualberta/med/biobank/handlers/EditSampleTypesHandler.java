package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SampleTypesEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class EditSampleTypesHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.editSampleTypes";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        SiteWrapper siteWrapper = SessionManager.getInstance().getCurrentSite();
        SiteAdapter sa = new SiteAdapter(sessionAdapter, siteWrapper);
        AdapterBase.openForm(new FormInput(sa), SampleTypesEntryForm.ID);
        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.canCreate(SampleTypeWrapper.class) || SessionManager
            .canUpdate(SampleTypeWrapper.class))
            && SessionManager.getInstance().getSession() != null;
    }
}
