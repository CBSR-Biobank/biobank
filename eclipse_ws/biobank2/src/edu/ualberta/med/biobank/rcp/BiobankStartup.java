package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import edu.ualberta.med.biobank.handlers.LoginHandler;

public class BiobankStartup implements IStartup {

    @Override
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window != null) {
                    IHandlerService handlerService = (IHandlerService) window
                        .getService(IHandlerService.class);
                    try {
                        handlerService.executeCommand(LoginHandler.ID, null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
