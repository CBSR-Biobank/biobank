package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class DeleteSelectionHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AdapterBase adapter = SessionManager.getSelectedNode();
        Assert.isNotNull(adapter, "adapter is null"); //$NON-NLS-1$
        if (!adapter.isDeletable()) {
            BgcPlugin.openError(
                Messages.DeleteSelectionHandler_delete_error_title,
                Messages.DeleteSelectionHandler_delete_error_msg);
            return null;
        }
        adapter.deleteWithConfirm();
        return null;
    }

    @Override
    public boolean isEnabled() {
        AdapterBase adapter = SessionManager.getSelectedNode();
        boolean isEnabled = adapter != null && adapter.isDeletable()
            && SessionManager.canDelete(adapter.getModelObject());
        return isEnabled;
    }
}
