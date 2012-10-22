package edu.ualberta.med.biobank.action.impl.center;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import edu.ualberta.med.biobank.action.ActionExecutor;
import edu.ualberta.med.biobank.action.ActionHandler;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.center.CenterCreate;
import edu.ualberta.med.biobank.i18n.ActionException;

// TODO: move the implementation into a separate project?
// Note: Like transaction demarcation, revision demarcation should be done outside of the actions.
public class CenterCreateHandler
    implements ActionHandler<CenterCreate, IdResult> {

    @Override
    public Class<CenterCreate> getActionType() {
        return CenterCreate.class;
    }

    @Override
    public IdResult run(CenterCreate action, ActionExecutor executor)
        throws ActionException {

        // TODO: add a record of the log being done.
        // call another action? It doesn't matter...
        return null;
    }

    @Override
    public void rollback(CenterCreate action, IdResult result,
        ActionExecutor executor) throws ActionException {
        // handled by db
    }

    @Override
    public boolean allowed(CenterCreate action) {
        return false;
    }
}
