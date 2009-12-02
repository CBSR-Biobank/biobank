package edu.ualberta.med.biobank.rcp;

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
        IWorkbenchAction resetView = ActionFactory.NEW_EDITOR.create(window);
        register(resetView);
    }

}
