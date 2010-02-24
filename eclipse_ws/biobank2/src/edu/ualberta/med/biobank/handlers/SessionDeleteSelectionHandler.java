package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SessionDeleteSelectionHandler extends AbstractHandler implements
    IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        AdapterBase adapter = SessionManager.getSelectedNode();
        Assert.isNotNull(adapter, "adapter is null");
        if (!adapter.isDeletable()) {
            BioBankPlugin.openError("Delete Error",
                "this item cannot be deleted");
            return null;
        }
        adapter.deleteWithConfirm();
        return null;
    }

}
