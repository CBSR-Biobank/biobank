package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.views.SearchView;

public class SearchHandler extends AbstractHandler {

    public static final String SEARCH_COMMAND_ID = "edu.ualberta.med.biobank.commands.search"; 

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        try {
            page.showView(SearchView.ID);
        } catch (PartInitException e) {
            throw new ExecutionException("View cannot be opened", e);
        }
        return null;
    }

}
