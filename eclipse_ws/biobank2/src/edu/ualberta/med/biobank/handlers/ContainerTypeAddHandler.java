package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;

public class ContainerTypeAddHandler extends AbstractHandler {
    private static final I18n i18n = I18nFactory.getI18n(ContainerTypeAddHandler.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.commands.containerTypeAdd";

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteWrapper site = SessionManager.getUser().getCurrentWorkingSite();

        if (site == null) {
            BgcPlugin.openError(
                // TR: error dialog title
                i18n.tr("Working Center Error"),
                // TR: error dialog message
                i18n.tr("Cannot create container type. The working center you chose is not a "
                    + "repository site.\n\n"
                    + "In this case, use the context menu to add a container type to a site."));
            return null;
        }

        ContainerTypeAdapter containerTypeAdapter = new ContainerTypeAdapter(
            null, new ContainerTypeWrapper(SessionManager.getAppService()));
        ((ContainerTypeWrapper) containerTypeAdapter.getModelObject()).setSite(site);
        containerTypeAdapter.openEntryForm(false);
        return null;
    }
}
