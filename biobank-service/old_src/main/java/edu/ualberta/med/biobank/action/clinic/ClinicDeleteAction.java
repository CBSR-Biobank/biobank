package edu.ualberta.med.biobank.action.clinic;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.center.CenterDeleteAction;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.clinic.ClinicDeletePermission;
import edu.ualberta.med.biobank.model.Clinic;

public class ClinicDeleteAction extends CenterDeleteAction {
    private static final long serialVersionUID = 1L;

    public ClinicDeleteAction(Clinic clinic) {
        super(clinic);
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ClinicDeletePermission(centerId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Clinic clinic = context.load(Clinic.class, centerId);
        return super.run(context, clinic);
    }
}