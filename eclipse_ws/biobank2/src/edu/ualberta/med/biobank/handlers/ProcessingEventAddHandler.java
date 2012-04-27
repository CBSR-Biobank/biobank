package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.handlers.LogoutSensitiveHandler;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.views.ProcessingView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingEventAddHandler extends LogoutSensitiveHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        ProcessingEventWrapper pe = new ProcessingEventWrapper(
            SessionManager.getAppService());
        pe.setCenter(SessionManager.getUser().getCurrentWorkingCenter());
        ProcessingEventAdapter node = new ProcessingEventAdapter(ProcessingView
            .getCurrent().getProcessingNode(), pe);
        node.openEntryForm();
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new ProcessingEventCreatePermission(SessionManager
                            .getUser().getCurrentWorkingCenter().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(Messages.HandlerPermission_error,
                Messages.HandlerPermission_message);
        }
        return allowed;
    }

}