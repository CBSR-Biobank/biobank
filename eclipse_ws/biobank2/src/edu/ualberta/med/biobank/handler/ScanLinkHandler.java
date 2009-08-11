package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.views.patients.ScanLinkView;

public class ScanLinkHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil
            .getActiveWorkbenchWindowChecked(event);
        try {
            IWorkbenchPage activePage = window.getActivePage();
            if (activePage == null) {
                return null;
            }
            for (IViewReference ref : activePage.getViewReferences()) {
                activePage.hideView(ref);
            }
            activePage.showView(ScanLinkView.ID);

        } catch (PartInitException e) {
            throw new ExecutionException("Part could not be initialized", e); //$NON-NLS-1$
        }
        return null;
    }

}
