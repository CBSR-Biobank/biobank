package edu.ualberta.med.biobank.common.action.researchGroup;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupDeletePermission;
import edu.ualberta.med.biobank.model.ResearchGroup;

public class ResearchGroupDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected final Integer rgId;

    public ResearchGroupDeleteAction(ResearchGroup researchGroup) {
        if (researchGroup == null) {
            throw new IllegalArgumentException();
        }
        this.rgId = researchGroup.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ResearchGroupDeletePermission(rgId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        // FIXME: this should work but doesn't
        ResearchGroup rg = context.get(ResearchGroup.class, rgId);
        context.getSession().delete(rg);
        return new EmptyResult();
    }
}
