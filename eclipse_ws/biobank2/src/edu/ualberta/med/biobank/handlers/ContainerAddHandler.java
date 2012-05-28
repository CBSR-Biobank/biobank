package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import org.eclipse.core.commands.AbstractHandler;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;

public class ContainerAddHandler extends AbstractHandler {
    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.commands.containerAdd";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ContainerAdapter containerAdapter =
            new ContainerAdapter(null, new ContainerWrapper(
                SessionManager.getAppService()));
        ((ContainerWrapper) containerAdapter.getModelObject())
            .setSite(SessionManager.getUser().getCurrentWorkingSite());
        containerAdapter.openEntryForm(false);
        return null;
    }
}
