package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;

public class DeleteSelectionHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractAdapterBase adapter = SessionManager.getSelectedNode();
        Assert.isNotNull(adapter, "adapter is null"); //$NON-NLS-1$
        adapter.deleteWithConfirm();
        return null;
    }
}
