package edu.ualberta.med.biobank.action.center;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.center.Center;

public abstract class CenterDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer centerId;

    public CenterDeleteAction(Center center) {
        if (center == null) {
            throw new IllegalArgumentException();
        }
        this.centerId = center.getId();
    }

    public EmptyResult run(ActionContext context, Center center)
        throws ActionException {
        context.getSession().delete(center);
        return new EmptyResult();
    }
}
