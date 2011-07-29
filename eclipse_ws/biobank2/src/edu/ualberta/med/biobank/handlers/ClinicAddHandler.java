package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;

public class ClinicAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addClinic"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter session = SessionManager.getInstance().getSession();
        Assert.isNotNull(session);
        session.addClinic();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.isSuperAdminMode()
            && SessionManager.canCreate(ClinicWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }
}
