package edu.ualberta.med.biobank.rcp;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.ResourceResolver;
import edu.ualberta.med.biobank.common.ServiceConnection;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
    public static final String PLUGIN_ID = "biobank2";

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
     * IApplicationContext)
     */
    public Object start(IApplicationContext context) throws Exception {
        Display display = PlatformUI.createDisplay();

        ServiceConnection.setResourceResolver(new ResourceResolver() {
            @Override
            public URL resolveURL(URL url) throws Exception {
                return FileLocator.resolve(url);
            }
        });
        ServiceConnection.setTrustStore();

        boolean trace = "true".equalsIgnoreCase(Platform
            .getDebugOption(PlatformUI.PLUGIN_ID + "/trace/graphics"));
        if (trace) {
            Sleak sleak = new Sleak();
            sleak.open();
        }

        try {
            int returnCode = PlatformUI.createAndRunWorkbench(display,
                new ApplicationWorkbenchAdvisor());
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally {
            display.dispose();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return;
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (!display.isDisposed())
                    workbench.close();
            }
        });
    }
}
