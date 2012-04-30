package edu.ualberta.med.biobank.common.action.specimen;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenDeletePermission;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenDeleteAction(Specimen specimen) {
        if (specimen == null) {
            throw new IllegalArgumentException();
        }
        this.specimenId = specimen.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SpecimenDeletePermission(specimenId)
            .isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Specimen specimen = context.load(Specimen.class, specimenId);

        context.getSession().delete(specimen);
        return new EmptyResult();
    }
}
