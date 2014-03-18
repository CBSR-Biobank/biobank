package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.views.ProcessingView;

public class ProcessingEventAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        ProcessingEventWrapper pe = new ProcessingEventWrapper(
            SessionManager.getAppService());
        pe.setCenter(SessionManager.getUser().getCurrentWorkingCenter());

        ProcessingView view = ProcessingView.getCurrent();
        if (view != null) {
            ProcessingEventAdapter node = new ProcessingEventAdapter(view.getProcessingNode(), pe);
            node.openEntryForm();
        }
        return null;
    }

}