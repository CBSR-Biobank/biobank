package edu.ualberta.med.biobank.common.action.request;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.request.RequestDeletePermission;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.User;

public class RequestDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer rId = null;

    public RequestDeleteAction(Integer id) {
        this.rId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new RequestDeletePermission(rId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Request r =
            new SessionUtil(session).get(Request.class, rId);

        // / ??? what checks???

        session.delete(r);
        return new EmptyResult();
    }
}
