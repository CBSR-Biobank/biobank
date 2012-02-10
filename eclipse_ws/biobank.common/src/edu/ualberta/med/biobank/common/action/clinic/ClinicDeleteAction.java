package edu.ualberta.med.biobank.common.action.clinic;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.center.CenterDeleteAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicDeletePermission;
import edu.ualberta.med.biobank.model.Clinic;

public class ClinicDeleteAction extends CenterDeleteAction {
    private static final long serialVersionUID = 1L;

    public ClinicDeleteAction(Integer id) {
        super(id);
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