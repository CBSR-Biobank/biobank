package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;

public class SiteAddHandler extends AbstractHandler {
    @SuppressWarnings("unused")
    private static BgcLogger LOGGER = BgcLogger.getLogger(SiteAddHandler.class
        .getName());

    // private EventBus eventBus;

    @Inject
    public void setEventBus(EventBus eventBus) {
        // this.eventBus = eventBus;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // MVP code
        // TODO: this class should be injected, not inject itself. Worst case,
        // have some Handler super-class with an EventBus that injects itself
        // upon instantiation?
        // Injector injector = BiobankPlugin.getInjector();
        // injector.injectMembers(this);

        // eventBus.fireEvent(new SiteCreateEvent());

        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        sessionAdapter.addSite();

        return null;

    }

    @Override
    public boolean isEnabled() {
        return SessionManager.isSuperAdminMode()
            && SessionManager.canCreate(SiteWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }
}