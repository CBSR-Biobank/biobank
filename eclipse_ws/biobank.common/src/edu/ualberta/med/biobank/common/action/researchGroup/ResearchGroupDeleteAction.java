package edu.ualberta.med.biobank.common.action.researchGroup;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.center.CenterDeleteAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupDeletePermission;
import edu.ualberta.med.biobank.model.ResearchGroup;

/**
 *
 * Action object that deletes the Research Group from the database
 *
 * Code Changes -
 * 		1> Extend the CenterDeleteAction as the Research Group should have similar behavior as a Site(Center)
 * 		2> Delegate the call to the parent to delete
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupDeleteAction extends CenterDeleteAction {
    private static final long serialVersionUID = 1L;

    public ResearchGroupDeleteAction(ResearchGroup researchGroup) {
        super(researchGroup);
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ResearchGroupDeletePermission(centerId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
	ResearchGroup rg = context.load(ResearchGroup.class, centerId);
        return super.run(context, rg);
    }
}