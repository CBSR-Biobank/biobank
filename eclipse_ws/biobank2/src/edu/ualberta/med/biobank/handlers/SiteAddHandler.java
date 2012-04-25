package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.site.SiteCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteAddHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory.getI18n(SiteAddHandler.class);

    @SuppressWarnings("unused")
    private static BgcLogger LOGGER = BgcLogger.getLogger(SiteAddHandler.class
        .getName());

    // private EventBus eventBus;

    @SuppressWarnings("unused")
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

    @SuppressWarnings("nls")
    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new SiteCreatePermission());
            return SessionManager.isSuperAdminMode()
                && allowed
                && SessionManager.getInstance().getSession() != null;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Unable to retrieve permissions"));
            return false;
        }
    }
}