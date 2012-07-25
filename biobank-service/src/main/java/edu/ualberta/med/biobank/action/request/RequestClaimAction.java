package edu.ualberta.med.biobank.action.request;

import java.util.List;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.RequestSpecimen;

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
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.REQUEST_PROCESS.isAllowed(context.getUser());
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        for (Integer id : rSpecIds) {
            RequestSpecimen rs = context.get(RequestSpecimen.class, id);
            rs.setClaimedBy(context.getUser().getLogin());
            context.getSession().saveOrUpdate(rs);
        }
        context.getSession().flush();
        return new EmptyResult();
    }
}
