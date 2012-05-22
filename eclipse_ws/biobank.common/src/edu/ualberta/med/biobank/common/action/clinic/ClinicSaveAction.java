package edu.ualberta.med.biobank.common.action.clinic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;

public class ClinicSaveAction extends CenterSaveAction {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr CONTACT_HAS_STUDIES_ERRMSG =
        bundle.tr("Cannot delete contact {0} because it is still associated" +
            " with other studies.");

    // This info class does not support the Contact <-> Study association
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
        }

        public Contact populateContact(Clinic clinic, Contact contact) {
            contact.setClinic(clinic);
            contact.setId(this.id);
            contact.setName(this.name);
            contact.setTitle(this.title);
            contact.setMobileNumber(this.mobileNumber);
            contact.setFaxNumber(this.faxNumber);
            contact.setPagerNumber(this.pagerNumber);
            contact.setOfficeNumber(this.officeNumber);
            contact.setEmailAddress(this.emailAddress);
            return contact;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private Boolean sendsShipments;
    private Collection<ContactSaveInfo> contactSaveInfos;
    private Clinic clinic = null;

    public void setSendsShipments(Boolean sendsShipments) {
        this.sendsShipments = sendsShipments;
    }

    public void setContactSaveInfos(Collection<ContactSaveInfo> contactSaveInfos) {
        this.contactSaveInfos = contactSaveInfos;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Permission permission;
        if (centerId == null)
            permission = new ClinicCreatePermission();
        else
            permission = new ClinicUpdatePermission(centerId);
        return permission.isAllowed(context);
    }

    /**
     * Contacts cannot be deleted if it is still associated with a study.
     */
    @Override
    public IdResult run(ActionContext context) throws ActionException {
        clinic = context.load(Clinic.class, centerId, new Clinic());
        clinic.setSendsShipments(sendsShipments);

        saveContacts(context);

        return run(context, clinic);
    }

    // TODO: do not allow delete of a contact linked to a study
    private void saveContacts(ActionContext context) {
        Set<Contact> newContactCollection = new HashSet<Contact>();
        for (ContactSaveInfo contactSaveInfo : contactSaveInfos) {
            Contact contact;
            if (contactSaveInfo.id == null) {
                contact = new Contact();
            } else {
                contact = context.load(Contact.class, contactSaveInfo.id);
            }
            newContactCollection.add(contactSaveInfo.populateContact(clinic,
                contact));
        }

        // delete contacts no longer in use
        SetDifference<Contact> contactsDiff =
            new SetDifference<Contact>(
                clinic.getContacts(), newContactCollection);
        clinic.setContacts(contactsDiff.getNewSet());
        for (Contact contact : contactsDiff.getRemoveSet()) {
            Collection<Study> studyCollection = contact.getStudies();
            if ((studyCollection != null) && !studyCollection.isEmpty()) {
                throw new ActionException(CONTACT_HAS_STUDIES_ERRMSG
                    .format(contact.getName()));
            }
            context.getSession().delete(contact);
        }
    }
}
