package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.ServiceConnection;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
        IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public String getInitialWindowPerspectiveId() {
        return MainPerspective.ID;
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
        configurer.setSaveAndRestore(true);
    }

    @Override
    public boolean preShutdown() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        if (page.getPerspective().getId().equals(ReportsPerspective.ID)) {
            IPerspectiveDescriptor main = workbench.getPerspectiveRegistry()
                .findPerspectiveWithId(MainPerspective.ID);
            page.setPerspective(main);
        }
        if (BioBankPlugin.isAskPrint()
            && page.getPerspective().getId().equals(
                AliquotManagementPerspective.ID)) {
            BioBankPlugin.openInformation("Can't close",
                "Please end aliquot management session before closing");
            return false;
        }
        if (SessionManager.getInstance().isConnected()) {
            ServiceConnection.logout(SessionManager.getAppService());
        }
        return true;
    }
}
