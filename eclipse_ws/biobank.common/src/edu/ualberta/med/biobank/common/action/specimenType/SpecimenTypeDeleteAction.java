package edu.ualberta.med.biobank.common.action.specimenType;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeDeletePermission;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypeDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer specimenTypeId;

    public SpecimenTypeDeleteAction(SpecimenType specimenType) {
        if (specimenType == null) {
            throw new IllegalArgumentException();
        }
        this.specimenTypeId = specimenType.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SpecimenTypeDeletePermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        SpecimenType specimen =
            context.load(SpecimenType.class, specimenTypeId);

        context.getSession().delete(specimen);
        return new EmptyResult();
    }
}
