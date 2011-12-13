package edu.ualberta.med.biobank.common.action.clinic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class ClinicSaveAction extends CenterSaveAction {
    private static final long serialVersionUID = 1L;

    public static class ContactSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id = null;
        public String name;
        public String title;
        public String mobileNumber;
        public String faxNumber;
        public String pagerNumber;
        public String officeNumber;
        public String emailAddress;
        public Set<Integer> studyIds;

        public ContactSaveInfo() {

        }

        public ContactSaveInfo(Contact contact) {
            this.id = contact.getId();
            this.name = contact.getName();
            this.title = contact.getTitle();
            this.mobileNumber = contact.getMobileNumber();
            this.faxNumber = contact.getFaxNumber();
            this.pagerNumber = contact.getPagerNumber();
            this.officeNumber = contact.getOfficeNumber();
            this.emailAddress = contact.getEmailAddress();

            HashSet<Integer> studyIds = new HashSet<Integer>();
            for (Study study : contact.getStudyCollection()) {
                studyIds.add(study.getId());
            }
            this.studyIds = studyIds;
        }

        public Contact populateContcat(Clinic clinic, Contact contact) {
            contact.setClinic(clinic);
            contact.setName(this.name);
            contact.setTitle(this.title);
            contact.setMobileNumber(this.mobileNumber);
            contact.setFaxNumber(this.faxNumber);
            contact.setPagerNumber(this.pagerNumber);
            contact.setOfficeNumber(this.officeNumber);
            contact.setEmailAddress(this.emailAddress);
            return contact;
        }
    }

    private Boolean sendsShipments;
    private Collection<ContactSaveInfo> contactSaveInfos;
    private Session session = null;
    private SessionUtil sessionUtil = null;
    private Clinic clinic = null;

    public void setSendsShipments(Boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    public void setContactSaveInfos(Collection<ContactSaveInfo> contactSaveInfos) {
        this.contactSaveInfos = contactSaveInfos;
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

    /**
     * Contacts cannot be deleted if it is still associated with a study.
     */
    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (contactSaveInfos == null) {
            throw new NullPropertyException(Clinic.class,
                ClinicPeer.CONTACT_COLLECTION);
        }

        this.session = session;
        sessionUtil = new SessionUtil(session);
        clinic = sessionUtil.get(Clinic.class, centerId, new Clinic());
        clinic.setSendsShipments(sendsShipments);

        saveContacts();

        return run(user, session, sessionUtil, clinic);
    }

    // TODO: do not allow delete of a contact linked to a study
    private void saveContacts() {
        Set<Contact> newContactCollection = new HashSet<Contact>();
        for (ContactSaveInfo contactSaveInfo : contactSaveInfos) {
            Contact contact;
            if (contactSaveInfo.id == null) {
                contact = new Contact();
            } else {
                contact =
                    ActionUtil.sessionGet(session, Contact.class,
                        contactSaveInfo.id);
            }
            contact = contactSaveInfo.populateContcat(clinic, contact);
        }

        // delete contacts no longer in use
        SetDifference<Contact> contactsDiff =
            new SetDifference<Contact>(
                clinic.getContactCollection(), newContactCollection);
        clinic.setContactCollection(contactsDiff.getAddSet());
        for (Contact contact : contactsDiff.getRemoveSet()) {
            if (!contact.getStudyCollection().isEmpty()) {
                throw new ActionException("canot delete contact "
                    + contact.getName());
            }
            session.delete(contact);
        }
    }

}
