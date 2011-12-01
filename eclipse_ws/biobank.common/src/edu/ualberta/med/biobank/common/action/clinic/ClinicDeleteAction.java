package edu.ualberta.med.biobank.common.action.clinic;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.center.CenterDeleteAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicDeletePermission;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.User;

public class ClinicDeleteAction extends CenterDeleteAction {
    private static final long serialVersionUID = 1L;

    private final Clinic clinic;

    public ClinicDeleteAction(Clinic clinic) {
        super(clinic.getId());
        this.clinic = clinic;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new ClinicDeletePermission(centerId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        return super.run(user, session, clinic);
    }
}