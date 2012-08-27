package edu.ualberta.med.biobank.forms.listener;

import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.IBusyListener;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class ProgressMonitorDialogBusyListener implements IBusyListener {
    private static final I18n i18n = I18nFactory
        .getI18n(ProgressMonitorDialogBusyListener.class);

    private final String task;
    private final Semaphore semaphore = new Semaphore(1);

    public ProgressMonitorDialogBusyListener(String task) {
        this.task = task;
    }

    @Override
    public void showBusy() {
        Display.getDefault().asyncExec(new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                IRunnableContext context = new ProgressMonitorDialog(Display
                    .getDefault().getActiveShell());
                try {
                    context.run(true, false, new IRunnableWithProgress() {
                        @Override
                        public void run(final IProgressMonitor monitor) {
                            try {
                                semaphore.acquire();
                                monitor.beginTask(task,
                                    IProgressMonitor.UNKNOWN);

                                // wait until done() called
                                semaphore.acquire();
                                semaphore.release();
                                monitor.done();
                            } catch (Exception e) {
                                BgcPlugin.openAsyncError(
                                    // dialog title
                                    i18n.tr("Thread Error"), e);
                            }
                        }
                    });
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        // dialog title
                        i18n.tr("Loading Error"), e);
                }
            }
        });
    }

    @Override
    public void done() {
        semaphore.release();
    }
}
