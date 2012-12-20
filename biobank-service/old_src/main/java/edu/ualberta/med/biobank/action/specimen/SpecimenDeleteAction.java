package edu.ualberta.med.biobank.action.specimen;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.specimen.SpecimenDeletePermission;
import edu.ualberta.med.biobank.model.study.Specimen;

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
