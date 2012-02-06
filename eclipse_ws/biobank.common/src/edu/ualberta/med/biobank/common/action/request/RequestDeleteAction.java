package edu.ualberta.med.biobank.common.action.request;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.request.RequestDeletePermission;
import edu.ualberta.med.biobank.model.Request;

public class RequestDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer rId = null;

    public RequestDeleteAction(Integer id) {
        this.rId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new RequestDeletePermission(rId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Request r = context.get(Request.class, rId);

        if (r.getSubmitted() != null)
            throw new ActionException(
                "This request has already been submitted.");

        context.getSession().delete(r);
        return new EmptyResult();
    }
}
