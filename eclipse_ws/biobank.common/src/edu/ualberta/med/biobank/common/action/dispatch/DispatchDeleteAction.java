package edu.ualberta.med.biobank.common.action.dispatch;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.User;

public class DispatchDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer shipId = null;

    public DispatchDeleteAction(Integer id) {
        this.shipId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new DispatchDeletePermission(shipId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Dispatch ship =
            new SessionUtil(session).get(Dispatch.class, shipId);

        // / ??? what checks???

        session.delete(ship);
        return new EmptyResult();
    }
}
