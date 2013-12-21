package edu.ualberta.med.biobank.common.action.specimen;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;

public class SpecimenGetInfoAction implements Action<SpecimenBriefInfo> {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenGetInfoAction(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(specimenId).isAllowed(context);
    }

    @Override
    public SpecimenBriefInfo run(ActionContext context) throws ActionException {
        return SpecimenActionHelper.getSpecimenBriefInfo(context, specimenId, null);
    }

}
