package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.views.SearchView;

public class SearchHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        try {
            workbench.getActiveWorkbenchWindow().getActivePage().showView(
                SearchView.ID);
        } catch (PartInitException e) {
            throw new ExecutionException("View can't be open", e);
        }
        return null;
    }

}
