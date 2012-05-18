package edu.ualberta.med.biobank.rcp;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

/**
 * This class shows an example for checking for updates and performing the
 * update synchronously. It is up to the caller to run this in a job if a
 * background update check is desired. This is a reasonable way to run an
 * operation when user intervention is not required. Another approach is to
 * separately perform the resolution and provisioning steps, deciding whether to
 * perform these synchronously or in a job.
 * 
 * Any p2 operation can be run modally (synchronously), or the job can be
 * requested and scheduled by the caller.
 * 
 * @see UpdateOperation#resolveModal(IProgressMonitor)
 * @see UpdateOperation#getResolveJob(IProgressMonitor)
 * @see UpdateOperation#getProvisioningJob(IProgressMonitor)
 * 
 * @see "http://wiki.eclipse.org/Equinox/p2/Adding_Self-Update_to_an_RCP_Application"
 */
public class P2Util {

    private static final String JUSTUPDATED = "justUpdated";

    private static BgcLogger logger = BgcLogger.getLogger(P2Util.class
        .getName());

    @SuppressWarnings("restriction")
    public static void checkForUpdates() {
        if (BiobankPlugin.getDefault().isDebugging()) {
            // do not try to update the client if running in debug mode
            return;
        }

        final IProvisioningAgent agent = (IProvisioningAgent) org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper
            .getService(BiobankPlugin.bundleContext,
                IProvisioningAgent.SERVICE_NAME);
        if (agent == null) {
            logger
                .addRcpLogStatus(
                    IStatus.ERROR,
                    "No provisioning agent found.  This application is not set up for updates.",
                    null);
        }
        // XXX if we're restarting after updating, don't check again.
        final IPreferenceStore prefStore = BiobankPlugin.getDefault()
            .getPreferenceStore();
        if (prefStore.getBoolean(JUSTUPDATED)) {
            prefStore.setValue(JUSTUPDATED, false);
            return;
        }

        // XXX check for updates before starting up.
        // If an update is performed, restart. Otherwise log
        // the status.
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                throws InvocationTargetException, InterruptedException {
                IStatus updateStatus = checkForUpdates(agent, monitor);
                if (updateStatus.getCode() != UpdateOperation.STATUS_NOTHING_TO_UPDATE
                    && updateStatus.getSeverity() != IStatus.ERROR) {
                    prefStore.setValue(JUSTUPDATED, true);
                    PlatformUI.getWorkbench().restart();
                } else {
                    logger.addRcpLogStatus(updateStatus);
                }
            }
        };
        try {
            new ProgressMonitorDialog(null).run(true, true, runnable);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        }
    }

    // Check for updates to this application and return a status.
    private static IStatus checkForUpdates(IProvisioningAgent agent,
        IProgressMonitor monitor) throws OperationCanceledException {
        ProvisioningSession session = new ProvisioningSession(agent);
        // the default update operation looks for updates to the currently
        // running profile, using the default profile root marker. To change
        // which installable units are being updated, use the more detailed
        // constructors.
        UpdateOperation operation = new UpdateOperation(session);
        SubMonitor sub = SubMonitor.convert(monitor,
            "Checking for application updates...", 200);
        IStatus status = operation.resolveModal(sub.newChild(100));
        if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
            return status;
        }
        if (status.getSeverity() == IStatus.CANCEL)
            throw new OperationCanceledException();

        if (status.getSeverity() != IStatus.ERROR) {
            // More complex status handling might include showing the user what
            // updates
            // are available if there are multiples, differentiating patches vs.
            // updates, etc.
            // In this example, we simply update as suggested by the operation.
            ProvisioningJob job = operation.getProvisioningJob(null);
            if (job == null)
                return new Status(IStatus.ERROR, BiobankPlugin.PLUGIN_ID,
                    "No updates were found.");

            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog
                        .openInformation(
                            null,
                            "Updates",
                            "Updates have been found. The application will restart after the installation is done.");
                }
            });

            status = job.runModal(sub.newChild(100));
            if (status.getSeverity() == IStatus.CANCEL)
                throw new OperationCanceledException();
        }
        return status;
    }
}
