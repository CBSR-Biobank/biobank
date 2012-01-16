package edu.ualberta.med.biobank.common.action.researchGroup;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupDeletePermission;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.User;

public class ResearchGroupDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer rgId = null;

    public ResearchGroupDeleteAction(Integer id) {
        this.rgId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new ResearchGroupDeletePermission(rgId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        ResearchGroup rg =
            new SessionUtil(session).get(ResearchGroup.class, rgId);

        // / ??? what checks???

        session.delete(rg);
        return new EmptyResult();
    }
}
