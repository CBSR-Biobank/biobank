package edu.ualberta.med.biobank.treeview.util;

import java.lang.reflect.InvocationTargetException;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public abstract class CallRunnablePersistOnAdapter implements
    IRunnableWithProgress {

    private AdapterBase adapter;

    public CallRunnablePersistOnAdapter(AdapterBase adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
        InterruptedException {
        monitor.beginTask("Saving...", IProgressMonitor.UNKNOWN);
        try {
            adapter.getModelObject().persist();
            afterPersist();
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    doSetDirty(true);
                }
            });
            monitor.setCanceled(true);
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    doSetDirty(true);
                }
            });
            monitor.setCanceled(true);
        } catch (final AccessDeniedException ade) {
            BgcPlugin.openAccessDeniedErrorMessage(ade);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    doSetDirty(true);
                }
            });
            monitor.setCanceled(true);
        } catch (BiobankCheckException bce) {
            BgcPlugin.openAsyncError("Save error", bce);
            monitor.setCanceled(true);
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    doSetDirty(true);
                }
            });
        } catch (Exception e) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    doSetDirty(true);
                }
            });
            throw new RuntimeException(e);
        }
        monitor.done();

    }

    public abstract void afterPersist() throws Exception;

    public abstract void doSetDirty(boolean b);

}
