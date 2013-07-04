package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.batchop.CeventAttrImportForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;

public class CeventAttrImportHandler extends AbstractHandler {
    private static final I18n i18n = I18nFactory.getI18n(CeventAttrImportHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance().getSession();
        Assert.isNotNull(sessionAdapter);
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                new FormInput(sessionAdapter), CeventAttrImportForm.ID, false, 0);
        } catch (Exception e) {
            throw new ExecutionException(
                // exception message
                i18n.tr("Could not execute handler."), e);
        }

        return null;
    }
}
