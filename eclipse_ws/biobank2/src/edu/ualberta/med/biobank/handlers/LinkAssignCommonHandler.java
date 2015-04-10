package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.rcp.perspective.LinkAssignPerspective;
import edu.ualberta.med.biobank.treeview.AdapterBase;

/**
 * Open a form in patient administration like scan link, scan assign or cabinet like/assign
 */
public abstract class LinkAssignCommonHandler extends AbstractHandler
    implements
    IHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(LinkAssignCommonHandler.class);

    @SuppressWarnings("nls")
    public Object openLinkAssignPerspective(String editorId, AdapterBase adapter)
        throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage().closeAllEditors(true)) {
                workbench.showPerspective(
                    LinkAssignPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
                IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                // open the editor
                AdapterBase.openForm(new FormInput(adapter), editorId, true);
                hideConsoleViewIcons(page);
            }
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                // exception message
                i18n.tr("Error while opening link-assign management perspective"),
                e);
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
