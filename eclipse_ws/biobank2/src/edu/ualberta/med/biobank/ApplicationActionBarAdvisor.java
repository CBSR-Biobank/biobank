package edu.ualberta.med.biobank;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {	
    	//resetPerspective(window);
    }

    protected void resetPerspective(IWorkbenchWindow window) {	
    	IWorkbenchAction quickStartAction = ActionFactory.INTRO.create(window);
    	register(quickStartAction);
    	IWorkbenchAction resetView = ActionFactory.RESET_PERSPECTIVE
    	.create(window);
    	register(resetView);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    }
    
}
