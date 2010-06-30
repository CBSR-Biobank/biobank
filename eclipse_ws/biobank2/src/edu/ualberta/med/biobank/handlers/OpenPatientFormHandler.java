package edu.ualberta.med.biobank.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.console.IConsoleConstants;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.rcp.AliquotManagementPerspective;
import edu.ualberta.med.biobank.treeview.AdapterBase;

/**
 * Open a form in patient administration like scan link, scan assign or cabinet
 * like/assign
 */
public class OpenPatientFormHandler extends AbstractHandler implements IHandler {

    private static final String EDITOR_ID_PARAM = "edu.ualberta.med.biobank.commands.patients.openPatientForm.editorId";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        @SuppressWarnings({ "rawtypes" })
        final Map parameters = event.getParameters();
        final String editorId = (String) parameters.get(EDITOR_ID_PARAM);
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true)) {
                workbench.showPerspective(AliquotManagementPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
                IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
                    .getActivePage();
                // open the editor
                AdapterBase.openForm(new FormInput(SessionManager.getInstance()
                    .getSession()), editorId, true);
                hideConsoleViewIcons(page);
            }
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Error while opening aliquot management perspective", e);
        }
        return null;
    }

    private void hideConsoleViewIcons(IWorkbenchPage activePage) {
        // Remove buttons in the console view toolbar
        // TODO can we do something nicer ?
        IViewPart viewPart = activePage
            .findView(IConsoleConstants.ID_CONSOLE_VIEW);
        if (viewPart != null) {
            IViewSite viewSite = viewPart.getViewSite();
            IActionBars actionBars = viewSite.getActionBars();
            IToolBarManager toolBarManager = actionBars.getToolBarManager();

            IContributionItem[] contributionItems = toolBarManager.getItems();
            contributionItems = toolBarManager.getItems();
            for (int i = 0; i < contributionItems.length; i++)
                if (i >= 2)
                    toolBarManager.remove(contributionItems[i]);
        }
    }
}
