package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.views.SearchView;

public class SearchHandler extends AbstractHandler {
    private static final I18n i18n = I18nFactory.getI18n(SearchHandler.class);

    @SuppressWarnings("nls")
    public static final String SEARCH_COMMAND_ID =
        "edu.ualberta.med.biobank.commands.search";

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        try {
            page.showView(SearchView.ID);
        } catch (PartInitException e) {
            throw new ExecutionException(
                // exception message
                i18n.tr("View cannot be opened"), e);
        }
        return null;
    }

}
