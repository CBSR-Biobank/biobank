package edu.ualberta.med.biobank.common.action.clinic;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
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
        Permission permission;
        if (centerId == null)
            permission = new ClinicCreatePermission();
        else
            permission = new ClinicUpdatePermission(centerId);
        return permission.isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (contactIds == null) {
            throw new NullPointerException("contact ids cannot be null");
        }
        SessionUtil sessionUtil = new SessionUtil(session);
        Clinic clinic = sessionUtil.get(Clinic.class, centerId, new Clinic());
        clinic.setSendsShipments(sendsShipments);

        Map<Integer, Contact> contacts =
            sessionUtil.get(Contact.class, contactIds);
        SetDifference<Contact> contactsDiff = new SetDifference<Contact>(
            clinic.getContactCollection(), contacts.values());
        clinic.setContactCollection(contactsDiff.getNewSet());
        for (Contact c : contactsDiff.getRemoveSet()) {
            session.delete(c);
        }

        return run(user, session, sessionUtil, clinic);
    }

}
