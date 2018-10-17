package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;

public class RequestStateChangeAction implements Action<EmptyResult> {

    private static final long serialVersionUID = 3825968229912773240L;

    private final List<Integer> rSpecIds;

    private final RequestSpecimenState state;

    public RequestStateChangeAction(List<Integer> rSpecIds, RequestSpecimenState state) {
        this.rSpecIds = rSpecIds;
        this.state = state;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.REQUEST_PROCESS.isAllowed(context.getUser());
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        for (Integer id : rSpecIds) {
            RequestSpecimen rs = context.get(RequestSpecimen.class, id);
            rs.setState(state);
            context.getSession().saveOrUpdate(rs);
        }
        return new EmptyResult();
    }
}
