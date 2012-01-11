package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.User;

public class RequestClaimAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 3825968229912773240L;
    private List<Integer> rSpecIds;

    public RequestClaimAction(List<Integer> rSpecIds) {
        this.rSpecIds = rSpecIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return PermissionEnum.REQUEST_PROCESS.isAllowed(user);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);

        for (Integer id : rSpecIds) {
            RequestSpecimen rs = sessionUtil.get(RequestSpecimen.class, id);
            rs.setClaimedBy(user.getLogin());
            session.saveOrUpdate(rs);
        }
        session.flush();
        return new EmptyResult();
    }
}
