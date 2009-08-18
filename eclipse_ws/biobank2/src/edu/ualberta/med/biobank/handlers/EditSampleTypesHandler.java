package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.SampleTypesEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.SessionAdapter;

public class EditSampleTypesHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.editSampleTypes";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSessionAdapter();
        Assert.isNotNull(sessionAdapter);

        try {
            // sessionAdapter.getSiteAdapter();
            FormInput input = new FormInput(sessionAdapter);
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, SampleTypesEntryForm.ID, true);
        } catch (PartInitException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}
