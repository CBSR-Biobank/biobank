package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

/**
 * This action returns all contacts in the database.
 * 
 * NOTE! Not the best way to do it if there are many clinics.
 * 
 */
public class ContactsGetAllAction implements Action<ListResult<Contact>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ClinicReadPermission().isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Contact> run(ActionContext context)
        throws ActionException {
        List<Contact> contacts = new ArrayList<Contact>();
        List<Clinic> clinics = context.getSession().createCriteria(Clinic.class).list();

        // GUI requires that for each clinic, all contacts are initialized
        for (Clinic clinic : clinics) {
            for (Contact contact : clinic.getContacts()) {
                contact.getName();
                contacts.add(contact);
            }
        }

        return new ListResult<Contact>(contacts);
    }
}
