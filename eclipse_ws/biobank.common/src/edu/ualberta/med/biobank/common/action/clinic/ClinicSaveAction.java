package edu.ualberta.med.biobank.common.action.clinic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.User;

public class ClinicSaveAction extends CenterSaveAction {

    private static final long serialVersionUID = 1L;

    private Boolean sendsShipments;
    private Set<Integer> contactIds;

    public void setSendsShipments(Boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    public void setContactIds(Set<Integer> contactIds) {
        this.contactIds = contactIds;
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

        // TODO: set collections based on diffs
        Map<Integer, Contact> contacts =
            sessionUtil.get(Contact.class, contactIds);
        clinic.setContactCollection(new HashSet<Contact>(contacts.values()));

        return run(user, session, sessionUtil, clinic);
    }

}
