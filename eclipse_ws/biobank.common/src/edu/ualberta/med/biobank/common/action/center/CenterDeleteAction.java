package edu.ualberta.med.biobank.common.action.center;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.User;

public abstract class CenterDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer centerId = null;

    public CenterDeleteAction(Integer id) {
        this.centerId = id;
    }

    public EmptyResult run(User user, Session session, Center center)
        throws ActionException {
        // TODO: checks
        // FIXME permissions
        session.delete(center);
        return new EmptyResult();
    }
}
