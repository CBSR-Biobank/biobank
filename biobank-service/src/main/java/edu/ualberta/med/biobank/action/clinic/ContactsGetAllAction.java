package edu.ualberta.med.biobank.action.clinic;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.model.Contact;

public class ContactsGetAllAction implements Action<ListResult<Contact>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String QRY_CONTACTS =
        "FROM " + Contact.class.getName() + " contact"
            + " INNER JOIN FETCH contact.clinic clinic"
            + " INNER JOIN FETCH clinic.contacts";

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ClinicReadPermission().isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Contact> run(ActionContext context)
        throws ActionException {
        Query query = context.getSession().createQuery(QRY_CONTACTS);
        return new ListResult<Contact>(query.list());
    }
}
