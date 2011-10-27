package edu.ualberta.med.biobank.common.action.clinic;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.User;

public class ClinicSaveAction extends CenterSaveAction {

    private static final long serialVersionUID = 1L;

    private Boolean sendsShipments;

    public void setSendsShipments(Boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Clinic clinic = sessionUtil.get(Clinic.class, centerId, new Clinic());
        clinic.setSendsShipments(sendsShipments);

        return run(user, session, sessionUtil, clinic);
    }

}
