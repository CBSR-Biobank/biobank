package edu.ualberta.med.biobank.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.dispatch.OutgoingNode;
import edu.ualberta.med.biobank.views.DispatchAdministrationView;

public class DispatchAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteWrapper site = SessionManager.getCurrentSite();
        List<StudyWrapper> dispatchStudies = site.getDispatchStudiesAsSender();
        if (dispatchStudies == null || dispatchStudies.size() == 0) {
            BioBankPlugin.openAsyncError("Sender Site Error",
                "The current site does not have any dispatch studies associated"
                    + " with it.\nPlease see site configuration.");
            return null;
        }
        OutgoingNode node = DispatchAdministrationView.getCurrent()
            .getOutgoingNode();
        Assert.isNotNull(node);
        node.addDispatch();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(DispatchWrapper.class,
            SessionManager.getCurrentSite());
    }
}