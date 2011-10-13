package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;

public class SiteAddHandler extends AbstractHandler {

    private static BgcLogger LOGGER = BgcLogger.getLogger(SiteAddHandler.class
        .getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        sessionAdapter.addSite();

        IEditorInput input = new IEditorInput() {

            @Override
            public Object getAdapter(Class adapter) {
                return new Site();
            }

            @Override
            public boolean exists() {
                return false;
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return BiobankPlugin.getImageDescriptor(BgcPlugin.IMG_SITE);
            }

            @Override
            public String getName() {
                return "new Site";
            }

            @Override
            public String getToolTipText() {
                return "new Site";
            }

            @Override
            public IPersistableElement getPersistable() {
                return null;
            }

        };

        // close view form for this site if currently being
        // displayed to the user
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        IEditorPart part = page.findEditor(input);
        if (part != null) {
            return page.closeEditor(part, true);
        }

        try {
            part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, SiteEntryForm.ID, true);
        } catch (PartInitException e) {
            LOGGER.error("Can't open form with id " + SiteEntryForm.ID, e); //$NON-NLS-1$
        }

        return null;

    }

    @Override
    public boolean isEnabled() {
        return SessionManager.isSuperAdminMode()
            && SessionManager.canCreate(SiteWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }
}