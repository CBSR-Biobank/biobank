package edu.ualberta.med.biobank.rcp;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import edu.ualberta.med.biobank.SessionManager;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(
        IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(
        IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle("BioBank2");
        configurer.setShowProgressIndicator(true);
    }

    @Override
    public void postWindowOpen() {
        IStatusLineManager statusline = getWindowConfigurer()
            .getActionBarConfigurer().getStatusLineManager();
        statusline.setMessage(null, "Application ready");

        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        if (page.getPerspective().getId()
            .equals(SampleManagementPerspective.ID)) {
            // can't start on this perspective: switch to patient perspective
            try {
                workbench.showPerspective(PatientsAdministrationPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
            } catch (WorkbenchException e) {
                SessionManager.getLogger().error(
                    "Error while opening patients perpective", e);
            }
        }
        page.addPartListener(new BiobankPartListener());
        window.addPerspectiveListener(new BiobankPerspectiveListener());
    }

}
