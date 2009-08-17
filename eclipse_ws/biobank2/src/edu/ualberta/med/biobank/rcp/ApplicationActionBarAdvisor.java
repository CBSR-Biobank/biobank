package edu.ualberta.med.biobank.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(IWorkbenchWindow window) {
        resetPerspective(window);
    }

    protected void resetPerspective(IWorkbenchWindow window) {
        // IWorkbenchAction quickStartAction =
        // ActionFactory.INTRO.create(window);
        // register(quickStartAction);
        IWorkbenchAction resetView = ActionFactory.NEW_EDITOR.create(window);
        register(resetView);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
    }

    @Override
    protected void fillStatusLine(IStatusLineManager statusLine) {
        StatusLineContributionItem slci = new StatusLineContributionItem(
            "status.user");
        statusLine.add(slci);
    }

}
