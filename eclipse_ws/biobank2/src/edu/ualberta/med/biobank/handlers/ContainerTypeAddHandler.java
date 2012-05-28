package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import org.eclipse.core.commands.AbstractHandler;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;

public class ContainerTypeAddHandler extends AbstractHandler {
    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.commands.containerTypeAdd";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ContainerTypeAdapter containerTypeAdapter = new ContainerTypeAdapter(
            null, new ContainerTypeWrapper(SessionManager.getAppService()));
        ((ContainerTypeWrapper) containerTypeAdapter.getModelObject())
            .setSite(SessionManager.getUser().getCurrentWorkingSite());
        containerTypeAdapter.openEntryForm(false);
        return null;
    }
}
