package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;

public class ContainerAddHandler extends AbstractHandler {
    private static final I18n i18n = I18nFactory.getI18n(ContainerAddHandler.class);

    @SuppressWarnings("nls")
    public static final String ID =
    "edu.ualberta.med.biobank.commands.containerAdd";

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteWrapper site = SessionManager.getUser().getCurrentWorkingSite();

        if (site == null) {
            BgcPlugin.openError(
                // TR: error dialog title
                i18n.tr("Working Center Error"),
                // TR: error dialog message
                i18n.tr("Cannot create a container. The working center you chose is not a "
                    + "repository site.\n\n"
                    + "In this case, use the context menu to add a container to a site."));
            return null;
        }

        ContainerAdapter containerAdapter =
            new ContainerAdapter(null, new ContainerWrapper(SessionManager.getAppService()));
        ((ContainerWrapper) containerAdapter.getModelObject()).setSite(site);
        containerAdapter.openEntryForm(false);
        return null;
    }
}
