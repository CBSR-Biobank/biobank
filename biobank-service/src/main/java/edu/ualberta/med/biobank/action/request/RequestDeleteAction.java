package edu.ualberta.med.biobank.action.request;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.request.RequestDeletePermission;
import edu.ualberta.med.biobank.model.Request;

public class RequestDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer requestId;

    public RequestDeleteAction(Request request) {
        if (request == null) {
            throw new IllegalArgumentException();
        }
        this.requestId = request.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new RequestDeletePermission(requestId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Request r = context.get(Request.class, requestId);
        context.getSession().delete(r);
        return new EmptyResult();
    }
}
